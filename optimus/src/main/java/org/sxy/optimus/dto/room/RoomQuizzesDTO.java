package org.sxy.optimus.dto.room;

import org.sxy.optimus.dto.quiz.QuizDisplayDTO;

import java.util.List;

public class RoomQuizzesDTO {
    private String roomId;
    List<QuizDisplayDTO> quizzes;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<QuizDisplayDTO> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizDisplayDTO> quizzes) {
        this.quizzes = quizzes;
    }
}
