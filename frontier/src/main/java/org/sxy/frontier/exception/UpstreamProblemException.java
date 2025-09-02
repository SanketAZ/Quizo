package org.sxy.frontier.exception;

import org.springframework.http.ProblemDetail;

public class UpstreamProblemException extends RuntimeException {
    private final ProblemDetail problem;
    public UpstreamProblemException(ProblemDetail problem, Throwable cause) {
        super(problem != null ? problem.getDetail() : "Upstream error", cause);
        this.problem = problem;
    }
    public ProblemDetail getProblem() { return problem;}
}
