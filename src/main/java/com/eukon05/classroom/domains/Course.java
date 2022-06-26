package com.eukon05.classroom.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(value = {"appUserCourses", "assignments"})
@Getter

@Entity
@Table(name = "COURSE")
@EqualsAndHashCode
public class Course {

    @Id
    @GenericGenerator(name = "inc", strategy = "increment")
    @GeneratedValue(generator = "inc")
    private Long id;
    @Setter
    private String name;
    private String inviteCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private final List<AppUserCourse> appUserCourses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Assignment> assignments = new ArrayList<>();

    public Course(String name, String inviteCode) {
        this.name = name;
        this.inviteCode = inviteCode;
    }

    public Course() {

    }
}
