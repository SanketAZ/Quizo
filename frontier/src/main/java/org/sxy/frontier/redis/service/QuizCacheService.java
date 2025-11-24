package org.sxy.frontier.redis.service;

import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.QuizDetailDTO;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.dto.question.QuestionPositionDTO;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.mapper.QuizMapper;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;
import org.sxy.frontier.redis.repo.QuizCacheRepo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizCacheService {
    private static final Logger log = LoggerFactory.getLogger(QuizCacheService.class);
    @Autowired
    private QuizCacheRepo quizCacheRepo;

    public Optional<QuizDetailCacheDTO> getQuizDetailCache(UUID roomId, UUID quizId){
        return quizCacheRepo.getQuizDetails(roomId,quizId);
    }

    public Optional<String> getQuestionId(UUID roomId, UUID quizId, Integer qIndex, String seqLabel) {
        return quizCacheRepo.getQuestionIdFromIndex(roomId,quizId,qIndex,seqLabel);
    }

    public Optional<QuestionCacheDTO> getQuestionCache(UUID roomId, UUID quizId,UUID questionId) {
        return quizCacheRepo.getQuestion(roomId,quizId,questionId);
    }

}