package org.sxy.optimus.utility;

import org.sxy.optimus.enums.QuizStatus;

import java.util.Arrays;

public class QuizValidator {

    public static boolean validateQuizStatus(String status){
        return Arrays.stream(QuizStatus.values())
                .anyMatch(quizStatus -> quizStatus.name().equals(status));

    }
}
