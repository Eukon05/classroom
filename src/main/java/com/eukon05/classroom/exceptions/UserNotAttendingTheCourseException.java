package com.eukon05.classroom.exceptions;

public class UserNotAttendingTheCourseException extends RuntimeException{

    public UserNotAttendingTheCourseException(String username, long courseId){
        super("User " + username + " is not attending course " + courseId);
    }

}
