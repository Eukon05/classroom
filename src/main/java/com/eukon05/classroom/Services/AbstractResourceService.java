package com.eukon05.classroom.Services;

import com.eukon05.classroom.Exceptions.InvalidParametersException;
import com.eukon05.classroom.Exceptions.MissingParametersException;

public abstract class AbstractResourceService {

    public void valueCheck(String value) throws MissingParametersException {

        if(value == null || value.isEmpty())
            throw new MissingParametersException();

    }

    public void valueCheck(Object value) throws MissingParametersException {
        if(value == null)
            throw new MissingParametersException();
    }

    public void credentialCheck(String credential) throws MissingParametersException, InvalidParametersException {
        valueCheck(credential);

        if(credential.contains(" "))
            throw new InvalidParametersException();
    }

}
