package org.sxy.optimus.exception;

import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class ResourceDoesNotExitsException extends RuntimeException {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ResourceDoesNotExitsException.class);

    public ResourceDoesNotExitsException(String resourceName, String property, String value) {
        super(resourceName +"with property '" + property +": "+value+ "' doesn't exist.");

        log.warn("{} with property {} : {} doesn't exist.",resourceName, property, value);
    }
}
