package com.eukon05.classroom.services;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AssignmentDataDTO;
import com.eukon05.classroom.enums.Param;
import com.eukon05.classroom.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService extends AbstractResourceService{

    @Setter
    private AppUserService appUserService;

    @Setter
    private CourseService courseService;


    public List<Assignment> getAssignmentsForCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, InvalidParameterException, MissingParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.getAppUserCourse(appUser, course);

        return course.getAssignments();
    }

    public void createAssignment(String username, int courseId, AssignmentDataDTO dto)
            throws UserNotFoundException, CourseNotFoundException, MissingParametersException, AccessDeniedException, InvalidParameterException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        isValid(dto.getTitle().trim(), Param.title);

        //this is a dumb check to avoid throwing an exception when the assignment's content is empty
        //if the content isn't empty, we check if it fits in the specified character limit (isValid)
        String trimmed = "";

        if(dto.getContent()!=null) {
            isValid(dto.getContent().trim(), Param.content);
            trimmed = dto.getContent().trim();
        }

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        course.getAssignments().add(new Assignment(dto.getTitle().trim(), trimmed, dto.getLinks()));
        courseService.saveCourse(course);

    }

    public void updateAssignment(String username, int courseId, int assignmentId, AssignmentDataDTO dto)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, MissingParametersException, InvalidParameterException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        Assignment assignment = getAssignmentById(assignmentId, course);

        if(dto.getTitle() == null && dto.getContent() == null && dto.getLinks() == null)
            throw new MissingParametersException();

        if(dto.getTitle() != null) {
            isValid(dto.getTitle().trim(), Param.title);
            assignment.setTitle(dto.getTitle().trim());
        }

        //again, a "brilliant" way of solving a problem with an empty content field
        if(dto.getContent() != null) {
            if(dto.getContent().trim().isEmpty())
                assignment.setContent("");
            else {
                isValid(dto.getContent().trim(), Param.content);
                assignment.setContent(dto.getContent().trim());
            }
        }

        if(dto.getLinks() != null)
            assignment.setLinks(dto.getLinks());

        courseService.saveCourse(course);
    }

    public void deleteAssignment(String username, int courseId, int assignmentId)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, InvalidParameterException, MissingParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        isValid(assignmentId, Param.assignmentId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        course.getAssignments().remove(getAssignmentById(assignmentId, course));
        courseService.saveCourse(course);
    }

    private Assignment getAssignmentById(int assignmentId, Course course) throws AssignmentNotFoundException {
        for(Assignment assignment : course.getAssignments()){
            if(assignment.getId()==assignmentId){
                return assignment;
            }
        }
        throw new AssignmentNotFoundException();
    }

    void deleteAllAssignmentsFromCourse(Course course){
        try {
            course.getAssignments().clear();
            courseService.saveCourse(course);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
