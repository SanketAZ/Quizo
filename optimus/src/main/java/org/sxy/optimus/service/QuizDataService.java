package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.question.QuestionDTO;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.dto.quiz.QuizDetailDTO;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.mapper.QuestionMapper;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.module.Question;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.redis.dto.QuestionCacheDTO;
import org.sxy.optimus.redis.service.QuizCacheService;
import org.sxy.optimus.repo.QuestionRepo;
import org.sxy.optimus.repo.QuizQuestionSequenceRepo;
import org.sxy.optimus.repo.QuizRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizDataService {
    private static final Logger log = LoggerFactory.getLogger(QuizDataService.class);
    private static final String DEFAULT_SEQUENCE_LABEL = "A";

    @Autowired
    private QuizMapper quizMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuizCacheService quizCacheService;
    @Autowired
    private QuizRepo quizRepo;
    @Autowired
    private QuestionRepo questionRepo;
    @Autowired
    private QuizQuestionSequenceRepo quizQuestionSequenceRepo;

    public QuizDetailDTO getQuizDetail(UUID roomId, UUID quizId){
        var cachedOpt = quizCacheService.getQuizDetailCache(roomId, quizId);
        if (cachedOpt.isPresent())
            return quizMapper.toQuizDetailDTO(cachedOpt.get());

        if (!quizRepo.existsById(quizId)) {
            throw new ResourceDoesNotExitsException("Quiz","QuizID",quizId.toString());
        }
        if (!quizRepo.existsByQuizIdAndRoomId(quizId,roomId)) {
            throw new UnauthorizedActionException("Quiz with id "+quizId.toString()+" does not exist in Room with id "+roomId.toString());
        }

        Quiz quiz= quizRepo.getQuizWithAllQuestions(quizId);
        QuizDetailDTO quizDetailDTO = quizMapper.toQuizDetailDTO(quiz);
        quizDetailDTO.setRoomId(roomId);

        quizCacheService.cacheQuizDetail(quizDetailDTO);
        return quizDetailDTO;
    }

    public List<QuestionPositionDTO> getQuestionPosition(String label, UUID roomId, UUID quizId){
        List<QuestionPositionDTO> cachedData = quizCacheService.getQuestionPositionCache(label,quizId,roomId);
        if (!cachedData.isEmpty())
            return cachedData;

        if (!quizRepo.existsById(quizId)) {
            throw new ResourceDoesNotExitsException("Quiz","QuizID",quizId.toString());
        }
        if (!quizRepo.existsByQuizIdAndRoomId(quizId,roomId)) {
            throw new UnauthorizedActionException("Quiz with id "+quizId.toString()+" does not exist in Room with id "+roomId.toString());
        }
        List<QuestionPositionDTO>questionPositions=quizQuestionSequenceRepo.findAllQuestionPositionsByQuiz(quizId);

        quizCacheService.cacheQuizQuestionSequence(questionPositions,DEFAULT_SEQUENCE_LABEL,quizId,roomId);
        return questionPositions;
    }

    public QuestionDTO getQuestion(UUID roomId, UUID quizId, UUID questionId){
        Optional<QuestionCacheDTO> questionCacheOp=quizCacheService.getQuestionCacheDTO(roomId,quizId,questionId);
        if(questionCacheOp.isPresent()){
            return questionMapper.toQuestionDTO(questionCacheOp.get());
        }
        Optional<Question> questionOp=questionRepo.findQuestionByQuestionIdWithQuiz(questionId);
        if(questionOp.isEmpty()){
            throw new ResourceDoesNotExitsException("Question","QuestionId",questionId.toString());
        }
        Question question=questionOp.get();
        if(!question.getQuiz().getQuizId().equals(quizId)){
            String msg=String.format("Question with id %s is not in Quiz %s", questionId.toString(),quizId.toString());
            throw new UnauthorizedActionException(msg);
        }
        QuestionDTO res=questionMapper.toQuestionDTO(question);
        quizCacheService.cacheQuestion(res,quizId,roomId);
        return res;
    }

}