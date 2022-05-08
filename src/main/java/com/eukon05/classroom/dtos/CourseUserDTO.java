package com.eukon05.classroom.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseUserDTO {

    private String username;
    private String name;
    private String surname;
    private Boolean isTeacher;

}
