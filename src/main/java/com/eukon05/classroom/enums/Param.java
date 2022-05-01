package com.eukon05.classroom.enums;

public enum Param {

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

    public final Integer number;
    Param(Integer number){
        this.number=number;
    }

}
