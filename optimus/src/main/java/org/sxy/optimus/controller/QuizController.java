package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.dto.PageResponse;
import org.sxy.optimus.dto.question.QuestionDTO;
import org.sxy.optimus.dto.quiz.QuizCreateDTO;
import org.sxy.optimus.dto.quiz.QuizCreatedDTO;
import org.sxy.optimus.dto.quiz.QuizUpdateRequestDTO;
import org.sxy.optimus.dto.quiz.QuizUpdateResponseDTO;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.QuizService;
import org.sxy.optimus.utility.UserContextHolder;

import java.util.UUID;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz",description = "Quiz service api's")
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

    //Fetching questions in the quiz for owner
    @PostMapping("/{quizId}/questions")
    public ResponseEntity<PageResponse<QuestionDTO>> fetchQuizQuestionsForOwner(@PathVariable("quizId")String quizId, @RequestBody PageRequestDTO pageRequestDTO){

        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        UUID quizID=UUID.fromString(quizId);
        PageResponse<QuestionDTO> response=quizService.getQuizQuestionsForOwner(userId,quizID,pageRequestDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

}