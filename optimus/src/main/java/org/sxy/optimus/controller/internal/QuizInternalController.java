package org.sxy.optimus.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.question.QuestionCacheDTO;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.dto.quiz.QuizDetailCacheDTO;
import org.sxy.optimus.service.QuizService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/v1/quiz")
public class QuizInternalController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/{quizId}/room/{roomId}/details")
    public ResponseEntity<QuizDetailCacheDTO> getQuizDetailsCache(@PathVariable UUID quizId, @PathVariable UUID roomId){

        QuizDetailCacheDTO resDTO=quizService.getOrLoadQuizDetailCache(roomId,quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resDTO);

    }
    @GetMapping("/{quizId}/room/{roomId}/question-positions")
    public ResponseEntity<List<QuestionPositionDTO>> getQuestionPositionCache(@PathVariable UUID quizId, @PathVariable UUID roomId, @RequestParam("sequence") String sequence){
        List<QuestionPositionDTO> resDTO=quizService.getOrLoadQuestionPositionCache(sequence, roomId,quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resDTO);
    }

    @GetMapping("/{quizId}/room/{roomId}/question/{questionId}")
    public ResponseEntity<QuestionCacheDTO> getQuestionCacheDTO(@PathVariable UUID quizId, @PathVariable UUID roomId, @PathVariable UUID questionId){
        QuestionCacheDTO res=quizService.getOrLoadQuestionCacheDTO(roomId,quizId, questionId);
        return ResponseEntity
                .ok()
                .body(res);
    }
}
