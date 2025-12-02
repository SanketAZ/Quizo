package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.question.*;
import org.sxy.optimus.dto.quiz.QuizQuestionsAddResDTO;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.QuestionService;
import org.sxy.optimus.utility.UserContextHolder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question",description = "Question service api's")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    //Adding multiple questions to quiz
    @PostMapping("/{userId}/{quizId}")
    @Operation(summary = "Adding Multiple Questions to quiz")
    public ResponseEntity<QuizQuestionsAddResDTO> addQuestion(@PathVariable(name = "userId") UUID userId,
                                                                  @PathVariable(name = "quizId") UUID quizId,
                                                                  @RequestBody @Valid List< @Valid QuestionRequestDTO> questions) {

        //checking userId in DTO and Principle User
        if(!userId.toString().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(userId.toString(), UserContextHolder.getUser().getId());
        }

        //Adding the questions
        QuizQuestionsAddResDTO resDTO = questionService.addQuestionsToQuiz(userId, quizId, questions);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resDTO);

    }

    //Update single question in quiz
    @PutMapping("/{userId}/{questionId}")
    @Operation(summary = "Updating one Questions")
    public ResponseEntity<QuestionUpdateResDTO> updateQuestion(@PathVariable(name = "userId") UUID userId,
                                                               @PathVariable(name = "questionId") UUID questionId,
                                                               @RequestBody @Valid QuestionUpdateReqDTO question){

        //checking userId in DTO and Principle User
        if(!userId.toString().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(userId.toString(), UserContextHolder.getUser().getId());
        }

        //Updating the question
        QuestionUpdateResDTO updatedQuestion=questionService.updateQuestion(userId, questionId, question);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(updatedQuestion);
    }

    @DeleteMapping
    public ResponseEntity<QuestionDeleteResDTO> deleteQuestionsFromQuiz(@RequestParam("quizId") String quizId, @RequestBody @Valid QuestionDeleteReqDTO reqDTO) {
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        UUID quizID = UUID.fromString(quizId);

        QuestionDeleteResDTO response = questionService.deleteQuestions(userId,quizID,reqDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

}