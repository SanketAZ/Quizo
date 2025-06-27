package org.sxy.optimus.exception;

public class MismatchException extends RuntimeException {
    public MismatchException(String expected, String actual) {
      super("Expected value " + expected + " does not match Actual value (" + actual + ").");
    }
}