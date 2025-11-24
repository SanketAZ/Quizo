package org.sxy.optimus.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.question.QuestionDTO;
import org.sxy.optimus.dto.quiz.QuizDetailDTO;
import org.sxy.optimus.redis.dto.QuestionCacheDTO;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.redis.dto.QuizDetailCacheDTO;
import org.sxy.optimus.service.QuizService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/v1/quiz")
public class QuizInternalController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/{quizId}/room/{roomId}/detail")
    public ResponseEntity<QuizDetailDTO> getQuizDetail(@PathVariable UUID quizId, @PathVariable UUID roomId){

        QuizDetailDTO resDTO=quizService.getQuizDetail(roomId,quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resDTO);

    }
    @GetMapping("/{quizId}/room/{roomId}/question-positions")
    public ResponseEntity<List<QuestionPositionDTO>> getQuestionPosition(@PathVariable UUID quizId, @PathVariable UUID roomId, @RequestParam("sequence") String sequence){
        List<QuestionPositionDTO> resDTO=quizService.getQuestionPosition(sequence, roomId,quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resDTO);
    }

    @GetMapping("/{quizId}/room/{roomId}/question/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestion(@PathVariable UUID quizId, @PathVariable UUID roomId, @PathVariable UUID questionId){
        QuestionDTO res=quizService.getQuestion(roomId,quizId, questionId);
        return ResponseEntity
                .ok()
                .body(res);
    }
}
