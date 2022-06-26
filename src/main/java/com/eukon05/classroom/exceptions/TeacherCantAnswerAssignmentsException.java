package com.eukon05.classroom.exceptions;

public class TeacherCantAnswerAssignmentsException extends RuntimeException{
    public TeacherCantAnswerAssignmentsException(){
        super("Teachers can't give answers to assignments");
    }
}
