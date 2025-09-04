package org.sxy.frontier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{roomId}/{quizId}")
    public ResponseEntity<ActiveQuizQuestionDTO> getQuestion(@PathVariable String roomId, @PathVariable String quizId, @RequestParam("qIndex")int qIndex, @RequestParam("seqLabel")String seqLabel) {
        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        UUID roomID=UUID.fromString(roomId);
        UUID quizID=UUID.fromString(quizId);
        ActiveQuizQuestionDTO res=participantSessionService.fetchQuestion(roomID,quizID,userId,qIndex,seqLabel);
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
}