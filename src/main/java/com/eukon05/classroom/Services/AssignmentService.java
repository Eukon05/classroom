package com.eukon05.classroom.Services;

import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Assignment;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AssignmentRepository;
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
    private UserService userService;

    @Setter
    private CourseService courseService;


    public List<Assignment> getAssignmentsForCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, InvalidParametersException, MissingParametersException, UserNotAttendingTheCourseException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.getAppUserCourse(appUser, course);

        return assignmentRepository.findAssignmentsByCourseID(courseId);

    }

    public void createAssignment(String username, int courseId, AssignmentDTO assignmentDTO)
            throws UserNotFoundException, CourseNotFoundException, MissingParametersException, AccessDeniedException, InvalidParametersException, UserNotAttendingTheCourseException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        valueCheck(assignmentDTO.title);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        Assignment assignment = new Assignment(assignmentDTO.title, assignmentDTO.content, assignmentDTO.links, courseId);
        assignmentRepository.save(assignment);

    }

    public void updateAssignment(String username, int courseId, int assignmentId, AssignmentDTO dto)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, MissingParametersException, InvalidParametersException, UserNotAttendingTheCourseException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        Assignment assignment = getAssignmentById(assignmentId);

        if(dto.title == null && dto.content == null && dto.links == null)
            throw new MissingParametersException();

        if(dto.title != null)
            assignment.setTitle(dto.title);

        if(dto.content != null)
            assignment.setContent(dto.content);

        if(dto.links != null)
            assignment.setLinks(dto.links);

        saveAssignment(assignment);

    }

    public void deleteAssignment(String username, int courseId, int assignmentId)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, InvalidParametersException, MissingParametersException, UserNotAttendingTheCourseException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        valueCheck(assignmentId);

        AppUserCourse auc = courseService.getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        deleteAssignment(assignmentId);
    }

    Assignment getAssignmentById(int assignmentId) throws AssignmentNotFoundException {

        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);

        if(assignment.isEmpty())
            throw new AssignmentNotFoundException();

        return assignment.get();

    }

    void deleteAllAssignmentsFromCourse(int courseId){
        assignmentRepository.deleteAll(assignmentRepository.findAssignmentsByCourseID(courseId));
    }

    private void deleteAssignment(int assignmentId) throws AssignmentNotFoundException {

        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);

        if(assignment.isEmpty())
            throw new AssignmentNotFoundException();

        assignmentRepository.delete(assignment.get());

    }

    private void saveAssignment(Assignment assignment){
        assignmentRepository.save(assignment);
    }
}
