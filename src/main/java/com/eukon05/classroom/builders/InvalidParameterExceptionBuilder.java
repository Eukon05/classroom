package com.eukon05.classroom.builders;

import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.Param;
import com.eukon05.classroom.exceptions.InvalidParameterException;

public class InvalidParameterExceptionBuilder {
    private final InvalidParameterException exception;

    public InvalidParameterExceptionBuilder(ExceptionType exceptionType, Param param){
        switch(exceptionType){
            case empty:{
                exception = new InvalidParameterException("Invalid parameter: " + param.name() + " cannot be an empty string");
                break;
            }
            case tooLong:{
                exception = new InvalidParameterException("Invalid parameter: " + param.name() + " cannot be longer than " + param.number + " characters");
                break;
            }
            case spaces:{
                exception = new InvalidParameterException("Invalid parameter: " + param.name() + " cannot contain spaces");
                break;
            }
            case tooShort:{
                exception = new InvalidParameterException("Invalid parameter: " + param.name() + " cannot be shorter than " + param.number + " characters");
                break;
            }
            case selfRoleChange:{
                exception = new InvalidParameterException("Invalid parameter: you cannot change your own role in the course");
                break;
            }
            default:{
                exception = new InvalidParameterException("Invalid parameter: " + param.name());
                break;
            }
        }
    }

    public InvalidParameterException build(){
        return exception;
    }

}
