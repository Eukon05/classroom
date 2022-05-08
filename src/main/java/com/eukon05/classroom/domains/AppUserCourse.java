package com.eukon05.classroom.domains;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="APPUSER_COURSE")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class AppUserCourse {

    @EmbeddedId
    private AppUserCourseId id;

    @ManyToOne
    @MapsId("username")
    private AppUser appUser;

    @ManyToOne
    @MapsId("courseId")
    private Course course;

    private boolean isTeacher;

    public AppUserCourse(AppUser appUser, Course course, boolean isTeacher) {
        this.course = course;
        this.appUser = appUser;
        this.isTeacher = isTeacher;
        this.id = new AppUserCourseId(appUser.getUsername(), course.getId());
    }
}
