package com.eukon05.classroom.exceptions;

public class MissingRefreshTokenException extends Exception{

    public MissingRefreshTokenException(){
        super("Missing refresh token");
    }

}
