package com.eukon05.classroom.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ASSIGNMENT")
public class Assignment {

    @Id
    @GenericGenerator(name = "inc", strategy = "increment")
    @GeneratedValue(generator = "inc")
    private Integer id;
    private String title;
    private String content;

    @ElementCollection()
    private Set<String> links = new HashSet<>();

    public Assignment(String title, String content, Set<String> links){
        this.title=title;
        this.content=content;
        this.links=links;
    }

}
