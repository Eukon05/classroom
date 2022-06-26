package com.eukon05.classroom.enums;

public enum ParamType {

    USERNAME(100),
    NAME(100),
    SURNAME(200),
    PASSWORD(100),
    COURSE_NAME(100),
    TITLE(200),
    CONTENT(1000),
    INVITE_CODE(6),
    COURSE_ID(null),
    ASSIGNMENT_ID(null),
    IS_TEACHER(null);

    public final Integer length;

    ParamType(Integer length) {
        this.length = length;
    }

}
