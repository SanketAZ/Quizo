package org.sxy.optimus.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            log.error("Method arguments not valid : {} -> {} ",error.getField(),error.getDefaultMessage());
            errors.put(error.getField(), error.getDefaultMessage());
        });
        ProblemDetail body=pd(HttpStatus.BAD_REQUEST, "Validation Failed",
                "One or more fields are invalid.", request, "validation");
        body.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleHandlerMethodValidationException(HandlerMethodValidationException ex,HttpServletRequest request){
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
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "Validation Failed",
                "One or more request parameters are invalid.", request, "validation");
        body.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(UserIdMismatchException.class)
    public ResponseEntity<ProblemDetail> handleUserIdMismatchException(UserIdMismatchException ex,HttpServletRequest request){
        log.error(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "UserIdMismatchException",
                ex.getMessage(), request, "bad-request");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MismatchException.class)
    public ResponseEntity<ProblemDetail> handleMismatchException(MismatchException ex,HttpServletRequest request){
        log.error(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "MismatchException",
                ex.getMessage(), request, "bad-request");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(UserDoseNotExistException.class)
    public ResponseEntity<ProblemDetail> handleUserDoseNotExistException(UserDoseNotExistException ex,HttpServletRequest request){
        log.error(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "UserDoseNotExistException",
                ex.getMessage(), request, "bad-request");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(QuizDoesNotExistsException.class)
    public ResponseEntity<ProblemDetail> handleQuizDoesNotExistsException(QuizDoesNotExistsException ex,HttpServletRequest request){
        log.error(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "QuizDoesNotExistsException",
                ex.getMessage(), request, "bad-request");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResourceDoesNotExitsException.class)
    public ResponseEntity<ProblemDetail> handleResourceDoesNotExitsException(ResourceDoesNotExitsException ex,HttpServletRequest request){
        log.error(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.NOT_FOUND, "Resource Not Found",
                ex.getMessage(), request, "not-found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedActionException(UnauthorizedActionException ex,HttpServletRequest request){
        log.warn(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.FORBIDDEN, "Forbidden",
                ex.getMessage(), request, "forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleQuizDoesNotExistsException(ValidationException ex,HttpServletRequest request){
        log.warn(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "Validation Exception",
                ex.getMessage(), request, "bad-request");
        body.setProperty("validationErrors", ex.getValidationResults());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex,HttpServletRequest request){
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "IllegalArgumentException",
                ex.getMessage(), request, "bad-request");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalStateException ex,HttpServletRequest request){
        log.warn(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "IllegalStateException",
                ex.getMessage(), request, "bad-request");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(QuizStartTimeException.class)
    public ResponseEntity<ProblemDetail> handleInvalidQuizStartTimeException(QuizStartTimeException ex,HttpServletRequest request){
        log.warn(ex.getMessage());
        ProblemDetail body = pd(HttpStatus.BAD_REQUEST, "QuizStartTimeException",
                ex.getMessage(), request, "bad-request");
        return ResponseEntity.badRequest().body(body);
    }

    private ProblemDetail pd(HttpStatus status, String title, String detail, HttpServletRequest req,String type){
        ProblemDetail body=ProblemDetail.forStatus(status);
        body.setTitle(title);
        body.setDetail(detail);
        body.setInstance(URI.create(req.getRequestURL().toString()));
        body.setProperty("timestamp", Instant.now().toString());
        return body;
    }
}
