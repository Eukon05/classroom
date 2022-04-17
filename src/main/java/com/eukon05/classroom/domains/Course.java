package com.eukon05.classroom.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(value="appUsers")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COURSE")
public class Course {

    @Id
    @GenericGenerator(name = "inc", strategy = "increment")
    @GeneratedValue(generator = "inc")
    private Integer id;

    private String name;

    private String inviteCode;

    @OneToMany
    private List<AppUserCourse> appUsers = new ArrayList<>();

    public Course(String name){
        this.name=name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Course that = (Course) o;
        return Objects.equals(name, that.name) && Objects.equals(inviteCode, that.inviteCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, inviteCode);
    }

}
