package com.eukon05.classroom.repositories;

import com.eukon05.classroom.domains.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    Optional<Course> findCourseByInviteCode(String inviteCode);

}
