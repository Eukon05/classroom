package com.eukon05.classroom.exceptions;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(String username){
        super("User with username \"" + username + "\" not found");
    }

}
