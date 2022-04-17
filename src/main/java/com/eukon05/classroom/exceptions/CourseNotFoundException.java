package com.eukon05.classroom.exceptions;

public class CourseNotFoundException extends Exception{

    public CourseNotFoundException(){
        super("This course doesn't exist");
    }

}
