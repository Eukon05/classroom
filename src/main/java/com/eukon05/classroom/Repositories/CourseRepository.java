package com.eukon05.classroom.Repositories;

import com.eukon05.classroom.Domains.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    Optional<Course> findCourseByInviteCode(String inviteCode);

    @Query(value = "SELECT MAX(id) FROM Course", nativeQuery = true)
    Optional<Integer> findMaxCourseId();

}
