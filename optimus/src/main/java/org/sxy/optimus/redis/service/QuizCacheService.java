package org.sxy.optimus.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.question.QuestionDTO;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.dto.quiz.QuizDetailDTO;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.mapper.QuestionMapper;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.module.Question;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.module.compKey.RoomQuizId;
import org.sxy.optimus.redis.dto.QuestionCacheDTO;
import org.sxy.optimus.redis.dto.QuizDetailCacheDTO;
import org.sxy.optimus.redis.repo.QuizCacheRepository;
import org.sxy.optimus.repo.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizCacheService {

    private static final Logger log = LoggerFactory.getLogger(QuizCacheService.class);
    private static final String DEFAULT_SEQUENCE_LABEL = "A";
    private static final Duration MIN_TTL = Duration.ofMinutes(1);
    private static final Duration BUFFER = Duration.ofMinutes(5);

    @Autowired
    private QuizMapper quizMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuizCacheRepository quizCacheRepository;


    public Optional<QuizDetailCacheDTO> getQuizDetailCache(UUID roomId, UUID quizId){
        return quizCacheRepository.getQuizDetails(roomId, quizId);
    }

    public QuizDetailCacheDTO cacheQuizDetail(QuizDetailDTO quizDetailDTO){
        QuizDetailCacheDTO quizDetailCacheDTO = quizMapper.toQuizDetailCacheDTO(quizDetailDTO);
        Duration ttl =getTTLForQuiz(quizDetailDTO);
        try {
            quizCacheRepository.uploadQuizDetails(quizDetailCacheDTO,ttl);
            log.debug("Cache successfully the Quiz Detail: quizId={}, ttl={}s",
                    quizDetailCacheDTO.getQuizId(),
                    ttl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to cache the Quiz Detail: quizId={}, ttl={}s - operation=uploadQuizDetails, error={}",
                    quizDetailCacheDTO.getQuizId(),
                    ttl.getSeconds(),
                    e.getClass().getSimpleName(),
                    e);
        }
        return  quizDetailCacheDTO;
    }

    private QuizDetailCacheDTO warmQuizDetailCache(QuizDetailDTO quizDetailDTO){
        return  cacheQuizDetail(quizDetailDTO);
    }

    public List<QuestionPositionDTO> getQuestionPositionCache(String label, UUID roomId, UUID quizId){
        return quizCacheRepository.getQuizQuestionSequence(label,quizId,roomId);
    }

    public List<QuestionPositionDTO> cacheQuizQuestionSequence(List<QuestionPositionDTO> questionPositionList,String seqLabel,UUID quizId,UUID roomId){
        Duration ttl = Duration.ofSeconds(900);
        try {
            quizCacheRepository.uploadQuizQuestionSequence(questionPositionList,seqLabel,quizId,roomId,ttl);
            log.debug("Cache warmed successfully for quiz question sequence: " +
                            "quizId={}, roomId={}, seqLabel={}, questionCount={}, ttl={}s",
                    quizId,
                    roomId,
                    seqLabel,
                    questionPositionList.size(),
                    ttl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to warm cache for quiz question sequence: " +
                            "quizId={}, roomId={}, seqLabel={}, questionCount={}, ttl={}s - error={}",
                    quizId,
                    roomId,
                    seqLabel,
                    questionPositionList.size(),
                    ttl.getSeconds(),
                    e.getMessage(),
                    e);
        }
        return  questionPositionList;
    }

    private List<QuestionPositionDTO> warmQuizQuestionSequenceCache(List<QuestionPositionDTO> questionPositionList,String seqLabel,UUID quizId,UUID roomId){
        return  cacheQuizQuestionSequence(questionPositionList,seqLabel,quizId,roomId);
    }

    public Optional<QuestionCacheDTO> getQuestionCacheDTO(UUID roomId, UUID quizId, UUID questionId){
        return quizCacheRepository.getQuestion(roomId,quizId,questionId);
    }

    public QuestionCacheDTO cacheQuestion(QuestionDTO questionDTO,UUID quizId, UUID roomId){
        QuestionCacheDTO questionCacheDTO=questionMapper.toQuestionCacheDTO(questionDTO);
        Duration ttl = Duration.ofSeconds(900);
        List<QuestionCacheDTO> questionCacheDTOList=List.of(questionCacheDTO);
        try {
            quizCacheRepository.uploadQuizQuestions(questionCacheDTOList,quizId,roomId,ttl);
            log.debug("Cache warmed successfully for single question: " +
                            "quizId={}, roomId={}, questionId={}, ttl={}s",
                    quizId,
                    roomId,
                    questionCacheDTO.getQuestionId(),
                    ttl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to warm cache for single question: " +
                            "quizId={}, roomId={}, questionId={}, ttl={}s - error={}",
                    quizId,
                    roomId,
                    questionCacheDTO.getQuestionId(),
                    ttl.getSeconds(),
                    e.getMessage(),
                    e);
        }
        return questionCacheDTO;
    }

    private QuestionCacheDTO warmQuestionCache(QuestionDTO questionDTO,UUID quizId, UUID roomId){
        return cacheQuestion(questionDTO,quizId,roomId);
    }


    private Duration getTTLForQuiz(QuizDetailDTO dto) {
        Instant now = Instant.now();
        Instant quizEnd = dto.getStartTime().plus(Duration.ofSeconds(dto.getDurationSec()));

        Duration timeUntilEnd = Duration.between(now, quizEnd);
        if (timeUntilEnd.isNegative()) {
            log.warn("Quiz {} already ended, using minimum TTL", dto.getQuizId());
            return MIN_TTL;
        }

        Duration ttl = timeUntilEnd.plus(BUFFER);
        return ttl.compareTo(MIN_TTL) < 0 ? MIN_TTL : ttl;
    }

}

