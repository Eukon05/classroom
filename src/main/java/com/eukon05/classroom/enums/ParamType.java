package com.eukon05.classroom.enums;

public enum ParamType {

    username(100),
    name(100),
    surname(200),
    password(100),
    courseName(100),
    title(200),
    content(1000),
    inviteCode(6),
    courseId(null),
    assignmentId(null),
    isTeacher(null);

    public final Integer length;
    ParamType(Integer length){
        this.length = length;
    }

}
