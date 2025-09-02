package org.sxy.optimus.redis;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.sxy.optimus.dto.question.QuestionCacheDTO;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.dto.quiz.QuizDetailCacheDTO;
import org.sxy.optimus.utility.redis.RedisKeys;


import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Validated
public class RedisCacheQuizRepository {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheQuizRepository.class);

    private final RedisTemplate<String, QuestionCacheDTO> redisTemplate;
    private final RedisTemplate<String, QuizDetailCacheDTO> redisTemplateQuizDetail;
    private final RedisTemplate<String, String> redisTemplateString;


    public RedisCacheQuizRepository(@Qualifier("String-QuestionCacheDTO") RedisTemplate<String, QuestionCacheDTO> redisTemplate, @Qualifier("String-QuizDetailCacheDTO")RedisTemplate<String, QuizDetailCacheDTO> redisDetailTemplate, @Qualifier("String-String")RedisTemplate<String, String> redisTemplateString) {
        this.redisTemplate = redisTemplate;
        this.redisTemplateQuizDetail = redisDetailTemplate;
        this.redisTemplateString = redisTemplateString;
    }

    public boolean isQuizPreloaded(UUID quizId,UUID roomId,String sequenceLabel) {
        String key1= RedisKeys.buildQuizDetailKey(quizId.toString(),roomId.toString());
        String Key2= RedisKeys.buildQuizQuestionsKey(quizId.toString(),roomId.toString());
        String Key3= RedisKeys.buildQuizSequenceKey(quizId.toString(),roomId.toString(),sequenceLabel);;

        return redisTemplateQuizDetail.hasKey(key1) && redisTemplate.hasKey(Key2) && redisTemplate.hasKey(Key3);
    }

    //This method is to upload the quiz detail to redis
    public void uploadQuizDetails(@NotNull QuizDetailCacheDTO quizDetailCacheDTO,@Min(1) Long ttlInSeconds)  throws Exception{
        String key= RedisKeys.buildQuizDetailKey(quizDetailCacheDTO.getQuizId(),quizDetailCacheDTO.getRoomId());
        long ttl= Optional.ofNullable(ttlInSeconds).orElse(3600L);

        try {
            redisTemplateQuizDetail.opsForValue().set(key, quizDetailCacheDTO);
            redisTemplateQuizDetail.expire(key, Duration.ofSeconds(ttl));
        }catch (Exception e){
            logger.error("Failed to upload quiz details to Redis for quizID: {}", quizDetailCacheDTO.getQuizId(),e);
            throw e;
        }

        logger.info("Uploaded quiz details to Redis for quizID: {} with TTL: {} seconds. Redis key: {}",
                quizDetailCacheDTO.getQuizId(), ttlInSeconds, key);
    }

    //This method is used to upload the quiz question to redis using hash
    public void uploadQuizQuestions(@NotEmpty List<QuestionCacheDTO>questions, @NotNull UUID quizID,@NotNull UUID roomId,@Min(1) Long ttlInSeconds) throws Exception{

        String Key= RedisKeys.buildQuizQuestionsKey(quizID.toString(),roomId.toString());
        long ttl= Optional.ofNullable(ttlInSeconds).orElse(3600L);

        Map<String, QuestionCacheDTO> mapOfQuestionsQuestions = mapQuestionsById(questions);

        try{
            redisTemplate.opsForHash().putAll(Key,mapOfQuestionsQuestions);
            redisTemplate.expire(Key, Duration.ofSeconds(ttl));
        }catch (Exception e){
            logger.error("Failed to upload quiz questions to Redis for quizID: {}", quizID, e);
            throw e;
        }

        logger.info("Uploaded {} questions to Redis for quizID: {} with TTL: {} seconds. Redis key: {}",
                questions.size(), quizID, ttlInSeconds, Key);
    }

    public void uploadQuizQuestionSequence(@NotEmpty List<QuestionPositionDTO>questionPositionList,String sequenceLabel,UUID quizID, UUID roomID, @Min(1) Long ttlInSeconds) throws Exception{
        String Key= RedisKeys.buildQuizSequenceKey(quizID.toString(),roomID.toString(),sequenceLabel);
        long ttl= Optional.ofNullable(ttlInSeconds).orElse(3600L);

        Map<String,String> questionPositionMap=questionPositionList.stream()
                .collect(Collectors.toMap(questionPositionDTO -> questionPositionDTO.getPosition().toString(),QuestionPositionDTO::getQuestionId));

        try{
            redisTemplateString.opsForHash().putAll(Key,questionPositionMap);
            redisTemplateString.expire(Key, Duration.ofSeconds(ttl));
        }catch (Exception e){
            logger.error("Failed to upload quiz question sequence to Redis for quizID: {}", quizID, e);
            throw e;
        }

        logger.info("Uploaded {} question sequence to Redis for quizID: {} with TTL: {} seconds. Redis key: {}",
                questionPositionList.size(), quizID, ttlInSeconds, Key);

    }

    public List<QuestionPositionDTO> getQuizQuestionSequence(String sequenceLabel,UUID quizID, UUID roomID){
        String Key= RedisKeys.buildQuizSequenceKey(quizID.toString(),roomID.toString(),sequenceLabel);
        HashOperations<String, String, String> hashOp=redisTemplateString.opsForHash();
        Map<String, String> questionPositionMap;
        try {
            questionPositionMap = hashOp.entries(Key);
        } catch (Exception e){
            logger.error("Failed to get quiz question sequence to Redis for quizID: {}", quizID, e);
            throw e;
        }

        return questionPositionMap.entrySet()
                .stream()
                .map(entry ->new QuestionPositionDTO(entry.getValue(), Integer.valueOf(entry.getKey())))
                .toList();
    }
    public Optional<QuizDetailCacheDTO> getQuizDetails(UUID roomId, UUID quizId) {
        String key= RedisKeys.buildQuizDetailKey(quizId.toString(),roomId.toString());
        QuizDetailCacheDTO quizDetailCacheDTO = redisTemplateQuizDetail.opsForValue().get(key);
        return Optional.ofNullable(quizDetailCacheDTO);
    }

    public Optional<QuestionCacheDTO> getQuestion(UUID roomId, UUID quizId, UUID questionId) {
        String key= RedisKeys.buildQuizQuestionsKey(quizId.toString(),roomId.toString());
        QuestionCacheDTO questionCacheDTO= (QuestionCacheDTO) redisTemplate.opsForHash().get(key,questionId.toString());
        return Optional.ofNullable(questionCacheDTO);
    }

    private Map<String,QuestionCacheDTO> mapQuestionsById(@NotEmpty List<QuestionCacheDTO>questions) {
        return questions.stream()
                .collect(Collectors.toMap(QuestionCacheDTO::getQuestionId, Function.identity()));
    }

    private String formKey(List<String>segments){
        return String.join(":", segments);
    }
}
