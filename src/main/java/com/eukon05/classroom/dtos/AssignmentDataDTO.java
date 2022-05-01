package com.eukon05.classroom.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AssignmentDataDTO {

    private String title;
    private String content;
    private Set<String> links;

}
