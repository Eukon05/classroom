package com.eukon05.classroom.exceptions;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException(){
        super("Invalid token");
    }

}
