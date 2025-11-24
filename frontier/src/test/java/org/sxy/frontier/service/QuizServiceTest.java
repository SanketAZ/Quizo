package org.sxy.frontier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.option.ActiveQuizOptionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.mapper.QuizMapper;
import org.sxy.frontier.redis.repo.QuizCacheRepo;
import org.sxy.frontier.redis.dto.OptionCacheDTO;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizCacheRepo quizCacheRepo;
    @Mock
    private QuizMapper quizMapper;
    @Mock
    private OptimusServiceClient optimusServiceClient;

    @InjectMocks
    private QuizService quizService;

    private Optional<QuestionCacheDTO> opQuestionCacheDTO;
    private QuestionCacheDTO questionCache;
    private ActiveQuizQuestionDTO  activeQuizQuestionDTO;

    @BeforeEach
    void setUp(){
        List<OptionCacheDTO> optionCacheDTOList = new ArrayList<>();
        List<ActiveQuizOptionDTO> activeQuizOptionDTOList = new ArrayList<>();

        for(int i=0;i<4;i++){
            String optionId = String.format("optionId%d",i);
            String text = String.format("optionText%d",i);
            OptionCacheDTO optionCacheDTO = OptionCacheDTO.builder()
                    .optionId(optionId)
                    .isCorrect(false)
                    .text(text)
                    .build();
            optionCacheDTOList.add(optionCacheDTO);

            ActiveQuizOptionDTO activeQuizOptionDTO = ActiveQuizOptionDTO.builder()
                    .optionId(optionId)
                    .text(text)
                    .build();

            activeQuizOptionDTOList.add(activeQuizOptionDTO);
        }

        this.questionCache=QuestionCacheDTO.builder()
                .questionId("d9572526-de51-4c11-ac84-2774445e1645")
                .text("Unit Test")
                .weight(4)
                .options(optionCacheDTOList)
                .build();

        this.opQuestionCacheDTO = Optional.of(questionCache);
        this.activeQuizQuestionDTO=ActiveQuizQuestionDTO.builder()
                .questionId("d9572526-de51-4c11-ac84-2774445e1645")
                .text("Unit Test")
                .weight(4)
                .options(activeQuizOptionDTOList)
                .build();
    }


    @Nested
    class fetchActiveQuizQuestionTests{

        @Test
        void fetchQuestionCacheHit(){
            //Given
            UUID roomId = UUID.fromString("b0753bd8-c296-4f22-befa-e3caddf9ff6e");
            UUID quizId= UUID.fromString("80aa351d-f51c-42e8-9dc7-7b7f4de99c8f");
            UUID questionId = UUID.fromString("d9572526-de51-4c11-ac84-2774445e1645");

            when(quizCacheRepo.getQuestion(roomId,quizId,questionId))
                    .thenReturn(opQuestionCacheDTO);
            when(quizMapper.toActiveQuizQuestionDTO(questionCache))
                    .thenReturn(activeQuizQuestionDTO);

            //when
            final ActiveQuizQuestionDTO res=quizService.fetchActiveQuizQuestion(roomId,quizId,questionId);
            //Then
            assertNotNull(res);
            assertEquals(activeQuizQuestionDTO.getQuestionId(),res.getQuestionId());
            assertEquals(activeQuizQuestionDTO.getText(),res.getText());
            verify(quizCacheRepo,times(1)).getQuestion(roomId,quizId,questionId);
            verify(quizMapper,times(1)).toActiveQuizQuestionDTO(questionCache);
            verify(optimusServiceClient,times(0)).getQuestion(roomId,quizId,questionId);

        }


    }

}