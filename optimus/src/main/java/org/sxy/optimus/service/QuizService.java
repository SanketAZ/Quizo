package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.QuizCreateDTO;
import org.sxy.optimus.dto.QuizCreatedDTO;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.repo.QuizRepo;


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
}
