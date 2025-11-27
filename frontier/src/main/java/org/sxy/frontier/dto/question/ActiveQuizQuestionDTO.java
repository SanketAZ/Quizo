package org.sxy.frontier.dto.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sxy.frontier.dto.option.ActiveQuizOptionDTO;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActiveQuizQuestionDTO {
    @NotEmpty
    private String questionId;
    @NotEmpty
    private String  text;
    @NotNull
    private Integer weight;
    private boolean answered;
    private String selectedOptionId;

    @NotEmpty
    List< @NotEmpty ActiveQuizOptionDTO> options;

}
