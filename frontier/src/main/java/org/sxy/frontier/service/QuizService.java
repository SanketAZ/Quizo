package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.dto.option.OptionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.exception.InvalidSubmissionException;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.mapper.QuizMapper;
import org.sxy.frontier.module.Submission;
import org.sxy.frontier.redis.QuizCacheRepo;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;
import org.sxy.frontier.repo.SubmissionRepo;

import java.time.Instant;
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
        log.info("Fetching ActiveQuizQuestion: roomId={}, quizId={}, questionId={}",
                roomId, quizId, questionID);

        Optional<QuestionCacheDTO>questionCacheDTO=quizCacheRepo.getQuestion(roomId,quizId,questionID);
        if(questionCacheDTO.isPresent()){
            log.debug("Cache HIT for questionId={} in quizId={} roomId={}", questionID, quizId, roomId);

            QuestionCacheDTO questionCache=questionCacheDTO.get();
            log.info("Returning question from cache: questionId={}", questionID);
            return quizMapper.toActiveQuizQuestionDTO(questionCache);
        }
        log.warn("Cache MISS for questionId={} in quizId={} roomId={}. Fetching from Optimus service.",
                questionID, quizId, roomId);
        //cache miss, need to call the optimus service
        QuestionCacheDTO questionCache=optimusServiceClient.getQuestionCache(roomId,quizId,questionID);
        return quizMapper.toActiveQuizQuestionDTO(questionCache);
    }

    public AnswerEvaluation evaluateAnswer(UUID roomId, UUID quizId, AnswerSubmissionReqDTO answerSubmissionReqDTO){
       //get question details
        UUID questionID=UUID.fromString(answerSubmissionReqDTO.getQuestionId());
        UUID submittedOptionID=UUID.fromString(answerSubmissionReqDTO.getOptionId());

        Optional<QuestionCacheDTO> cached=quizCacheRepo.getQuestion(roomId,quizId,questionID);
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
