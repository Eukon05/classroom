package com.eukon05.classroom.services;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AssignmentDataDTO;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService extends AbstractResourceService{

    private final AssignmentRepository assignmentRepository;

    @Setter
    private AppUserService appUserService;

    @Setter
    private CourseService courseService;


    public List<Assignment> getAssignmentsForCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, InvalidParametersException, MissingParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.getAppUserCourse(appUser, course);

        return assignmentRepository.findAssignmentsByCourseID(courseId);
    }

    public void createAssignment(String username, int courseId, AssignmentDataDTO dto)
            throws UserNotFoundException, CourseNotFoundException, MissingParametersException, AccessDeniedException, InvalidParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        valueCheck(dto.getTitle().trim());

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        Assignment assignment = new Assignment(dto.getTitle().trim(), dto.getContent().trim(), dto.getLinks(), courseId);
        assignmentRepository.save(assignment);
    }

    public void updateAssignment(String username, int courseId, int assignmentId, AssignmentDataDTO dto)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, MissingParametersException, InvalidParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        Assignment assignment = getAssignmentById(assignmentId);

        if((dto.getTitle() == null || dto.getTitle().trim().isEmpty()) && dto.getContent() == null && dto.getLinks() == null)
            throw new MissingParametersException();

        if(dto.getTitle() != null && !dto.getTitle().trim().isEmpty())
            assignment.setTitle(dto.getTitle().trim());

        if(dto.getContent() != null)
            assignment.setContent(dto.getContent().trim());

        if(dto.getLinks() != null)
            assignment.setLinks(dto.getLinks());

        assignmentRepository.save(assignment);
    }

    public void deleteAssignment(String username, int courseId, int assignmentId)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, InvalidParametersException, MissingParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        valueCheck(assignmentId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        Assignment assignment = getAssignmentById(assignmentId);

        assignmentRepository.delete(assignment);
    }

    Assignment getAssignmentById(int assignmentId) throws AssignmentNotFoundException {
        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
        return assignment.orElseThrow(() -> new AssignmentNotFoundException());
    }

    void deleteAllAssignmentsFromCourse(int courseId){
        assignmentRepository.deleteAll(assignmentRepository.findAssignmentsByCourseID(courseId));
    }

}
