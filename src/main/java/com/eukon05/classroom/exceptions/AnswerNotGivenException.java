package com.eukon05.classroom.exceptions;

public class AnswerNotGivenException extends RuntimeException {
    public AnswerNotGivenException() {
        super("You didn't give an answer to this assignment");
    }
}
