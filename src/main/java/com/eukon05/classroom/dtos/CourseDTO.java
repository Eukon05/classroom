package com.eukon05.classroom.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {

    private Long id;
    private String courseName;
    private String inviteCode;

}
