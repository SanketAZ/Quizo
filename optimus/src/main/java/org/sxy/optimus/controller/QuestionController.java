package org.sxy.optimus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.QuestionCreateResDTO;
import org.sxy.optimus.dto.QuestionRequestDTO;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.QuestionService;
import org.sxy.optimus.utility.UserContextHolder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    //Adding multiple questions to quiz
    @PostMapping("/{userId}/{quizId}")
    public ResponseEntity<List<QuestionCreateResDTO>> addQuestion(@PathVariable(name = "userId") UUID userId,
                                                                  @PathVariable(name = "quizId") UUID quizId,
                                                                  @RequestBody List<QuestionRequestDTO> questions) {

        //checking userId in DTO and Principle User
        if(!userId.toString().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(userId.toString(), UserContextHolder.getUser().getId());
        }

        //Adding the questions
        List<QuestionCreateResDTO> addedQuestions = questionService.addQuestionsToQuiz(userId, quizId, questions);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addedQuestions);

    }
}
