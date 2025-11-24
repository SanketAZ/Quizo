package org.sxy.frontier.client;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.sxy.frontier.dto.QuizDetailDTO;
import org.sxy.frontier.dto.RoomUserDetails;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.dto.question.QuestionPositionDTO;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class OptimusServiceClient {

    private static final Logger log = LoggerFactory.getLogger(OptimusServiceClient.class);

    @Autowired
    private RestClient optimusClient;
    @Autowired
    private ObjectMapper objectMapper;


    public QuizDetailDTO getQuizDetails(UUID roomId, UUID quizId){
        try {
            return optimusClient.get()
                    .uri("/quiz/{quizId}/room/{roomId}/detail",quizId,roomId)
                    .retrieve()
                    .body(QuizDetailDTO.class);
        } catch (Exception e) {
            log.error("Failed to fetch quiz details for quizId={} roomId={}", quizId, roomId, e);
            throw new RuntimeException("Quiz details fetch failed", e);
        }
    }

    public List<QuestionPositionDTO> getQuestionPositions(UUID roomId,UUID quizId,String sequence){
        try {
            return optimusClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/quiz/{quizId}/room/{roomId}/question-positions")
                            .queryParam("sequence",sequence)
                            .build(quizId,roomId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QuestionPositionDTO>>() {});
        } catch (Exception e) {
            log.error("Failed to fetch question positions for quizId={} roomId={} sequence={}", quizId, roomId, sequence, e);
            throw new RuntimeException("Question positions fetch failed", e);
        }
    }

    public RoomUserDetails getRoomUserDetails(UUID roomId, UUID userId){
        try {
            return optimusClient.get()
                    .uri("/room/{roomId}/user/{userId}/details", roomId, userId)
                    .retrieve()
                    .body(RoomUserDetails.class);
        } catch (Exception e) {
            log.error("Failed to fetch RoomUserDetails for roomId={} userId={}", roomId, userId, e);
            throw new RuntimeException("Failed to fetch RoomUserDetails", e);
        }
    }

    public QuestionDTO getQuestion(UUID roomId, UUID quizId, UUID questionId){
        try {
            return optimusClient.get().uri("/quiz/{quizId}/room/{roomId}/question/{questionId}",quizId,roomId,questionId)
                    .retrieve()
                    .body(QuestionDTO.class);
        } catch (Exception e) {
            log.error("Failed to fetch QuestionCacheDTO for roomId={} quizId={} questionId={}", roomId, quizId, questionId, e);
            throw new RuntimeException("Failed to fetch QuestionCacheDTO", e);
        }
    }

    private ProblemDetail readProblemDetail(byte[] body){
        try {
            if(body == null || body.length == 0) return null;
            return objectMapper.readValue(body, ProblemDetail.class);
        } catch (Exception ignore) {
            return null;
        }
    }

}
