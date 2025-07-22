package org.sxy.optimus.utility;

import org.sxy.optimus.enums.QuizStatus;
import org.sxy.optimus.exception.QuizStartTimeException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class QuizValidator {

    public static boolean validateQuizStatus(String status){
        return Arrays.stream(QuizStatus.values())
                .anyMatch(quizStatus -> quizStatus.name().equals(status));

    }

    public static void assertValidStartTime(Instant currentTime,Instant startTime,Integer bufferInSeconds){
        if (!validateQuizStartTime(currentTime, startTime, bufferInSeconds)) {
            String msg = String.format(
                    "Quiz start time must be at least %d seconds (%d minutes) after the current time. " +
                            "Provided start time: %s, Current time: %s.",
                    bufferInSeconds, bufferInSeconds / 60, startTime, currentTime
            );
            throw new QuizStartTimeException(msg);
        }
    }

    public static boolean validateQuizStartTime(Instant currentTime,Instant startTime,Integer bufferInSeconds){
        if (currentTime ==null ||startTime == null || bufferInSeconds == null || bufferInSeconds < 0) {
            throw new IllegalArgumentException("Start time and buffer must be non-null and buffer must be non-negative");
        }

        Duration duration = Duration.between(currentTime, startTime);
        return duration.toSeconds() >= bufferInSeconds;
    }

    public static void assertCanUpdateBeforeStart(Instant startTime, Instant currentTime, int minBufferSeconds) {
        if (startTime == null) {
            return;
        }

        Instant lockTime = startTime.minusSeconds(minBufferSeconds);
        if (!currentTime.isBefore(lockTime)) {
            String msg=String.format(
                    "Cannot update quiz. It is locked %d seconds before start. Current time: %s, Start time: %s",
                    minBufferSeconds, currentTime, startTime);

            throw new QuizStartTimeException(msg);
        }
    }
}
