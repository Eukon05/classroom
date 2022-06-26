package com.eukon05.classroom.builders;

import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.InvalidParameterException;

public class InvalidParameterExceptionBuilder {
    private final InvalidParameterException exception;

    public InvalidParameterExceptionBuilder(ExceptionType exceptionType, ParamType paramType){
        exception = switch(exceptionType){
            case EMPTY -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be an empty string");
            case TOO_LONG -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be longer than " + paramType.length + " characters");
            case SPACES -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot contain any spaces");
            case TOO_SHORT -> new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be shorter than " + paramType.length + " characters");
            case SELF_ROLE_CHANGE -> new InvalidParameterException("Invalid parameter: you cannot change your own role in the course");
        };
    }

    public InvalidParameterException build(){
        return exception;
    }

}
