package com.eukon05.classroom.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUserDTO {

    private String username;
    private String name;
    private String surname;
    private boolean isTeacher;

}
