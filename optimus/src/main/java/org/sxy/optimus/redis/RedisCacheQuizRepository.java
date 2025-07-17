package org.sxy.optimus.redis;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.sxy.optimus.dto.question.QuestionCacheDTO;
import org.sxy.optimus.dto.quiz.QuizDetailCacheDTO;



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


    public RedisCacheQuizRepository(@Qualifier("String-QuestionCacheDTO") RedisTemplate<String, QuestionCacheDTO> redisTemplate, @Qualifier("String-QuizDetailCacheDTO")RedisTemplate<String, QuizDetailCacheDTO> redisDetailTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTemplateQuizDetail = redisDetailTemplate;
    }

    public boolean isQuizPreloaded(UUID quizId) {
        String key1=formKey(List.of("quiz", quizId.toString(), "detail"));
        String Key2=formKey(List.of("quiz",quizId.toString(),"questions"));

        return redisTemplateQuizDetail.hasKey(key1) && redisTemplate.hasKey(Key2);
    }

    //This method is to upload the quiz detail to redis
    public void uploadQuizDetails(@NotNull QuizDetailCacheDTO quizDetailCacheDTO,@Min(1) Long ttlInSeconds)  throws Exception{
        String key=formKey(List.of("quiz", quizDetailCacheDTO.getQuizId(), "detail"));
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
    public void uploadQuizQuestions(@NotEmpty List<QuestionCacheDTO>questions, @NotNull UUID quizID,@Min(1) Long ttlInSeconds) throws Exception{

        String Key=formKey(List.of("quiz",quizID.toString(),"questions"));
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

    private Map<String,QuestionCacheDTO> mapQuestionsById(@NotEmpty List<QuestionCacheDTO>questions) {
        return questions.stream()
                .collect(Collectors.toMap(QuestionCacheDTO::getQuestionId, Function.identity()));
    }

    private String formKey(List<String>segments){
        return String.join(":", segments);
    }
}
