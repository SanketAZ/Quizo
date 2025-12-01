package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.dto.PageResponse;
import org.sxy.optimus.dto.question.QuestionDTO;
import org.sxy.optimus.dto.question.QuestionDeleteReqDTO;
import org.sxy.optimus.dto.question.QuestionDeleteResDTO;
import org.sxy.optimus.dto.quiz.*;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.QuizService;
import org.sxy.optimus.utility.UserContextHolder;

import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz",description = "Quiz service api's")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping
    @Operation(summary = "Create Quiz with description")
    public ResponseEntity<QuizCreatedDTO> createQuiz(@RequestBody @Valid QuizCreateDTO quizCreateDTO) {

        //checking userId in DTO and Principle User
        if (!quizCreateDTO.getCreatorUserId().equals(UserContextHolder.getUser().getId())) {
            throw new UserIdMismatchException(quizCreateDTO.getCreatorUserId(), UserContextHolder.getUser().getId());
        }

        //creating quiz
        QuizCreatedDTO quizCreatedDTO = quizService.createQuiz(quizCreateDTO);

        return ResponseEntity.ok().body(quizCreatedDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quiz description")
    public ResponseEntity<QuizUpdateResponseDTO> updateQuiz(@PathVariable(name = "id") String id,
                                                            @RequestBody @Valid QuizUpdateRequestDTO quizUpdateRequestDTO) {
        //checking userId in DTO and Principle User
        if (!quizUpdateRequestDTO.getCreatorUserId().equals(UserContextHolder.getUser().getId())) {
            throw new UserIdMismatchException(quizUpdateRequestDTO.getCreatorUserId(), UserContextHolder.getUser().getId());
        }

        //Updating the quiz details
        QuizUpdateResponseDTO quizUpdateResponseDTO = quizService.updateQuiz(id, quizUpdateRequestDTO);

        return ResponseEntity.ok().body(quizUpdateResponseDTO);
    }

    //Fetching questions in the quiz for owner
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<PageResponse<QuestionDTO>> fetchQuizQuestionsForOwner(
            @PathVariable("quizId") String quizId,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "DESC") @Pattern(regexp = "ASC|DESC") String sortOrder) {

        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        UUID quizID = UUID.fromString(quizId);
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();

        PageResponse<QuestionDTO> response = quizService.getQuizQuestionsForOwner(userId, quizID, pageRequestDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<QuizDisplayDTO>> fetchOwnedQuizzes(
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") @Max(100) int pageSize,
            @RequestParam(value = "roomId", required = false) String roomId,
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "DESC") @Pattern(regexp = "ASC|DESC")  String sortOrder

    ) {
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        UUID roomID = roomId != null ? UUID.fromString(roomId) : null;
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();
        PageResponse<QuizDisplayDTO> response=quizService.fetchOwnedQuizzes(userId, roomID, status, pageRequestDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping("/joined")
    public ResponseEntity<PageResponse<QuizDisplayDTO>> fetchJoinedQuizzes(
            @RequestParam(value = "pageNo", defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @RequestParam(value = "roomId", required = false) String roomId,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "sortBy", defaultValue = "startTime") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "ASC") @Pattern(regexp = "ASC|DESC") String sortOrder
    ) {
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        UUID roomUUID = roomId != null ? UUID.fromString(roomId) : null;

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();

        PageResponse<QuizDisplayDTO> response = quizService.fetchJoinedQuizzes(
                userId, roomUUID, status, pageRequestDTO
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{quizId}/start-time")
    public ResponseEntity<QuizStartTimeResDTO> setQuizStartTime(@PathVariable("quizId") String quizId, @RequestBody @Valid QuizStartTimeReqDTO reqDTO) {

        UUID userId = UUID.fromString(reqDTO.getCreatorUserId());
        UUID quizID = UUID.fromString(quizId);
        QuizStartTimeResDTO response = quizService.assignStartTimeToQuiz(quizID, reqDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/{quizId}/question-sequence")
    public ResponseEntity<QuizQuestionSequenceDTO> updateQuizQuestionsSequence(@PathVariable("quizId") String quizId,@RequestBody @Valid QuizQuestionSequenceDTO reqDTO) {
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        UUID quizID = UUID.fromString(quizId);
        QuizQuestionSequenceDTO response = quizService.updateQuizQuestionSequence(userId,quizID,reqDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @DeleteMapping("/{quizId}/question")
    public ResponseEntity<QuestionDeleteResDTO> deleteQuestionsFromQuiz(@PathVariable("quizId") String quizId,@RequestBody @Valid QuestionDeleteReqDTO reqDTO) {
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        UUID quizID = UUID.fromString(quizId);

        QuestionDeleteResDTO response = quizService.deleteQuestions(userId,quizID,reqDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }
}