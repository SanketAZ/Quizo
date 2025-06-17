package org.sxy.optimus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDoseNotExistException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(UserDoseNotExistException.class);

    public UserDoseNotExistException(String property , String value) {

        super("User with property '" + property +": "+value+ "' doesn't exist or is not exist.");

        log.warn("User with property '{}: {}' doesn't exist or is not exist.", property, value);
    }
}
