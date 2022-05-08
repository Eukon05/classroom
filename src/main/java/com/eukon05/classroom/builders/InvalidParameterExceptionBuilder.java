package com.eukon05.classroom.builders;

import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.InvalidParameterException;

public class InvalidParameterExceptionBuilder {
    private final InvalidParameterException exception;

    public InvalidParameterExceptionBuilder(ExceptionType exceptionType, ParamType paramType){
        exception = switch(exceptionType){
            case empty -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be an empty string");
            case tooLong -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be longer than " + paramType.length + " characters");
            case spaces -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot contain any spaces");
            case tooShort -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be shorter than " + paramType.length + " characters");
            case selfRoleChange -> new InvalidParameterException("Invalid parameter: you cannot change your own role in the course");
        };
    }

    public InvalidParameterException build(){
        return exception;
    }

}
