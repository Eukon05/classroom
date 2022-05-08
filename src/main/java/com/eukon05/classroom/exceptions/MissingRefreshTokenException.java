package com.eukon05.classroom.exceptions;

public class MissingRefreshTokenException extends RuntimeException{

    public MissingRefreshTokenException(){
        super("Missing refresh token");
    }

}
