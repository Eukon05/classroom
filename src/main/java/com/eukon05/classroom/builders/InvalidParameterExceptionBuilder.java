package com.eukon05.classroom.builders;

import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.InvalidParameterException;

public class InvalidParameterExceptionBuilder {
    private final InvalidParameterException exception;

    public InvalidParameterExceptionBuilder(ExceptionType exceptionType, ParamType paramType){
        switch(exceptionType){
            case empty:{
                exception = new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be an empty string");
                break;
            }
            case tooLong:{
                exception = new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be longer than " + paramType.length + " characters");
                break;
            }
            case spaces:{
                exception = new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot contain any spaces");
                break;
            }
            case tooShort:{
                exception = new InvalidParameterException("Invalid parameter: " + paramType.name() + " cannot be shorter than " + paramType.length + " characters");
                break;
            }
            case selfRoleChange:{
                exception = new InvalidParameterException("Invalid parameter: you cannot change your own role in the course");
                break;
            }
            default:{
                exception = new InvalidParameterException("Invalid parameter: " + paramType.name());
                break;
            }
        }
    }

    public InvalidParameterException build(){
        return exception;
    }

}
