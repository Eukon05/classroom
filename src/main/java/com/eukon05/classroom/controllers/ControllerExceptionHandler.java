package com.eukon05.classroom.controllers;

import com.eukon05.classroom.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>("Invalid/missing body", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidParameterException.class,
            MissingParametersException.class,
            UsernameTakenException.class,
            MissingRefreshTokenException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected String badRequestHandler(Exception ex){
        return ex.getMessage();
    }

    @ExceptionHandler(value = {AccessDeniedException.class,
            UserNotAttendingTheCourseException.class,
            InvalidTokenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected String accessDeniedHandler(Exception ex){
        return ex.getMessage();
    }

    @ExceptionHandler(value = {UserNotFoundException.class,
    CourseNotFoundException.class,
    AssignmentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected String resourceNotFoundHandler(Exception ex){
        return ex.getMessage();
    }

}
