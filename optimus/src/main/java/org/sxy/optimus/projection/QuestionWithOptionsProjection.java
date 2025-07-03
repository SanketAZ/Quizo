package org.sxy.optimus.projection;

import java.util.List;

public interface QuestionWithOptionsProjection {
    String getQuestionId();
    String getText();
    String getWeight();
    List<OptionProjection> getOptions();

    interface OptionProjection{
        String getOptionId();
        String getText();
        Boolean getIsCorrect();
    }
}
