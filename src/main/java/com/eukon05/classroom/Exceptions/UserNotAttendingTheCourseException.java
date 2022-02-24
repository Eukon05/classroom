package com.eukon05.classroom.Exceptions;

public class UserNotAttendingTheCourseException extends Exception{

    public UserNotAttendingTheCourseException(String username, int courseId){
        super("User " + username + " is not attending course " + courseId);
    }

}
