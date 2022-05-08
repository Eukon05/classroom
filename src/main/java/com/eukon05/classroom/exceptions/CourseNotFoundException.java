package com.eukon05.classroom.exceptions;

public class CourseNotFoundException extends RuntimeException{

    public CourseNotFoundException(String inviteCode){
        super("Course with invite code \"" + inviteCode + "\" does not exist");
    }

    public CourseNotFoundException(long courseId){
        super("Course with id " + courseId + " does not exist");
    }

}
