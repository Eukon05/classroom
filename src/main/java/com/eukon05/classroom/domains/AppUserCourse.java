package com.eukon05.classroom.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="APPUSER_COURSE")
@Getter
@Setter
@NoArgsConstructor
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

    public AppUserCourse(AppUser appUser, Course course, boolean isTeacher){
        this.course=course;
        this.appUser=appUser;
        this.isTeacher=isTeacher;
        this.id = new AppUserCourseId(appUser.getUsername(), course.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AppUserCourse that = (AppUserCourse) o;
        return Objects.equals(appUser, that.appUser) &&
                Objects.equals(course, that.course) && Objects.equals(isTeacher, that.isTeacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appUser, course, isTeacher);
    }
}
