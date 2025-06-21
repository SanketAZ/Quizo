package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.QuestionCreateResDTO;
import org.sxy.optimus.dto.QuestionRequestDTO;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.exception.ValidationException;
import org.sxy.optimus.mapper.QuestionMapper;
import org.sxy.optimus.module.Option;
import org.sxy.optimus.module.Question;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.repo.QuestionRepo;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private QuizService quizService;

    @Autowired
    private ValidationService validationService;

    private final QuestionMapper questionMapper;



    public QuestionService(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    //Method will add the question to the provided quiz
    public List<QuestionCreateResDTO> addQuestionsToQuiz(UUID userId, UUID quizId, List<QuestionRequestDTO> questions) {

        //validation of the questions list
        if(!validationService.validateQuestionRequestDTOs(questions).isEmpty()){
            throw new ValidationException("Validation failed for one or more questions",validationService.validateQuestionRequestDTOs(questions));
        }

        //fetch the quiz
        Quiz quiz=quizService.getQuiz(quizId);

        if(!quiz.getCreatorUserId().equals(userId)) {
            throw new UnauthorizedActionException("User with id "+userId +"is not authorized to update the quiz");
        }

        //Mapping to get list of Quest ions
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

        log.info("Questions added to quiz :{}",quiz.getQuizId());

        return questionMapper.toQuestionCreateResDTOList(savedQuestions);
    }

}
