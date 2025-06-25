package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.quiz.QuizCreateDTO;
import org.sxy.optimus.dto.quiz.QuizCreatedDTO;
import org.sxy.optimus.dto.quiz.QuizUpdateRequestDTO;
import org.sxy.optimus.dto.quiz.QuizUpdateResponseDTO;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.QuizService;
import org.sxy.optimus.utility.UserContextHolder;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping
    @Operation(summary = "Create Quiz with description")
    public ResponseEntity<QuizCreatedDTO> createQuiz(@RequestBody @Valid QuizCreateDTO quizCreateDTO){

        //checking userId in DTO and Principle User
        if(!quizCreateDTO.getCreatorUserId().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(quizCreateDTO.getCreatorUserId(), UserContextHolder.getUser().getId());
        }

        //creating quiz
        QuizCreatedDTO quizCreatedDTO=quizService.createQuiz(quizCreateDTO);

        return ResponseEntity.ok().body(quizCreatedDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quiz description")
    public ResponseEntity<QuizUpdateResponseDTO> updateQuiz(@PathVariable(name="id") String id,
            @RequestBody @Valid QuizUpdateRequestDTO quizUpdateRequestDTO){
        //checking userId in DTO and Principle User
        if(!quizUpdateRequestDTO.getCreatorUserId().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(quizUpdateRequestDTO.getCreatorUserId(), UserContextHolder.getUser().getId());
        }

        //Updating the quiz details
        QuizUpdateResponseDTO quizUpdateResponseDTO=quizService.updateQuiz(id,quizUpdateRequestDTO);

        return ResponseEntity.ok().body(quizUpdateResponseDTO);
    }

}