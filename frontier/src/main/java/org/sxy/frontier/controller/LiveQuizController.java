package org.sxy.frontier.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.service.ParticipantSessionService;
import org.sxy.frontier.utility.UserContextHolder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quiz")
public class LiveQuizController {

    @Autowired
    private ParticipantSessionService  participantSessionService;

    @GetMapping("/session/{sessionId}/question/{questionNumber}")
    public ResponseEntity<ActiveQuizQuestionDTO> getQuestion(@PathVariable String sessionId, @PathVariable @Min(1) int questionNumber) {
        UUID sessionID = UUID.fromString(sessionId);
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        ActiveQuizQuestionDTO res = participantSessionService.fetchActiveQuizQuestion(userId, sessionID, questionNumber);
        return ResponseEntity
                .ok()
                .body(res);
    }

    @PostMapping("/session/{sessionId}/submission")
    public ResponseEntity<AnswerSubmissionResDTO> submitQuestionAnswer(@PathVariable String sessionId,@RequestBody @Valid AnswerSubmissionReqDTO answerSubmissionReqDTO) {
        UUID sessionID = UUID.fromString(sessionId);
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        AnswerSubmissionResDTO result=participantSessionService.submitQuestion(userId,sessionID,answerSubmissionReqDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @PostMapping("/{roomId}/{quizId}/start")
    public ResponseEntity<ParticipantQuizSessionDTO> startQuiz(@PathVariable String roomId, @PathVariable String quizId) {
        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        UUID roomID=UUID.fromString(roomId);
        UUID quizID=UUID.fromString(quizId);
        ParticipantQuizSessionDTO res=participantSessionService.startQuiz(roomID,quizID,userId);
        return ResponseEntity
                .ok()
                .body(res);
    }
}