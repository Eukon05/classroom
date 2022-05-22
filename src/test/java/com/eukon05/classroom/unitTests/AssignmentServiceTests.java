package com.eukon05.classroom.unitTests;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AssignmentDataDTO;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.CourseRepository;
import com.eukon05.classroom.services.AppUserService;
import com.eukon05.classroom.services.AssignmentService;
import com.eukon05.classroom.services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssignmentServiceTests {

    private AssignmentService assignmentService;
    private CourseRepository courseRepository;
    private AppUserService appUserService;


    @BeforeEach
    void initService(){
        courseRepository = Mockito.mock(CourseRepository.class);
        appUserService = Mockito.mock(AppUserService.class);
        assignmentService = new AssignmentService(appUserService, Mockito.spy(new CourseService(courseRepository, appUserService)));
    }

    @Test
    void create_assignment_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException {
        AppUser user = new AppUser("testOne", "testpass", "test", "one");
        Course course = new Course("course", "tstcod");

        course.getAppUserCourses().add(new AppUserCourse(user, course, true));

        Mockito.when(appUserService.getUserByUsername(Mockito.anyString()))
                .thenReturn(user);

        Mockito.when(courseRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(course));

        AssignmentDataDTO dto = new AssignmentDataDTO("test assignment", "test content", new HashSet<>());
        dto.getLinks().add("https://github.com/Eukon05");

        assignmentService.createAssignment("testOne", 1L, dto);
        assertEquals(1, course.getAssignments().size());
    }

    @Test
    void update_assignment_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, AssignmentNotFoundException {
        AppUser user = new AppUser("testOne", "testpass", "test", "one");
        Course course = new Course("course", "tstcod");

        course.getAppUserCourses().add(new AppUserCourse(user, course, true));

        Mockito.when(appUserService.getUserByUsername(Mockito.anyString()))
                .thenReturn(user);

        Mockito.when(courseRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(course));

        Assignment assignment = Mockito.spy(new Assignment("test", "test", null));
        course.getAssignments().add(assignment);

        Mockito.when(assignment.getId()).thenReturn(1L);

        AssignmentDataDTO dto = new AssignmentDataDTO("test assignment", "test content", new HashSet<>());

        assignmentService.updateAssignment("testOne", 1L, 1L, dto);

        assertEquals("test assignment", assignment.getTitle());
        assertEquals("test content", assignment.getContent());
        assertEquals(dto.getLinks(), assignment.getLinks());
    }

    @Test
    void delete_assignment_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException, AccessDeniedException, CourseNotFoundException, AssignmentNotFoundException, UserNotAttendingTheCourseException {
        AppUser user = new AppUser("testOne", "testpass", "test", "one");
        Course course = new Course("course", "tstcod");

        course.getAppUserCourses().add(new AppUserCourse(user, course, true));

        Mockito.when(appUserService.getUserByUsername(Mockito.anyString()))
                .thenReturn(user);

        Mockito.when(courseRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(course));

        Assignment assignment = Mockito.spy(new Assignment("test", "test", null));
        course.getAssignments().add(assignment);

        Mockito.when(assignment.getId()).thenReturn(1L);

        assignmentService.deleteAssignment("testOne", 1L, 1L);
        assertEquals(0, course.getAssignments().size());
    }

}
