package org.sxy.frontier.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.QuizDetailDTO;
import org.sxy.frontier.dto.SubmissionDTO;
import org.sxy.frontier.mapper.SubmissionMapper;
import org.sxy.frontier.redis.dto.SubmissionCacheDTO;
import org.sxy.frontier.redis.repo.SubmissionCacheRepo;
import org.sxy.frontier.service.QuizDataService;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionCacheService {
    @Autowired
    private SubmissionCacheRepo submissionCacheRepo;
    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private QuizDataService quizDataService;

    private static final Logger log = LoggerFactory.getLogger(SubmissionCacheService.class);
    private static final Duration MIN_TTL = Duration.ofMinutes(1);
    private static final Duration BUFFER = Duration.ofMinutes(5);

    public Optional<SubmissionCacheDTO> getSubmission(UUID roomId, UUID quizId,UUID questionId,UUID userId) {
        String roomIdStr=roomId.toString();
        String quizIdStr=quizId.toString();
        String questionIdStr=questionId.toString();
        String userIdStr=userId.toString();
        return submissionCacheRepo.getSubmissionCache(roomIdStr,quizIdStr,userIdStr,questionIdStr);
    }

    public SubmissionCacheDTO cacheSubmission(SubmissionDTO submissionDTO){
        log.info("Caching submission: userId={}, questionId={}, quizId={}, roomId={}",
                submissionDTO.getUserId(), submissionDTO.getQuestionId(),
                submissionDTO.getQuizId(), submissionDTO.getRoomId());

        SubmissionCacheDTO submissionCacheDTO = submissionMapper.toSubmissionCacheDTO(submissionDTO);
        Duration ttl=getTTLForSubmission(submissionDTO);

        try {
            submissionCacheRepo.saveSubmissionCache(submissionCacheDTO, ttl);
            log.debug("Successfully cached submission: userId={}, questionId={}, ttl={}",
                    submissionDTO.getUserId(), submissionDTO.getQuestionId(), ttl);
        }catch (Exception e){
            log.error("Failed to cache submission: userId={}, questionId={}",
                    submissionDTO.getUserId(), submissionDTO.getQuestionId(), e);
        }
        return submissionCacheDTO;
    }

    private Duration getTTLForSubmission(SubmissionDTO dto) {
        Instant now = Instant.now();
        QuizDetailDTO quizDetail=quizDataService.getQuizDetail(dto.getRoomId(),dto.getQuizId());

        Instant quizEnd = quizDetail.getStartTime().plus(Duration.ofSeconds(quizDetail.getDurationSec()));

        Duration timeUntilEnd = Duration.between(now, quizEnd);
        if (timeUntilEnd.isNegative()) {
            log.warn("Quiz {} already ended, using minimum TTL", dto.getQuizId());
            return MIN_TTL;
        }

        Duration ttl = timeUntilEnd.plus(BUFFER);
        return ttl.compareTo(MIN_TTL) < 0 ? MIN_TTL : ttl;
    }
}
