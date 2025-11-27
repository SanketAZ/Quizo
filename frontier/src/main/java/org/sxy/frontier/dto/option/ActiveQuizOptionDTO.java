package org.sxy.frontier.dto.option;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveQuizOptionDTO {
    @NotEmpty
    private String optionId;
    @NotEmpty
    private String text;
}

