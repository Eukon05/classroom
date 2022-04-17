package com.eukon05.classroom.unitTests;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AssignmentDataDTO;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.AppUserCourseRepository;
import com.eukon05.classroom.repositories.AssignmentRepository;
import com.eukon05.classroom.repositories.CourseRepository;
import com.eukon05.classroom.services.AppUserService;
import com.eukon05.classroom.services.AssignmentService;
import com.eukon05.classroom.services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssignmentServiceTests {

    private CourseService courseService;
    private AssignmentService assignmentService;
    private AssignmentRepository assignmentRepository;
    private CourseRepository courseRepository;
    private AppUserService appUserService;
    private AppUserCourseRepository aucRepository;

    @BeforeEach
    void initService(){

        courseRepository = Mockito.mock(CourseRepository.class);
        aucRepository = Mockito.mock(AppUserCourseRepository.class);
        assignmentRepository = Mockito.mock(AssignmentRepository.class);
        appUserService = Mockito.mock(AppUserService.class);

        assignmentService = new AssignmentService(assignmentRepository);

        courseService = Mockito.spy(new CourseService(courseRepository, aucRepository, assignmentService));
        courseService.setAppUserService(appUserService);

    }

    @Test
    void create_assignment_test() throws UserNotFoundException, InvalidParametersException, MissingParametersException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException {

        AppUser user = new AppUser("testOne", "testpass", "test", "one");

        Course course = new Course("course");
        course.setId(1);

        course.getAppUsers().add(new AppUserCourse(user, course, true));

        Mockito.when(appUserService.getUserByUsername(Mockito.anyString()))
                .thenReturn(user);

        Mockito.when(courseRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(course));

        AssignmentDataDTO dto = new AssignmentDataDTO();
        dto.setTitle("test assignment");
        dto.setContent("test content");
        dto.setLinks(new ArrayList<>());
        dto.getLinks().add("https://github.com/Eukon05");

        assignmentService.createAssignment("testOne", 1, dto);
        Mockito.verify(assignmentRepository).save(Mockito.any(Assignment.class));

    }

    @Test
    void update_assignment_test() throws UserNotFoundException, InvalidParametersException, MissingParametersException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, AssignmentNotFoundException {

        AppUser user = new AppUser("testOne", "testpass", "test", "one");

        Course course = new Course("course");
        course.setId(1);

        course.getAppUsers().add(new AppUserCourse(user, course, true));

        Mockito.when(appUserService.getUserByUsername(Mockito.anyString()))
                .thenReturn(user);

        Mockito.when(courseRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(course));

        Assignment assignment = new Assignment("test", "test", null, course.getId());

        Mockito.when(assignmentRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(assignment));

        AssignmentDataDTO dto = new AssignmentDataDTO();
        dto.setTitle("test assignment");
        dto.setContent("test content");
        dto.setLinks(new ArrayList<>());
        dto.getLinks().add("https://github.com/Eukon05");

        assignmentService.updateAssignment("testOne", 1, 1, dto);
        Mockito.verify(assignmentRepository).save(Mockito.any(Assignment.class));

        assertEquals("test assignment", assignment.getTitle());
        assertEquals("test content", assignment.getContent());
        assertEquals(dto.getLinks(), assignment.getLinks());

    }

    @Test
    void delete_assignment_test() throws UserNotFoundException, InvalidParametersException, MissingParametersException, AccessDeniedException, CourseNotFoundException, AssignmentNotFoundException, UserNotAttendingTheCourseException {

        AppUser user = new AppUser("testOne", "testpass", "test", "one");

        Course course = new Course("course");
        course.setId(1);

        course.getAppUsers().add(new AppUserCourse(user, course, true));

        Assignment assignment = new Assignment("test", "test", null, course.getId());

        Mockito.when(assignmentRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(assignment));

        Mockito.when(appUserService.getUserByUsername(Mockito.anyString()))
                .thenReturn(user);

        Mockito.when(courseRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(course));

        assignmentService.deleteAssignment("testOne", course.getId(), 1);
        Mockito.verify(assignmentRepository).delete(Mockito.any(Assignment.class));

    }

}
