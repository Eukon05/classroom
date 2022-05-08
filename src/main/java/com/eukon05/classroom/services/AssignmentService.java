package com.eukon05.classroom.services;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AssignmentDataDTO;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.AccessDeniedException;
import com.eukon05.classroom.exceptions.AssignmentNotFoundException;
import com.eukon05.classroom.exceptions.MissingParametersException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.eukon05.classroom.ParamUtils.checkObject;
import static com.eukon05.classroom.ParamUtils.checkStringAndTrim;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AppUserService appUserService;
    private final CourseService courseService;


    public List<Assignment> getAssignmentsForCourse(String username, long courseId){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        //This line serves as a check for if the users is attending the course.
        //It will be used multiple times in this class, so I wanted to clarify what it is for.
        courseService.getAppUserCourse(appUser, course);

        return course.getAssignments();
    }

    @Transactional
    public void createAssignment(String username, long courseId, AssignmentDataDTO dto){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        final StringBuilder contentBuilder = new StringBuilder();
        Optional.ofNullable(dto.getContent()).ifPresent(content -> {
            //This line bypasses the empty string check in checkStringAndTrim()
            if(!content.trim().isEmpty()) {
                contentBuilder.append(checkStringAndTrim(content, ParamType.content));
            }
        });

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher()) {
            throw new AccessDeniedException();
        }

        course.getAssignments().add(new Assignment(checkStringAndTrim(dto.getTitle(), ParamType.title), contentBuilder.toString(), dto.getLinks()));
    }

    @Transactional
    public void updateAssignment(String username, long courseId, long assignmentId, AssignmentDataDTO dto){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher()) {
            throw new AccessDeniedException();
        }

        Assignment assignment = getAssignmentById(assignmentId, course);

        if(dto.getTitle() == null && dto.getContent() == null && dto.getLinks() == null)
            throw new MissingParametersException();

        Optional.ofNullable(dto.getTitle()).ifPresent(title -> assignment.setTitle(checkStringAndTrim(title, ParamType.title)));

        Optional.ofNullable(dto.getContent()).ifPresent(content -> {
            //This line bypasses the empty string check in checkStringAndTrim()
            if(content.trim().isEmpty()) {
                assignment.setContent("");
            }
            else {
                assignment.setContent(checkStringAndTrim(content, ParamType.content));
            }
        });

        Optional.ofNullable(dto.getLinks()).ifPresent(links -> assignment.setLinks(links));
    }

    @Transactional
    public void deleteAssignment(String username, long courseId, long assignmentId){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        checkObject(assignmentId, ParamType.assignmentId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher()) {
            throw new AccessDeniedException();
        }

        course.getAssignments().remove(getAssignmentById(assignmentId, course));
    }

    private Assignment getAssignmentById(long assignmentId, Course course){
        return course.getAssignments()
                .stream()
                .filter(assignment -> assignment.getId()==assignmentId)
                .findFirst()
                .orElseThrow(() -> new AssignmentNotFoundException(assignmentId));
    }

}
