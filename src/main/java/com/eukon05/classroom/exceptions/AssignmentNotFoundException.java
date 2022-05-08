package com.eukon05.classroom.exceptions;

public class AssignmentNotFoundException extends RuntimeException{

    public AssignmentNotFoundException(long assignmentId){
        super("Assignment with id " + assignmentId + " does not exist");
    }

}
