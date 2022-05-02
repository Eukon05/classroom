package com.eukon05.classroom.exceptions;

import com.eukon05.classroom.enums.ParamType;

public class MissingParametersException extends Exception{

    public MissingParametersException(){
        super("Missing parameters");
    }
    public MissingParametersException(ParamType paramType){
        super("Missing parameter: " + paramType.name());
    }

}
