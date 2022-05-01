package com.eukon05.classroom.services;

import com.eukon05.classroom.builders.InvalidParameterExceptionBuilder;
import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.Param;
import com.eukon05.classroom.exceptions.InvalidParameterException;
import com.eukon05.classroom.exceptions.MissingParametersException;

public abstract class AbstractResourceService {

    protected void isValid(String value, Param param) throws MissingParametersException, InvalidParameterException {
        if(value == null)
            throw new MissingParametersException(param);

        else if(value.isEmpty())
            throw new InvalidParameterExceptionBuilder(ExceptionType.empty, param).build();

        else if(value.length()>param.number)
            throw new InvalidParameterExceptionBuilder(ExceptionType.tooLong, param).build();
    }

    protected void isValid(Object value, Param param) throws MissingParametersException {
        if(value == null)
            throw new MissingParametersException(param);
    }

    protected void isCredentialValid(String credential, Param param) throws MissingParametersException, InvalidParameterException {
        isValid(credential, param);

        if (credential.contains(" "))
            throw new InvalidParameterExceptionBuilder(ExceptionType.spaces, param).build();
    }

}
