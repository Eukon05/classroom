package com.eukon05.classroom.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class AssignmentDataDTO {

    private String title;
    private String content;
    private List<String> links;

}
