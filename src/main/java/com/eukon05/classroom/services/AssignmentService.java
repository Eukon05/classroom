package com.eukon05.classroom.services;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AssignmentDataDTO;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.AssignmentNotFoundException;
import com.eukon05.classroom.exceptions.MissingParametersException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.eukon05.classroom.statics.ParamUtils.checkObject;
import static com.eukon05.classroom.statics.ParamUtils.checkStringAndTrim;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AppUserService appUserService;
    private final CourseService courseService;


    public List<Assignment> getAssignmentsForCourse(String username, long courseId){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.attendanceCheck(appUser, course);

        return course.getAssignments();
    }

    @Transactional
    public void createAssignment(String username, long courseId, AssignmentDataDTO dto){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.teacherCheck(appUser, course);

        final StringBuilder contentBuilder = new StringBuilder();
        Optional.ofNullable(dto.content()).ifPresent(content -> {
            //This line bypasses the empty string check in checkStringAndTrim()
            if(!content.trim().isEmpty()) {
                contentBuilder.append(checkStringAndTrim(content, ParamType.CONTENT));
            }
        });

        course.getAssignments().add(new Assignment(checkStringAndTrim(dto.title(), ParamType.TITLE), contentBuilder.toString(), dto.links()));
    }

    @Transactional
    public void updateAssignment(String username, long courseId, long assignmentId, AssignmentDataDTO dto){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.teacherCheck(appUser, course);

        Assignment assignment = getAssignmentById(assignmentId, course);

        if(dto.title() == null && dto.content() == null && dto.links() == null) {
            throw new MissingParametersException();
        }

        Optional.ofNullable(dto.title()).ifPresent(title -> assignment.setTitle(checkStringAndTrim(title, ParamType.TITLE)));

        Optional.ofNullable(dto.content()).ifPresent(content -> {
            //This line bypasses the empty string check in checkStringAndTrim()
            if(content.trim().isEmpty()) {
                assignment.setContent("");
            }
            else {
                assignment.setContent(checkStringAndTrim(content, ParamType.CONTENT));
            }
        });

        Optional.ofNullable(dto.links()).ifPresent(assignment::setLinks);
    }

    @Transactional
    public void deleteAssignment(String username, long courseId, long assignmentId){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.teacherCheck(appUser, course);

        checkObject(assignmentId, ParamType.ASSIGNMENT_ID);

        course.getAssignments().remove(getAssignmentById(assignmentId, course));
    }

    Assignment getAssignmentById(long assignmentId, Course course){
        return course.getAssignments()
                .stream()
                .filter(assignment -> assignmentId == assignment.getId())
                .findFirst()
                .orElseThrow(() -> new AssignmentNotFoundException(assignmentId));
    }

}
