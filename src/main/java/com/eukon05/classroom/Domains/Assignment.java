package com.eukon05.classroom.Domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ASSIGNMENT")
public class Assignment {

    @Id
    @GeneratedValue
    private Integer id;

    private String title;
    private String content;

    @ElementCollection
    private List<String> links = new ArrayList<>();

    private int courseID;

    public Assignment(String title, String content, List<String> links, int courseID){
        this.title=title;
        this.content=content;
        this.links=links;
        this.courseID=courseID;
    }

}
