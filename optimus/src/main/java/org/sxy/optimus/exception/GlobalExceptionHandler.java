package org.sxy.optimus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            log.error("Method arguments not valid : {} -> {} ",error.getField(),error.getDefaultMessage());
            errors.put(error.getField(), error.getDefaultMessage());
        });


        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserIdMismatchException.class)
    public ResponseEntity<Map<String,String>> handleUserIdMismatchException(UserIdMismatchException ex){
        Map<String,String> error = new HashMap<String,String>();
        error.put("message", ex.getMessage());

        log.error(ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}
