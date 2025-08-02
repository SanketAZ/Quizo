package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sxy.optimus.dto.quiz.QuizPreviewDTO;
import org.sxy.optimus.service.QuizParticipationService;
import org.sxy.optimus.utility.UserContextHolder;

import java.util.UUID;

@RestController
@Tag(name = "Quiz Participation",description = "Quiz service api's")
@RequestMapping("/api/quiz-participation")
public class QuizParticipationController {

    @Autowired
    private QuizParticipationService quizParticipationService;

    @GetMapping("/questions/{roomId}/{quizId}")
    @Operation(summary = "Fetch quiz preview for a participant with list of question IDs")
    public ResponseEntity<QuizPreviewDTO> fetchQuizQuestionsDetail(@PathVariable(name = "roomId") String roomId, @PathVariable(name = "quizId") String quizId){
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        QuizPreviewDTO res=quizParticipationService.getQuizParticipationPreview(UUID.fromString(roomId),UUID.fromString(quizId),userId);

        return ResponseEntity
                .ok()
                .body(res);
    }

}
