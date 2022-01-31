package com.eukon05.classroom.Domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String link;
    private int courseID;

    public Assignment(String title, String content, String link, int courseID){
        this.title=title;
        this.content=content;
        this.link=link;
        this.courseID=courseID;
    }

}
