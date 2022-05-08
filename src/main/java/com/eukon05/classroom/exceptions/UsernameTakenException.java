package com.eukon05.classroom.exceptions;

public class UsernameTakenException extends RuntimeException{

    public UsernameTakenException(String username){
        super("Username \"" + username + "\" is already taken");
    }

}
