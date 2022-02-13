package com.eukon05.classroom.Exceptions;

public class AssignmentNotFoundException extends Exception{

    public AssignmentNotFoundException(){
        super("This assignments doesn't exist");
    }

}
