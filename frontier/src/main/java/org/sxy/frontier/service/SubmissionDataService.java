package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.SubmissionDTO;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.mapper.SubmissionMapper;
import org.sxy.frontier.module.Submission;
import org.sxy.frontier.redis.dto.SubmissionCacheDTO;
import org.sxy.frontier.redis.service.SubmissionCacheService;
import org.sxy.frontier.repo.SubmissionRepo;

import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionDataService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionDataService.class);
    @Autowired
    private SubmissionCacheService submissionCacheService;
    @Autowired
    private SubmissionRepo submissionRepo;
    @Autowired
    private SubmissionMapper submissionMapper;

    public SubmissionDTO getSubmission(UUID roomId, UUID quizId, UUID questionId, UUID userId) {
        log.debug("Fetching submission for userId={}, quizId={}, roomId={}, questionId={}",
                userId, quizId, roomId, questionId);

        Optional<SubmissionCacheDTO> cacheOp=submissionCacheService.getSubmission(roomId,quizId,questionId,userId);
        if(cacheOp.isPresent()){
            log.debug("Cache hit - Submission found in cache for userId={}, quizId={}, roomId={}, questionId={}",
                    userId, quizId, roomId, questionId);
            return submissionMapper.toSubmissionDTO(cacheOp.get());
        }

        log.debug("Cache miss - Fetching submission from database for userId={}, quizId={}, roomId={}, questionId={}",
                userId, quizId, roomId, questionId);

        //fetch from the db
        Optional<Submission> submissionOp=submissionRepo.findByUserIdAndQuizIdAndRoomIdAndQuestionId(userId,quizId,roomId,questionId);
        if(submissionOp.isEmpty()){
            log.warn("Submission not found for userId={}, quizId={}, roomId={}, questionId={}",
                    userId, quizId, roomId, questionId);

            String msg=String.format("Submission not found for user=%s, quiz=%s, room=%s, question=%s",
                    userId, quizId, roomId, questionId);
            throw new ResourceDoesNotExitsException(msg);
        }

        Submission submission=submissionOp.get();
        SubmissionDTO submissionDTO=submissionMapper.toSubmissionDTO(submission);

        submissionCacheService.cacheSubmission(submissionDTO);
        return submissionDTO;
    }
}
