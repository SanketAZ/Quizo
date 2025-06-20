package org.sxy.optimus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuizDoesNotExistsException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(QuizDoesNotExistsException.class);

    public QuizDoesNotExistsException(String property , String value) {
        super("Quiz with property '" + property +": "+value+ "' doesn't exist or is not exist.");

        log.warn("Quiz with property '{}: {}' doesn't exist or is not exist.", property, value);
    }
}
