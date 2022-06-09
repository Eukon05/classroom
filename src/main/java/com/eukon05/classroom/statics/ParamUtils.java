package com.eukon05.classroom.statics;

import com.eukon05.classroom.builders.InvalidParameterExceptionBuilder;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.InvalidParameterException;
import com.eukon05.classroom.exceptions.MissingParametersException;

import java.util.Optional;

import static com.eukon05.classroom.enums.ExceptionType.*;

public class ParamUtils {

    public static String checkStringAndTrim(String value, ParamType paramType) throws MissingParametersException, InvalidParameterException {
        String trimmed = Optional.ofNullable(value).orElseThrow(() -> new MissingParametersException(paramType)).trim();

        if(trimmed.isEmpty())
            throw new InvalidParameterExceptionBuilder(empty, paramType).build();

        else if(trimmed.length() > paramType.length)
            throw new InvalidParameterExceptionBuilder(tooLong, paramType).build();

        return trimmed;
    }

    public static void checkObject(Object value, ParamType paramType) throws MissingParametersException {
        Optional.ofNullable(value).orElseThrow(() -> new MissingParametersException(paramType));
    }

    public static void checkCredential(String credential, ParamType paramType) throws MissingParametersException, InvalidParameterException {
        //I know we are checking if credential == null two times, but credentials can't contain ANY spaces, so the checkStringAndTrim method must be called last
        if (Optional.ofNullable(credential).orElseThrow(() -> new MissingParametersException(paramType)).contains(" "))
            throw new InvalidParameterExceptionBuilder(spaces, paramType).build();

        checkStringAndTrim(credential, paramType);
    }

}
