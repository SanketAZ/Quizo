package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.question.QuestionCacheDTO;
import org.sxy.optimus.dto.quiz.QuizDetailCacheDTO;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.mapper.QuestionMapper;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.module.compKey.RoomQuizId;
import org.sxy.optimus.redis.RedisCacheQuizRepository;
import org.sxy.optimus.repo.QuizRepo;
import org.sxy.optimus.repo.RoomQuizRepo;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class QuizCacheLoaderService {

    private static final Logger log = LoggerFactory.getLogger(QuizCacheLoaderService.class);

    @Autowired
    private RedisCacheQuizRepository cacheQuizRepository;

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private RoomQuizRepo roomQuizRepo;

    private final QuestionMapper questionMapper;

    private final QuizMapper quizMapper;

    public QuizCacheLoaderService(QuestionMapper questionMapper, QuizMapper quizMapper) {
        this.questionMapper = questionMapper;
        this.quizMapper = quizMapper;
    }

    //method to upload the quiz to redis before quiz starts
    //Retry logic will be implemented later
    public void preloadQuizToRedis(UUID quizId,UUID roomId) {
        if (!quizRepo.existsById(quizId)) {
            throw new ResourceDoesNotExitsException("Quiz","QuizID",quizId.toString());
        }

        if (!roomQuizRepo.existsById(new RoomQuizId(roomId,quizId))) {
            throw new UnauthorizedActionException("Quiz with id "+quizId.toString()+" does not exist in Room with id "+roomId.toString());
        }

        if ( cacheQuizRepository.isQuizPreloaded(quizId) ) {
            return;
        }

        //Fetching quiz from the database
        Quiz quiz= quizRepo.getQuizWithAllQuestions(quizId);

        //Setting the QuizDetailCacheDTO
        QuizDetailCacheDTO quizDetailCacheDTO = quizMapper.toQuizDetailCacheDTO(quiz);
        quizDetailCacheDTO.setRoomId(roomId.toString());

        List<QuestionCacheDTO> questionsToCache=quiz.getQuestions()
                .stream()
                .map(questionMapper::toQuestionCacheDTO).toList();

        long ttlForQuiz = getTTLForQuiz(quiz.getStartTime(),quiz.getDurationSec(),300);

        if(ttlForQuiz<0){
            throw new IllegalStateException("Quiz is already started or finished");
        }

        try {
            //Uploading the quiz details to redis
            cacheQuizRepository.uploadQuizDetails(quizDetailCacheDTO,ttlForQuiz);

            //Uploading the questions of quiz to redis
            cacheQuizRepository.uploadQuizQuestions(questionsToCache,quizId,roomId,ttlForQuiz);
        }catch (Exception e){
            log.error("Exception occurred while uploading the quiz to redis {}",e.getMessage());
        }

        log.info("Quiz {} has been successfully loaded", quizId);
    }

    private long getTTLForQuiz(Instant quizStartTime,int quizDurationSec,int bufferDurationSec) {
        if(quizStartTime==null){
            throw new IllegalArgumentException("Quiz Start time must not be null");
        }
        if(quizDurationSec<=0){
            throw new IllegalArgumentException("Quiz Duration must be greater than 0. Found: "+quizDurationSec);
        }
        if(bufferDurationSec<0){
            throw new IllegalArgumentException("Buffer duration cannot be negative. Found: "+bufferDurationSec);
        }

        Instant currentTime = Instant.now();
        if(quizStartTime.isBefore(currentTime)){
            return -1;
        }
        Instant quizEndTime = quizStartTime.plus(Duration.ofSeconds(quizDurationSec));

        long totalDiff=Duration.between(currentTime, quizEndTime).toSeconds();

        totalDiff+=bufferDurationSec;

        return totalDiff;
    }

}
