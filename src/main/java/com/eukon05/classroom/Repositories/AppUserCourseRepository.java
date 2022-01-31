package com.eukon05.classroom.Repositories;

import com.eukon05.classroom.Domains.AppUserCourseId;
import com.eukon05.classroom.Domains.AppUserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserCourseRepository extends JpaRepository<AppUserCourse, AppUserCourseId> {
}
