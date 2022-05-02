package com.eukon05.classroom.services;

import com.eukon05.classroom.builders.InvalidParameterExceptionBuilder;
import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.InvalidParameterException;
import com.eukon05.classroom.exceptions.MissingParametersException;

public abstract class AbstractResourceService {

    protected String checkStringAndTrim(String value, ParamType paramType) throws MissingParametersException, InvalidParameterException {
        if(value == null)
            throw new MissingParametersException(paramType);

        String trimmed = value.trim();

        if(trimmed.isEmpty())
            throw new InvalidParameterExceptionBuilder(ExceptionType.empty, paramType).build();

        else if(trimmed.length() > paramType.length)
            throw new InvalidParameterExceptionBuilder(ExceptionType.tooLong, paramType).build();

        return trimmed;
    }

    protected void checkObject(Object value, ParamType paramType) throws MissingParametersException {
        if(value == null)
            throw new MissingParametersException(paramType);
    }

    protected void checkCredential(String credential, ParamType paramType) throws MissingParametersException, InvalidParameterException {
        //I know we are checking if credential == null two times, but credentials can't contain ANY spaces, so the checkStringAndTrim method must be called last
        if(credential == null)
            throw new MissingParametersException(paramType);

        if (credential.contains(" "))
            throw new InvalidParameterExceptionBuilder(ExceptionType.spaces, paramType).build();

        checkStringAndTrim(credential, paramType);
    }

}
