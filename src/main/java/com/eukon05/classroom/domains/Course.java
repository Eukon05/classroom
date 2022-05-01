package com.eukon05.classroom.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(value= {"appUsers", "assignments"})
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COURSE")
@EqualsAndHashCode
public class Course {

    @Id
    @GenericGenerator(name = "inc", strategy = "increment")
    @GeneratedValue(generator = "inc")
    private Integer id;

    private String name;

    private String inviteCode;

    @OneToMany
    private List<AppUserCourse> appUsers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    public Course(String name){
        this.name=name;
    }

}
