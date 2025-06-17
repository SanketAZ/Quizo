package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.QuizCreateDTO;
import org.sxy.optimus.dto.QuizCreatedDTO;
import org.sxy.optimus.dto.QuizUpdateRequestDTO;
import org.sxy.optimus.dto.QuizUpdateResponseDTO;
import org.sxy.optimus.exception.QuizDoesNotExistsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.exception.UserDoseNotExistException;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.repo.QuizRepo;

import java.time.Instant;
import java.util.UUID;


@Service
public class QuizService {

    @Autowired
    private QuizRepo quizRepo;

    private final QuizMapper quizMapper;

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    public QuizService(QuizMapper quizMapper) {
        this.quizMapper = quizMapper;
    }

    //quiz creation method
    public QuizCreatedDTO createQuiz(QuizCreateDTO quizCreateDTO){

       Quiz quiz=quizMapper.toQuiz(quizCreateDTO);
       Quiz createdQuiz=quizRepo.save(quiz);

       log.info("New Quiz created :{}",createdQuiz);
       return quizMapper.toQuizCreateDTO(createdQuiz);
    }

    //quiz details update
    public QuizUpdateResponseDTO updateQuiz(String quizID,QuizUpdateRequestDTO quizUpdateRequestDTO){

        Quiz quiz=quizRepo.findById(UUID.fromString(quizID))
                .orElseThrow(() -> new QuizDoesNotExistsException("quizId",quizID));

        if(!quiz.getCreatorUserId().toString().equals(quizUpdateRequestDTO.getCreatorUserId())){
            throw new UnauthorizedActionException("User with id "+quizUpdateRequestDTO.getCreatorUserId() +"is not authorized to update this quiz");
        }

        log.info("Quiz before update :{}",quiz);
        quiz.setDescription(quizUpdateRequestDTO.getDescription());
        quiz.setDurationSec(quizUpdateRequestDTO.getDurationSec());
        quiz.setTitle(quizUpdateRequestDTO.getTitle());
        quiz.setStartTime(Instant.parse(quizUpdateRequestDTO.getStartTime()));
        Quiz updatedQuiz=quizRepo.save(quiz);
        log.info("Quiz updated :{}",updatedQuiz);

        return quizMapper.toQuizUpdateResponseDTO(updatedQuiz);
    }
}
