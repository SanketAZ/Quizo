package org.sxy.optimus.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.option.OptionRequestDTO;
import org.sxy.optimus.dto.option.OptionUpdateReqDTO;
import org.sxy.optimus.dto.question.QuestionCreateResDTO;
import org.sxy.optimus.dto.question.QuestionRequestDTO;
import org.sxy.optimus.dto.question.QuestionUpdateReqDTO;
import org.sxy.optimus.dto.question.QuestionUpdateResDTO;
import org.sxy.optimus.dto.quiz.QuizQuestionsAddResDTO;
import org.sxy.optimus.exception.QuestionDoesNotExistsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.exception.ValidationException;
import org.sxy.optimus.mapper.OptionMapper;
import org.sxy.optimus.mapper.QuestionMapper;
import org.sxy.optimus.module.Option;
import org.sxy.optimus.module.Question;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.repo.QuestionRepo;
import org.sxy.optimus.repo.QuizRepo;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private ValidationService validationService;

    private final QuestionMapper questionMapper;

    private final OptionMapper optionMapper;

    public QuestionService(QuestionMapper questionMapper, OptionMapper optionMapper) {
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
    }

    //Method will add the question to the provided quiz
    @Transactional
    public QuizQuestionsAddResDTO addQuestionsToQuiz(UUID userId, UUID quizId, List<QuestionRequestDTO> questions) {

        //validation of the question list
        if(!validationService.validateQuestionRequestDTOs(questions).isEmpty()){
            throw new ValidationException("Validation failed for one or more questions",validationService.validateQuestionRequestDTOs(questions));
        }

        //fetch the quiz
        Quiz quiz=quizService.getQuiz(quizId);

        if(!quiz.getCreatorUserId().equals(userId)) {
            throw new UnauthorizedActionException("User with id "+userId +"is not authorized to update the quiz");
        }

        //Mapping to get a list of Quest ions
        List<Question> questionToSave=questionMapper.toQuestionList(questions);

        //Imp step
        for(Question question:questionToSave) {
            question.setQuiz(quiz);

            for (Option option : question.getOptions()) {
                option.setQuestion(question);
            }
        }

        //Saving the questions
        List<Question> savedQuestions=questionRepo.saveAll(questionToSave);

        //saving the number of questions
        int numOfQuestions=questionRepo.countByQuizId(quizId);
        quiz.setQuestionCount(numOfQuestions);
        quizRepo.save(quiz);

        log.info("Questions added to quiz :{}",quiz.getQuizId());

        //Generating the response dto
        QuizQuestionsAddResDTO respDTO=new QuizQuestionsAddResDTO();
        respDTO.setTotalQuestions(numOfQuestions);
        respDTO.setTotalQuestionsAddedNow(savedQuestions.size());
        respDTO.setQuestionsAdded(questionMapper.toQuestionCreateResDTOList(savedQuestions));

        return respDTO;
    }

    public QuestionUpdateResDTO updateQuestion(UUID userId, UUID questionId, QuestionUpdateReqDTO questionUpdateReqDTO) {

        //validation of the question
        if(!validationService.validateQuestionUpdateReqDTOs(List.of(questionUpdateReqDTO)).isEmpty()){
            throw new ValidationException("Validation failed for one or more questions",validationService.validateQuestionUpdateReqDTOs(List.of(questionUpdateReqDTO)));
        }

        //fetch the Question
        Question question=questionRepo.findQuestionByQuestionId(questionId);

        //checking question is present or not
        if(question==null){
            throw new QuestionDoesNotExistsException("QuestionId",questionId.toString());
        }

        //validating user has access to the quiz
        if(!question.getQuiz().getCreatorUserId().equals(userId)){
            throw new UnauthorizedActionException("User with id "+userId +"is not authorized to update the question");
        }

        //update question fields
        question.setWeight(questionUpdateReqDTO.getWeight());
        question.setText(questionUpdateReqDTO.getText());

        //Updating existing options
        Map<UUID,Option> existingOptions=question.getOptions().stream()
                .collect(Collectors.toMap(Option::getOptionId, o -> o));


        Set<UUID>incomingIds=new HashSet<>();
        for(OptionUpdateReqDTO optionUpdateReqDTO:questionUpdateReqDTO.getOptions()) {
            UUID optId= UUID.fromString(optionUpdateReqDTO.getOptionId());
            incomingIds.add(optId);

            Option existingOption=existingOptions.get(optId);
            if(existingOption!=null){
                existingOption.setIsCorrect(optionUpdateReqDTO.getIsCorrect());
                existingOption.setText(optionUpdateReqDTO.getText());
            }
        }

        //removing options which are not present in request
        question.getOptions().removeIf(o->!incomingIds.contains(o.getOptionId()));

        //add newly created options to question
        for(OptionRequestDTO optionRequestDTO:questionUpdateReqDTO.getNewOptions()) {
            Option newOption=new Option();
            newOption.setText(optionRequestDTO.getText());
            newOption.setIsCorrect(optionRequestDTO.getIsCorrect());
            newOption.setQuestion(question);
            question.getOptions().add(newOption);
        }

        Question updatedQuestion=questionRepo.save(question);

        log.info("Question updated:{}",updatedQuestion);

        return questionMapper.toQuestionUpdateResDTO(updatedQuestion);
    }

}
