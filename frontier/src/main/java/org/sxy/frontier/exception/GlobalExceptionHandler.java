package org.sxy.frontier.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(QuizNotActiveException.class)
    public ResponseEntity<ProblemDetail> handleQuizNotActiveException(QuizNotActiveException ex,HttpServletRequest req){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail body=pd(status,"Quiz Not Active",ex.getMessage(),req,"QuizNotActiveException");

        log.error(ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ProblemDetail> handleQuizDoesNotExistsException(UnauthorizedActionException ex,HttpServletRequest req){
        HttpStatus status = HttpStatus.FORBIDDEN;
        ProblemDetail body = pd(status, "Forbidden", ex.getMessage(), req, "forbidden");
        log.warn(ex.getMessage());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceDoesNotExitsException.class)
    public ResponseEntity<ProblemDetail> handleResourceDoesNotExitsException(ResourceDoesNotExitsException ex,HttpServletRequest req){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemDetail body = pd(status, "Resource Not Found", ex.getMessage(), req, "not-found");
        log.error(ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(UpstreamProblemException.class)
    public ResponseEntity<ProblemDetail> UpstreamProblemException(UpstreamProblemException ex){

        ProblemDetail pd=ex.getProblem();
        if(pd==null){
            ProblemDetail fallback = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                    "Upstream error: " + ex.getMessage());
            fallback.setTitle("Bad Gateway");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(fallback);
        }
        HttpStatus status = HttpStatus.valueOf(pd.getStatus());
        return ResponseEntity.status(status).body(pd);
    }

    private ProblemDetail pd(HttpStatus status, String title, String detail, HttpServletRequest req, String type){
        ProblemDetail body=ProblemDetail.forStatus(status);
        body.setTitle(title);
        body.setDetail(detail);
        body.setInstance(URI.create(req.getRequestURL().toString()));
        body.setProperty("timestamp", Instant.now().toString());
        return body;
    }


}
