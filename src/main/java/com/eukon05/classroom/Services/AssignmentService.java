package com.eukon05.classroom.Services;

import com.eukon05.classroom.Domains.Assignment;
import com.eukon05.classroom.Exceptions.AssignmentNotFoundException;
import com.eukon05.classroom.Repositories.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public List<Assignment> getAssignmentsForCourse(int courseID){
        return assignmentRepository.findAssignmentsByCourseID(courseID);
    }


    public void createAssignment(int courseId, String title, String content, String link) {
        Assignment assignment = new Assignment(title, content, link, courseId);
        assignmentRepository.save(assignment);
    }

    public void deleteAllAssignmentsFromCourse(int courseId){
        assignmentRepository.deleteAll(getAssignmentsForCourse(courseId));
    }

    public void deleteAssignment(int assignmentId) throws AssignmentNotFoundException {

        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);

        if(assignment.isEmpty())
            throw new AssignmentNotFoundException();

        assignmentRepository.delete(assignment.get());

    }
}