package org.sxy.optimus.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sxy.optimus.dto.question.QuestionPositionDTO;

import java.util.List;
import java.util.UUID;

public class QuizQuestionSequenceDTO {
    @NotNull
    @Size(min=1)
    List<@Valid QuestionPositionDTO>questionsPositions;

    public List<QuestionPositionDTO> getQuestionsPositions() {
        return questionsPositions;
    }

    public void setQuestionsPositions(List<QuestionPositionDTO> questionsPositions) {
        this.questionsPositions = questionsPositions;
    }
}
