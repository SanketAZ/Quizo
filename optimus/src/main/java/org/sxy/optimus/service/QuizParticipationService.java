package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.quiz.QuizPreviewDTO;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.module.compKey.RoomUserId;
import org.sxy.optimus.repo.*;

import java.util.List;
import java.util.UUID;

@Service
public class QuizParticipationService {

    private static final Logger log = LoggerFactory.getLogger(QuizParticipationService.class);

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private RoomUserRepo roomUserRepo;

    /**
     * Returns a preview of quiz questions for a participant user.
     */
    public QuizPreviewDTO getQuizParticipationPreview(UUID roomId,UUID quizId,UUID userId) {
        //validate user access
        validateUserAccessToQuiz(roomId,quizId,userId);

        List<String> questionIds=questionRepo.findQuestionIdsByQuizId(quizId).stream()
                .map(UUID::toString)
                .toList();

        QuizPreviewDTO preview = new QuizPreviewDTO(quizId.toString(), questionIds);
        log.info("Prepared quiz preview for user={} in room={} quiz={}; questionsCount={} ",
                userId, roomId, quizId, questionIds.size());

        return preview;
    }

    /**
     * Validates that the room exists, the quiz is part of the room, and the user is in the room.
     * Throws ResourceDoesNotExitsException (404) or UnauthorizedActionException (403) accordingly.
     */
    private void validateUserAccessToQuiz(UUID roomId,UUID quizId,UUID userId) {
        //check room exists or not
        if(!roomRepo.existsById(roomId)){
            log.warn("Room not found: roomId={}", roomId);
            throw new ResourceDoesNotExitsException("Room","roomId",roomId.toString());
        }

        //check quiz is part of given room
        if(!quizRepo.existsByQuizIdAndRoomId(quizId,roomId)){
            log.warn("Quiz {} is not part of room {}", quizId, roomId);
            String msg=String.format("Quiz with id: %s does not exist in Room with id: %s",quizId,roomId);
            throw new UnauthorizedActionException(msg);
        }

        //check quiz is part of given room
        if(!roomUserRepo.existsById(new RoomUserId(roomId,userId))){
            log.warn("User {} is not present in room {}", userId, roomId);
            String msg=String.format("User with id: %s does not present in Room with id: %s",userId,roomId);
            throw new UnauthorizedActionException(msg);
        }

        log.debug("Access validated for user={} to quiz={} in room={}", userId, quizId, roomId);
    }

}
