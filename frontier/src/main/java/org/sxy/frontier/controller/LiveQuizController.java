package org.sxy.frontier.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ActiveQuizQuestionDTO> getQuestion(@PathVariable String sessionId, @PathVariable int questionNumber) {
        UUID sessionID = UUID.fromString(sessionId);
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        ActiveQuizQuestionDTO res = participantSessionService.fetchQuestion(sessionID, userId, questionNumber);
        return ResponseEntity
                .ok()
                .body(res);
    }

    @PostMapping("/{roomId}/{quizId}")
    public ResponseEntity<AnswerSubmissionResDTO> submitQuestionAnswer(@PathVariable String roomId, @PathVariable String quizId, @RequestBody AnswerSubmissionReqDTO answerSubmissionReqDTO) {
        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        UUID roomID=UUID.fromString(roomId);
        UUID quizID=UUID.fromString(quizId);
        AnswerSubmissionResDTO res=participantSessionService.submitQuestion(roomID,quizID,userId,answerSubmissionReqDTO);
        return ResponseEntity
                .ok()
                .body(res);
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