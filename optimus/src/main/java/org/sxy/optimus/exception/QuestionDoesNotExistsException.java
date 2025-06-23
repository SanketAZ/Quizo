package org.sxy.optimus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionDoesNotExistsException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(QuestionDoesNotExistsException.class);

    public QuestionDoesNotExistsException(String property , String value) {
        super("Question with property '" + property +": "+value+ "' doesn't exist.");

        log.warn("Question with property '{}: {}' doesn't exist.", property, value);
    }

}
