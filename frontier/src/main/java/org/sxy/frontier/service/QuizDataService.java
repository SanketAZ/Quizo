package org.sxy.frontier.service;

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
import org.sxy.frontier.redis.service.QuizCacheService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizDataService {

    private static final Logger log = LoggerFactory.getLogger(QuizDataService.class);
    @Autowired
    private QuizCacheService quizCacheService;
    @Autowired
    private QuizMapper quizMapper;
    @Autowired
    private OptimusServiceClient optimusServiceClient;

    public QuestionDTO getQuestion(UUID roomId, UUID quizId, UUID questionId){
        Optional<QuestionCacheDTO> cached=quizCacheService.getQuestionCache(roomId,quizId,questionId);

        if (cached.isPresent()) {
            log.debug("Cache HIT for questionId={} in quizId={}", questionId, quizId);
            return quizMapper.toQuestionDTO(cached.get());
        }
        log.debug("Cache MISS for questionId={} in quizId={}", questionId, quizId);

        return optimusServiceClient.getQuestion(roomId, quizId, questionId);
    }

    public QuizDetailDTO getQuizDetail(UUID roomId, UUID quizId) {
        Optional<QuizDetailCacheDTO> cacheOp=quizCacheService.getQuizDetailCache(roomId,quizId);

        if(cacheOp.isPresent()){
            log.debug("Cache hit: quizId={}, roomId={}", quizId, roomId);
            return quizMapper.toQuizDetailDTO(cacheOp.get());
        }
        log.debug("Cache miss: quizId={}, roomId={}", quizId, roomId);

        return optimusServiceClient.getQuizDetails(roomId,quizId);
    }

    public UUID getQuestionId(UUID roomId,UUID quizId,Integer qIndex,String seqLabel){
        Optional<String> questionIdOp=quizCacheService.getQuestionId(roomId,quizId,qIndex,seqLabel);
        if (questionIdOp.isPresent()) {
            log.debug("Cache hit: qIndex={}, quizId={}, seqLabel={}", qIndex, quizId, seqLabel);
            String questionId=questionIdOp.get();
            return UUID.fromString(questionId);
        }

        log.warn("Cache miss for qIndex={}, quizId={}, seqLabel={} - " +
                        "calling Optimus (expects to populate cache as side effect)",
                qIndex, quizId, seqLabel);

        List<QuestionPositionDTO> questionPositionDTOList=optimusServiceClient.getQuestionPositions(roomId,quizId,seqLabel);
        Map<Integer, String> quePosMap = questionPositionDTOList.
                stream()
                .collect(Collectors.toMap(QuestionPositionDTO::getPosition, QuestionPositionDTO::getQuestionId));

        if(!quePosMap.containsKey(qIndex)){
            String msg=String.format("Quiz Index %s is not in quiz %s",qIndex,quizId);
            throw new ResourceDoesNotExitsException(msg);
        }
        String questionId=quePosMap.get(qIndex);
        return UUID.fromString(questionId);
    }
}
