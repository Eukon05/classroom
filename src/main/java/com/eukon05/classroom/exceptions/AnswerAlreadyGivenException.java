package com.eukon05.classroom.exceptions;

public class AnswerAlreadyGivenException extends RuntimeException {
    public AnswerAlreadyGivenException() {
        super("You already gave an answer to this assignment");
    }
}
