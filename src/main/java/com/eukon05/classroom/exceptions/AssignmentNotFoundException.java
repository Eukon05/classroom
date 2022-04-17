package com.eukon05.classroom.exceptions;

public class AssignmentNotFoundException extends Exception{

    public AssignmentNotFoundException(){
        super("This assignments doesn't exist");
    }

}
