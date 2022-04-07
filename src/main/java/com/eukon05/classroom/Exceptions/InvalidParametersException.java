package com.eukon05.classroom.Exceptions;

public class InvalidParametersException extends Exception{

    //This line exists to avoid creating an object in AuthenticationController
    public static String message = "Invalid parameters";

    public InvalidParametersException(){
        super(message);
    }

}
