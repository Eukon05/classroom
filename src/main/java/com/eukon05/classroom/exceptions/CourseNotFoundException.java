package com.eukon05.classroom.exceptions;

public class CourseNotFoundException extends Exception{

    public CourseNotFoundException(String inviteCode){
        super("Course with invite code \"" + inviteCode + "\" does not exist");
    }

    public CourseNotFoundException(int courseId){
        super("Course with id " + courseId + " does not exist");
    }

}
