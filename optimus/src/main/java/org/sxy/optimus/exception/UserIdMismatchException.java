package org.sxy.optimus.exception;

public class UserIdMismatchException extends RuntimeException {
    public UserIdMismatchException(String dtoUserID,String authenticatedUserID) {
      super("DTO userId (" + dtoUserID + ") does not match authenticated userId (" + authenticatedUserID + ").");
    }
}