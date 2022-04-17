package com.eukon05.classroom.repositories;

import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.AppUserCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserCourseRepository extends JpaRepository<AppUserCourse, AppUserCourseId> {
}
