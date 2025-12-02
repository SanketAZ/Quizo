package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignQuizToRoomResDTO {
    private String quizId;
    private String roomId;
    private Instant startTime;
    private String roomTitle;
}
