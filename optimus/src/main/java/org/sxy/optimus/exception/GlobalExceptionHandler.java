package org.sxy.optimus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String,String>> handleHandlerMethodValidationException(HandlerMethodValidationException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getParameterValidationResults().forEach(paramResult -> {
            paramResult.getResolvableErrors().forEach(error -> {

                String path = error.getCodes() != null && error.getCodes().length > 0
                        ? error.getCodes()[0]
                        : paramResult.getMethodParameter().getParameterName();
                String message = error.getDefaultMessage();

                log.error("Validation failed: {} -> {}", path, message);
                errors.put(path, message);
            });
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

    @ExceptionHandler(UserDoseNotExistException.class)
    public ResponseEntity<Map<String,String>> handleUserDoseNotExistException(UserDoseNotExistException ex){
        Map<String,String> error = new HashMap<String,String>();
        error.put("message", ex.getMessage());

        log.error(ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(QuizDoesNotExistsException.class)
    public ResponseEntity<Map<String,String>> handleQuizDoesNotExistsException(QuizDoesNotExistsException ex){
        Map<String,String> error = new HashMap<String,String>();
        error.put("message", ex.getMessage());

        log.error(ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<Map<String,String>> handleQuizDoesNotExistsException(UnauthorizedActionException ex){
        Map<String,String> error = new HashMap<String,String>();
        error.put("message", ex.getMessage());

        log.warn(ex.getMessage());

        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String,Object>> handleQuizDoesNotExistsException(ValidationException ex){
        Map<String,Object> error = new HashMap<String,Object>();
        error.put("message", ex.getMessage());
        error.put("validationErrors",ex.getValidationResults());

        log.warn(ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}
