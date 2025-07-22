package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.dto.PageResponse;
import org.sxy.optimus.dto.question.QuestionDTO;
import org.sxy.optimus.dto.quiz.*;
import org.sxy.optimus.exception.*;
import org.sxy.optimus.mapper.QuestionMapper;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.projection.QuestionWithOptionsProjection;
import org.sxy.optimus.repo.QuestionRepo;
import org.sxy.optimus.repo.QuizRepo;
import org.sxy.optimus.repo.RoomRepo;
import org.sxy.optimus.utility.PageRequestHelper;
import org.sxy.optimus.utility.PageRequestValidator;
import org.sxy.optimus.utility.QuizValidator;
import org.sxy.optimus.validation.ValidationResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class QuizService {

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private QuestionRepo questionRepo;

    private final QuizMapper quizMapper;

    private static final int MIN_BUFFER_SECONDS = 300;

    private static final int MIN_BUFFER_SECONDS_FOR_UPDATE = 600;

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);


    private final QuestionMapper questionMapper;

    public QuizService(QuizMapper quizMapper, QuestionMapper questionMapper) {
        this.quizMapper = quizMapper;
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

    //This method fetches the quizzes present in the room
    public PageResponse<QuizDisplayDTO> fetchOwnerQuizzesForRoom(UUID userID,String status,UUID roomID, PageRequestDTO pageRequestDTO){
        //validating the PageRequestDTO with valid order by fields
        List<ValidationResult> errors= PageRequestValidator.validatePageRequest(pageRequestDTO, List.of("createdAt","updatedAt"));
        if(!errors.isEmpty()){
            throw new ValidationException("Validation failed PageRequestDTO",errors);
        }

        //validating the status
        if(!QuizValidator.validateQuizStatus(status)){
            throw new ResourceDoesNotExitsException("Quiz","QuizStatus",status);
        }

        if(!roomRepo.existsById(roomID)){
            throw new ResourceDoesNotExitsException("Room","roomID",roomID.toString());
        }

        if(!roomRepo.existsByRoomIdAndOwnerUserId(roomID,userID)){
            throw new UnauthorizedActionException("User with id "+userID +"is not authorized to fetch the quizzes");
        }

        Pageable pageable= PageRequestHelper.toPageable(pageRequestDTO);

        //Getting Quiz page
        Page<Quiz> quizPage=quizRepo.getQuizDisplayDTOByRoomId(roomID, status,pageable);
        List<QuizDisplayDTO> quizDisplayDTOs=quizPage.getContent()
                .stream()
                .map(quizMapper::quizToQuizDisplayDTO).toList();

        log.info("User with id {} fetched the quizzes for room with id {}",userID,roomID);

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

}
