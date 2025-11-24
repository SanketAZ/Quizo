package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.dto.option.OptionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.exception.InvalidSubmissionException;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.mapper.QuizMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);
    @Autowired
    private QuizMapper quizMapper;
    @Autowired
    private QuizDataService quizDataService;

    public ActiveQuizQuestionDTO fetchActiveQuizQuestion(UUID roomId, UUID quizId, UUID questionID){
        log.info("Fetching ActiveQuizQuestion: roomId={}, quizId={}, questionId={}",
                roomId, quizId, questionID);
        QuestionDTO questionDTO= quizDataService.getQuestion(roomId,quizId,questionID);
        return quizMapper.toActiveQuizQuestionDTO(questionDTO);
    }

    public AnswerEvaluation evaluateAnswer(UUID roomId, UUID quizId, AnswerSubmissionReqDTO answerSubmissionReqDTO){
       //get question details
        UUID questionID=UUID.fromString(answerSubmissionReqDTO.getQuestionId());
        UUID submittedOptionID=UUID.fromString(answerSubmissionReqDTO.getOptionId());

        QuestionDTO questionDTO= quizDataService.getQuestion(roomId,quizId,questionID);
        return checkAnswer(questionDTO,submittedOptionID,roomId,quizId);
    }

    private AnswerEvaluation checkAnswer(QuestionDTO questionDTO,UUID submittedOptionId,UUID roomId,UUID quizId){
        List<OptionDTO> options=questionDTO.getOptions();
        if(options==null || options.isEmpty()){
            log.warn("Question {} has no options configured (roomId={}, quizId={})",
                    questionDTO.getQuestionId(), roomId, quizId);

            String msg=String.format("Question %s has no options configured",questionDTO.getQuestionId());
            throw new ResourceDoesNotExitsException(msg);
        }
        Set<UUID> validOptionIds=options.stream()
                .map(optionDTO ->  UUID.fromString(optionDTO.getOptionId()))
                .collect(Collectors.toUnmodifiableSet());

        if(!validOptionIds.contains(submittedOptionId)){
            log.warn("Invalid submission: optionId={} not part of questionId={} (roomId={}, quizId={})",
                    submittedOptionId, questionDTO.getQuestionId(), roomId, quizId);

            String msg=String.format("Question %s has no option with %s configured",questionDTO.getQuestionId(),submittedOptionId);
            throw new InvalidSubmissionException(msg);
        }
        Optional<UUID>correctOptionId=options.stream()
                .filter(o->Boolean.TRUE.equals(o.getIsCorrect()))
                .map(optionDTO ->  UUID.fromString(optionDTO.getOptionId()))
                .findFirst();

        if(correctOptionId.isEmpty()){
            log.warn("No correct option configured for questionId={} (roomId={}, quizId={})",
                    questionDTO.getQuestionId(), roomId, quizId);

            String msg=String.format("No correct option configured for the Question %s",questionDTO.getQuestionId());
            throw new ResourceDoesNotExitsException(msg);
        }

        boolean isCorrect=correctOptionId.get().equals(submittedOptionId);
        log.debug("Answer checked for questionId={} (isCorrect={})", questionDTO.getQuestionId(), isCorrect);

        return AnswerEvaluation.builder()
                .questionId(questionDTO.getQuestionId())
                .submittedOptionId(submittedOptionId.toString())
                .correct(isCorrect)
                .obtainedMarks(isCorrect ? questionDTO.getWeight() : 0)
                .build();
    }
}
