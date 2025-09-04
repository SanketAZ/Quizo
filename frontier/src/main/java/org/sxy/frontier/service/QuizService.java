package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.option.OptionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.exception.InvalidSubmissionException;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.mapper.QuizMapper;
import org.sxy.frontier.redis.QuizCacheRepo;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);
    @Autowired
    private QuizCacheRepo quizCacheRepo;
    @Autowired
    private QuizMapper quizMapper;
    @Autowired
    private OptimusServiceClient optimusServiceClient;

    public ActiveQuizQuestionDTO fetchActiveQuizQuestion(UUID roomId, UUID quizId, UUID questionID){
        Optional<QuestionCacheDTO>questionCacheDTO=quizCacheRepo.getQuestion(roomId,quizId,questionID);
        if(questionCacheDTO.isPresent()){
            QuestionCacheDTO questionCache=questionCacheDTO.get();
            return quizMapper.toActiveQuizQuestionDTO(questionCache);
        }
        //cache miss, need to call the optimus service
        QuestionCacheDTO questionCache=optimusServiceClient.getQuestionCache(roomId,quizId,questionID);
        return quizMapper.toActiveQuizQuestionDTO(questionCache);
    }

    public AnswerSubmissionResDTO evaluateAnswer(UUID roomId, UUID quizId, AnswerSubmissionReqDTO answerSubmissionReqDTO){
       //get question details
        UUID questionID=UUID.fromString(answerSubmissionReqDTO.getQuestionId());
        UUID submittedOptionID=UUID.fromString(answerSubmissionReqDTO.getOptionId());

        Optional<QuestionCacheDTO> cached=quizCacheRepo.getQuestion(roomId,quizId,submittedOptionID);
        final QuestionCacheDTO questionCache;
        if (cached.isPresent()) {
            questionCache = cached.get();
            log.debug("Cache HIT for questionId={} in quizId={}", questionID, quizId);
        } else {
            log.debug("Cache MISS for questionId={} in quizId={} – fetching from Optimus", questionID, quizId);
            questionCache = optimusServiceClient.getQuestionCache(roomId, quizId, questionID);
        }
        QuestionDTO questionDTO=quizMapper.toQuestionDTO(questionCache);
        return checkAnswer(questionDTO,submittedOptionID,roomId,quizId);
    }

private AnswerSubmissionResDTO checkAnswer(QuestionDTO questionDTO,UUID submittedOptionId,UUID roomId,UUID quizId){
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

        AnswerSubmissionResDTO resDTO=new AnswerSubmissionResDTO();
        resDTO.setQuestionId(questionDTO.getQuestionId());
        resDTO.setOptionId(submittedOptionId.toString());
        resDTO.setCorrect(isCorrect);
        resDTO.setObtainedMarks(isCorrect ? questionDTO.getWeight() : 0);
        return resDTO;
    }
}
