package com.eukon05.classroom.Exceptions;

public class UsernameTakenException extends Exception{

    public UsernameTakenException(String username){
        super("Username \"" + username + "\" is already taken");
    }

}
