package com.eukon05.classroom.exceptions;

import com.eukon05.classroom.enums.Param;

public class MissingParametersException extends Exception{

    public MissingParametersException(){
        super("Missing parameters");
    }
    public MissingParametersException(Param param){
        super("Missing parameter: " + param.name());
    }

}
