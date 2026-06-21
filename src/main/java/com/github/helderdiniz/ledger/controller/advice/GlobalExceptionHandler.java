package com.github.helderdiniz.ledger.controller.advice;

import com.github.helderdiniz.ledger.exception.InsufficientFundsException;
import com.github.helderdiniz.ledger.exception.InvalidAmountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;

// Centralized error handling, producing ProblemDetail (RFC 9457)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(final InsufficientFundsException exception) {
        return build(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Insufficient funds",
                exception.getMessage());
    }

    @ExceptionHandler({InvalidAmountException.class, IllegalArgumentException.class})
    public ProblemDetail handleInvalidRequest(final RuntimeException exception) {
        return build(HttpStatus.BAD_REQUEST,
                "Invalid request",
                exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(final Exception exception) {
        LOGGER.error("Unexpected error handling request", exception);

        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred while processing the request.");
    }

    private ProblemDetail build(final HttpStatus status,
                                final String title,
                                final String detail) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", OffsetDateTime.now());
        return problemDetail;
    }
}
