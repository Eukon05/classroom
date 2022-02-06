package com.eukon05.classroom.Repositories;

import com.eukon05.classroom.Domains.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

    List<Assignment> findAssignmentsByCourseID(int courseID);

}
