package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.module.Submission;
import org.sxy.frontier.repo.SubmissionRepo;

import java.time.Instant;
import java.util.UUID;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepo submissionRepo;

    Logger log= LoggerFactory.getLogger(SubmissionService.class);

    public Submission saveSubmission(UUID userId, UUID roomId, UUID quizId, Instant submittedAt, AnswerEvaluation answerEvaluation){
        log.info("Saving submission started: userId={}, roomId={}, quizId={}, questionId={}, submittedOptionId={}, submittedAt={}",
                userId, roomId, quizId,
                answerEvaluation.getQuestionId(),
                answerEvaluation.getSubmittedOptionId(),
                submittedAt);

        UUID questionId=UUID.fromString(answerEvaluation.getQuestionId());
        UUID submittedOptionID=UUID.fromString(answerEvaluation.getSubmittedOptionId());

        Submission submissionToSave=Submission.builder()
                .userId(userId)
                .roomId(roomId)
                .quizId(quizId)
                .questionId(questionId)
                .selectedOptionId(submittedOptionID)
                .isCorrect(answerEvaluation.getCorrect())
                .obtainedMarks(answerEvaluation.getObtainedMarks())
                .submittedAt(submittedAt)
                .build();

        log.debug("Submission object built: {}", submissionToSave);

        Submission savedSubmission = submissionRepo.save(submissionToSave);

        log.info("Submission saved successfully: submissionId={}, userId={}, quizId={}, isCorrect={}, obtainedMarks={}",
                savedSubmission.getSubmissionId(),
                savedSubmission.getUserId(),
                savedSubmission.getQuizId(),
                savedSubmission.isCorrect(),
                savedSubmission.getObtainedMarks());

        return savedSubmission;
    }
}
