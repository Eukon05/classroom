package com.eukon05.classroom.exceptions;

public class UsernameTakenException extends Exception{

    public UsernameTakenException(String username){
        super("Username \"" + username + "\" is already taken");
    }

}
