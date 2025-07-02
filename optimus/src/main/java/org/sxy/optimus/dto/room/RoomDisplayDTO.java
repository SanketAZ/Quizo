package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;
import org.sxy.optimus.dto.quiz.QuizDisplayDTO;

import java.util.List;

public class RoomDisplayDTO {
    private String roomId;

    private String ownerUserId;

    private String title;

    private String description;

}
