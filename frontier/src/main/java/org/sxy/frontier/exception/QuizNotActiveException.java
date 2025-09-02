package org.sxy.frontier.exception;

import java.util.UUID;

public class QuizNotActiveException extends RuntimeException {
    public enum Reason{
      NOT_STARTED,
      FINISHED
    }

    private final Reason reason;
    public QuizNotActiveException(Reason reason, UUID roomId,UUID quizId) {
        super(buildMessage(reason)); //* this calls why to static method
        this.reason = reason;
    }

    private static String buildMessage(Reason reason){
      return switch (reason){
        case NOT_STARTED -> "Quiz is not live yet.";
        case FINISHED -> "Quiz has already finished.";
      };
    }

}
