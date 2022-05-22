package com.eukon05.classroom.exceptions;

public class UserAlreadyAttendingTheCourseException extends RuntimeException{

    public UserAlreadyAttendingTheCourseException(String username, long courseId){
        super("User \"" + username + "\" is already attending course " + courseId);
    }

}
