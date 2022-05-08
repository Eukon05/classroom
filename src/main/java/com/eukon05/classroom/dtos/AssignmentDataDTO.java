package com.eukon05.classroom.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDataDTO {

    private String title;
    private String content;
    private Set<String> links;

}
