package com.eukon05.classroom.exceptions;

public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(){
        super("Access denied");
    }

}
