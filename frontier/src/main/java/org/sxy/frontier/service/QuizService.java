package org.sxy.frontier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.mapper.QuizMapper;
import org.sxy.frontier.redis.QuizCacheRepo;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;

import java.util.Optional;
import java.util.UUID;

@Service
public class QuizService {

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
}
