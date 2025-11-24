package org.sxy.frontier.redis.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;
import org.sxy.frontier.utility.RedisKeys;

import java.util.Optional;
import java.util.UUID;

@Repository
public class    QuizCacheRepo {
    private final RedisTemplate<String, QuestionCacheDTO> rtQuestionCacheDTO;
    private final HashOperations<String, String,QuestionCacheDTO> hashOpsQuestionCacheDTO;
    private final RedisTemplate<String, QuizDetailCacheDTO> rtQuizDetailCacheDTO;
    private final HashOperations<String, String,QuizDetailCacheDTO> hashOpsQuizDetailCacheDTO;
    private final RedisTemplate<String,String>rtString;
    private final HashOperations<String,String,String> hashOpsString;


    @Autowired
    public QuizCacheRepo(@Qualifier("String-QuestionCacheDTO") RedisTemplate<String, QuestionCacheDTO> rtQuestionCacheDTO, @Qualifier("String-QuizDetailCacheDTO") RedisTemplate<String, QuizDetailCacheDTO> rtQuizDetailCacheDTO, @Qualifier("String-String")RedisTemplate<String, String> rtString) {
        this.rtQuestionCacheDTO = rtQuestionCacheDTO;
        this.hashOpsQuestionCacheDTO = rtQuestionCacheDTO.opsForHash();
        this.rtQuizDetailCacheDTO = rtQuizDetailCacheDTO;
        this.hashOpsQuizDetailCacheDTO = rtQuizDetailCacheDTO.opsForHash();
        this.rtString = rtString;
        this.hashOpsString = rtString.opsForHash();
    }

    public Optional<QuestionCacheDTO> getQuestion(UUID roomId, UUID quizId, UUID questionId) {
        String key= RedisKeys.buildQuizQuestionsKey(quizId.toString(),roomId.toString());
        QuestionCacheDTO questionCacheDTO=hashOpsQuestionCacheDTO.get(key,questionId.toString());
        return Optional.ofNullable(questionCacheDTO);
    }

    public Optional<QuizDetailCacheDTO> getQuizDetails(UUID roomId, UUID quizId) {
        String key= RedisKeys.buildQuizDetailKey(quizId.toString(),roomId.toString());
        QuizDetailCacheDTO quizDetailCacheDTO=rtQuizDetailCacheDTO.opsForValue().get(key);
        return Optional.ofNullable(quizDetailCacheDTO);
    }

    public boolean isQuizDetailsLoaded(UUID roomId, UUID quizId) {
        String key= RedisKeys.buildQuizDetailKey(quizId.toString(),roomId.toString());
        long size=hashOpsQuizDetailCacheDTO.size(key);
        return size>0;
    }

    public boolean isQuizQuestionsLoaded(UUID roomId, UUID quizId) {
        String key= RedisKeys.buildQuizQuestionsKey(quizId.toString(),roomId.toString());
        long size=hashOpsQuestionCacheDTO.size(key);
        return size>0;
    }

    public boolean isQuizQuestionSequenceLoaded(UUID roomId, UUID quizId) {
        String key=RedisKeys.buildQuizSequenceKey(quizId.toString(),roomId.toString(),"A");
        long size=hashOpsString.size(key);
        return size>0;
    }

    public boolean isQuestionIndexPresent(UUID roomId, UUID quizId,Integer qIndex) {
        String key=RedisKeys.buildQuizSequenceKey(quizId.toString(),roomId.toString(),"A");
        return hashOpsString.hasKey(key,qIndex.toString());
    }

    public Optional<String> getQuestionIdFromIndex(UUID roomId, UUID quizId, Integer qIndex, String seqLabel) {
        String key=RedisKeys.buildQuizSequenceKey(quizId.toString(),roomId.toString(),seqLabel);
        String questionId=hashOpsString.get(key,qIndex.toString());
        return Optional.ofNullable(questionId);
    }
}