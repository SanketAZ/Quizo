package org.sxy.optimus.service;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.dto.PageResponse;
import org.sxy.optimus.dto.question.*;
import org.sxy.optimus.dto.quiz.*;
import org.sxy.optimus.exception.*;
import org.sxy.optimus.mapper.QuestionMapper;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.mapper.QuizQuestionSequenceMapper;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.module.QuizQuestionSequence;
import org.sxy.optimus.projection.QuestionWithOptionsProjection;
import org.sxy.optimus.repo.*;
import org.sxy.optimus.specifications.QuizSpecifications;
import org.sxy.optimus.utility.PageRequestHelper;
import org.sxy.optimus.utility.PageRequestValidator;
import org.sxy.optimus.utility.QuizValidator;
import org.sxy.optimus.validation.ValidationResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class QuizService {

    @Autowired
    private QuizRepo quizRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private QuestionRepo questionRepo;
    @Autowired
    private QuizQuestionSequenceRepo quizQuestionSequenceRepo;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private QuizDataService quizDataService;
    @Autowired
    private AccessControlService accessControlService;

    private final QuizMapper quizMapper;

    private final QuizQuestionSequenceMapper questionSequenceMapper;

    private static final int MIN_BUFFER_SECONDS = 300;

    private static final int MIN_BUFFER_SECONDS_FOR_UPDATE = 600;

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);


    private final QuestionMapper questionMapper;

    public QuizService(QuizMapper quizMapper, QuizQuestionSequenceMapper quizQuestionMapper, QuestionMapper questionMapper) {
        this.quizMapper = quizMapper;
        this.questionSequenceMapper = quizQuestionMapper;
        this.questionMapper = questionMapper;
    }

    //quiz creation method
    public QuizCreatedDTO createQuiz(QuizCreateDTO quizCreateDTO){
       quizCreateDTO.setQuestionCount(0);
       Quiz quiz=quizMapper.toQuiz(quizCreateDTO);
       Quiz createdQuiz=quizRepo.save(quiz);

       log.info("New Quiz created :{}",createdQuiz);
       return quizMapper.toQuizCreateDTO(createdQuiz);
    }

    //quiz details update
    public QuizUpdateResponseDTO updateQuiz(String quizID,QuizUpdateRequestDTO quizUpdateRequestDTO){

        Quiz quiz=quizRepo.findById(UUID.fromString(quizID))
                .orElseThrow(() -> new QuizDoesNotExistsException("quizId",quizID));

        QuizValidator.assertCanUpdateBeforeStart(quiz.getStartTime(),Instant.now(),MIN_BUFFER_SECONDS_FOR_UPDATE);

        if(!quiz.getCreatorUserId().toString().equals(quizUpdateRequestDTO.getCreatorUserId())){
            throw new UnauthorizedActionException("User with id "+quizUpdateRequestDTO.getCreatorUserId() +"is not authorized to update this quiz");
        }

        log.info("Quiz before update :{}",quiz);
        quiz.setDescription(quizUpdateRequestDTO.getDescription());
        quiz.setDurationSec(quizUpdateRequestDTO.getDurationSec());
        quiz.setTitle(quizUpdateRequestDTO.getTitle());
        Quiz updatedQuiz=quizRepo.save(quiz);
        log.info("Quiz updated :{}",updatedQuiz);

        return quizMapper.toQuizUpdateResponseDTO(updatedQuiz);
    }

    public Quiz getQuiz(UUID quizID){

        Quiz quiz=quizRepo.findById(quizID)
                .orElseThrow(() -> new QuizDoesNotExistsException("quizId",quizID.toString()));
        return quiz;
    }

    @Transactional(readOnly=true)
    public PageResponse<QuizDisplayDTO> fetchOwnedQuizzes(UUID userID, @Nullable UUID roomId, String status, PageRequestDTO pageRequestDTO){
        List<ValidationResult> errors= PageRequestValidator.validatePageRequest(pageRequestDTO, List.of("createdAt","updatedAt"));
        if(!errors.isEmpty()){
            throw new ValidationException("Validation failed PageRequestDTO",errors);
        }

        if(!QuizValidator.validateQuizStatus(status)){
            throw new ResourceDoesNotExitsException("Quiz","QuizStatus",status);
        }

        accessControlService.validateRoomAccess(userID,roomId);

        //build specifications
        Specification<Quiz> spec = QuizSpecifications.hasOwnerId(userID)
                .and(QuizSpecifications.hasStatus(status));
        if(roomId!=null){
            spec = spec.and(QuizSpecifications.hasRoomId(roomId));
        }

        Pageable pageable= PageRequestHelper.toPageable(pageRequestDTO);

        Page<Quiz> quizPage=quizRepo.findAll(spec,pageable);
        List<QuizDisplayDTO> quizDisplayDTOs=quizPage.getContent()
                .stream()
                .map(quizMapper::quizToQuizDisplayDTO).toList();

        log.info("User {} fetched {} quizzes (roomId={}, status={})",
                userID, quizDisplayDTOs.size(), roomId, status);

        return PageResponse.of(quizDisplayDTOs,quizPage);
    }

    @Transactional(readOnly=true)
    public PageResponse<QuizDisplayDTO> fetchJoinedQuizzes(UUID userId, @Nullable UUID roomId, String status, PageRequestDTO pageRequestDTO){
        List<ValidationResult> errors= PageRequestValidator.validatePageRequest(pageRequestDTO, List.of("createdAt","updatedAt","startTime"));
        if(!errors.isEmpty()){
            throw new ValidationException("Validation failed PageRequestDTO",errors);
        }

        if(!QuizValidator.validateQuizStatus(status)){
            throw new ResourceDoesNotExitsException("Quiz","QuizStatus",status);
        }

        Specification<Quiz> spec = QuizSpecifications.userIsRoomParticipant(userId)
                .and(QuizSpecifications.hasStatus(status))
                .and(QuizSpecifications.notOwnedBy(userId));

        if (roomId != null) {
            accessControlService.validateRoomMembership(userId, roomId);
            spec = spec.and(QuizSpecifications.hasRoomId(roomId));
        }

        Pageable pageable= PageRequestHelper.toPageable(pageRequestDTO);

        Page<Quiz> quizPage=quizRepo.findAll(spec,pageable);
        List<QuizDisplayDTO> quizDisplayDTOs=quizPage.getContent()
                .stream()
                .map(quizMapper::quizToQuizDisplayDTO).toList();

        log.info("User {} fetched {} quizzes (roomId={}, status={})", userId, quizDisplayDTOs.size(), roomId, status);

        return PageResponse.of(quizDisplayDTOs,quizPage);
    }

    public PageResponse<QuestionDTO> getQuizQuestionsForOwner(UUID userId, UUID quizID, PageRequestDTO pageRequestDTO){
        //validating the PageRequestDTO with valid order by fields
        List<ValidationResult> errors= PageRequestValidator.validatePageRequest(pageRequestDTO, List.of("createdAt"));
        if(!errors.isEmpty()){
            throw new ValidationException("Validation failed PageRequestDTO",errors);
        }

        if(!quizRepo.existsById(quizID)){
            throw new ResourceDoesNotExitsException("Quiz","QuizID",quizID.toString());
        }

        if(!quizRepo.existsByQuizIdAndCreatorUserId(quizID,userId)){
            throw new UnauthorizedActionException("User with id "+userId +"is not authorized to fetch the questions");
        }

        Pageable pageable=PageRequestHelper.toPageable(pageRequestDTO);

        Page<QuestionWithOptionsProjection> questionPage=questionRepo.findQuestionWithOptionsByQuizId(quizID,pageable);

        List<QuestionDTO> questionDTOList=questionPage.getContent().stream()
                                .map(questionMapper::toQuestionDTO)
                                .toList();

        log.info("User with id {} fetched the questions for quiz with id {}",userId,quizID);

        return PageResponse.of(questionDTOList,questionPage);
    }

    //This method is to assign the quiz Start time
    public QuizStartTimeResDTO assignStartTimeToQuiz(UUID quizID,QuizStartTimeReqDTO quizStartTimeReqDTO){
        UUID userID=UUID.fromString(quizStartTimeReqDTO.getCreatorUserId());
        Quiz quiz=getQuiz(quizID);

        if(!quizRepo.existsByQuizIdAndCreatorUserId(quizID,userID)){
            throw new UnauthorizedActionException("User with id "+userID +"is not authorized to set the start time for quiz: "+quizID);
        }

        String startTimeStr = quizStartTimeReqDTO.getStartTime();
        Instant startTime = null;
        Instant currentTime = Instant.now();

        if(startTimeStr!=null && !startTimeStr.isBlank()){
            startTime = Instant.parse(startTimeStr);
            QuizValidator.assertValidStartTime(currentTime,startTime,MIN_BUFFER_SECONDS);
        }

        //setting start time
        quiz.setStartTime(startTime);
        Quiz savedQuiz=quizRepo.save(quiz);

        log.info("Successfully updated start time for quizId={} to startTime={}", quizID, startTime);

        return quizMapper.toQuizStartTimeResDTO(savedQuiz);
    }

    //This method is to update the sequence of questions in given quiz
    @Transactional
    public  QuizQuestionSequenceDTO updateQuizQuestionSequence(UUID userID, UUID quizID, QuizQuestionSequenceDTO reqDTO){
        quizQuestionSequenceRepo.deferConstraints();//*
        if(!quizRepo.existsByQuizIdAndCreatorUserId(quizID,userID)){
            throw new UnauthorizedActionException("User with id "+userID +"is not authorized to make updates for quiz: "+quizID);
        }

        List<UUID> questionIds = questionRepo.findQuestionIdsByQuizId(quizID);

        //validating the QuizQuestionSequenceDTO given to update the sequence of the questions
        List<ValidationResult> errors = validationService.validateQuizQuestionSequenceDTO(reqDTO,questionIds);
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }

        //Map to find out position with question id
        Map<String, Integer> questionPositionMap = reqDTO.getQuestionsPositions()
                .stream()
                .collect(Collectors.toMap(QuestionPositionDTO::getQuestionId, QuestionPositionDTO::getPosition));

        //updating the sequence and saving
        List<QuizQuestionSequence> existingQuestionSequence=quizQuestionSequenceRepo.findByQuizId(quizID);

        for (QuizQuestionSequence qs : existingQuestionSequence) {
            qs.setPosition(questionPositionMap.get(qs.getQuestionId().toString()));
        }

        List<QuestionPositionDTO> savedRes=quizQuestionSequenceRepo.saveAll(existingQuestionSequence).stream()
                .map(questionSequenceMapper::toQuestionPositionDTO)
                .toList();
        log.info("Successfully updated sequence for {} questions in quiz {}", savedRes.size(), quizID);

        QuizQuestionSequenceDTO response=new QuizQuestionSequenceDTO();
        response.setQuestionsPositions(savedRes);
        return response;
    }

    @Transactional
    public QuestionDeleteResDTO deleteQuestions(UUID userID, UUID quizID, QuestionDeleteReqDTO questionDeleteReqDTO){
        quizQuestionSequenceRepo.deferConstraints();//*
        if(!quizRepo.existsById(quizID)){
            throw new ResourceDoesNotExitsException("Quiz","quizID",userID.toString());
        }
        //validate user access
        if(!quizRepo.existsByQuizIdAndCreatorUserId(quizID,userID)){
            throw new UnauthorizedActionException("User with id "+userID +"is not authorized to make updates for quiz: "+quizID);
        }

        //validate all questions belong the quiz
        List<UUID>idsReq=questionDeleteReqDTO.getQuestionIds()
                .stream()
                .map(UUID::fromString)
                .toList();
        List<UUID>existingIds=questionRepo.findQuestionIdsForQuiz(quizID);
        int deletedCount=questionDeleteReqDTO.getQuestionIds().size();

        List<ValidationResult> errors = validationService.validateQuestionIdsToDelete(idsReq,existingIds);
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }

        //delete the questions
        questionRepo.deleteByIdsAndQuizId(idsReq,quizID);

        //Update the question sequence
        List<QuizQuestionSequence> questionSequenceList=quizQuestionSequenceRepo.findAllByQuizOrderByPositionAsc(quizRepo.getReferenceById(quizID));

        int updatedPos=1;
        for (QuizQuestionSequence qs : questionSequenceList) {
            qs.setPosition(updatedPos);
            updatedPos++;
        }
        quizQuestionSequenceRepo.saveAll(questionSequenceList);

        log.info("Delete operation complete for quiz {}. Deleted: {}", quizID, deletedCount);

        return QuestionDeleteResDTO.builder()
                .questionIds(questionDeleteReqDTO.getQuestionIds())
                .deletedCount(deletedCount)
                .build();
    }

    @Transactional(readOnly = true)
    public QuizDetailDTO getQuizDetail(UUID roomId, UUID quizId){
        return quizDataService.getQuizDetail(roomId, quizId);
    }

    @Transactional(readOnly = true)
    public List<QuestionPositionDTO> getQuestionPosition(String label,UUID roomId, UUID quizId){
       return quizDataService.getQuestionPosition(label,roomId,quizId);
    }

    public QuestionDTO getQuestion(UUID roomId, UUID quizId,UUID questionId){
        return quizDataService.getQuestion(roomId, quizId, questionId);
    }
}
