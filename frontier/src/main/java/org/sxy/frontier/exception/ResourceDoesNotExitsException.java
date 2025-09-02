package org.sxy.frontier.exception;

import org.slf4j.LoggerFactory;

public class ResourceDoesNotExitsException extends RuntimeException {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ResourceDoesNotExitsException.class);

    public ResourceDoesNotExitsException(String message) {
        super(message);
    }
}
