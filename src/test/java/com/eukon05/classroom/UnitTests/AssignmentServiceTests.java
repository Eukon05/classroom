package com.eukon05.classroom.UnitTests;

import com.eukon05.classroom.DTOs.AssignmentDataDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Assignment;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.AssignmentRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import com.eukon05.classroom.Services.AppUserService;
import com.eukon05.classroom.Services.AssignmentService;
import com.eukon05.classroom.Services.CourseService;
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
        dto.title = "test assignment";
        dto.content = "test content";
        dto.links = new ArrayList<>();
        dto.links.add("https://github.com/Eukon05");

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
        dto.title = "test assignment";
        dto.content = "test content";
        dto.links = new ArrayList<>();
        dto.links.add("https://github.com/Eukon05");

        assignmentService.updateAssignment("testOne", 1, 1, dto);
        Mockito.verify(assignmentRepository).save(Mockito.any(Assignment.class));

        assertEquals("test assignment", assignment.getTitle());
        assertEquals("test content", assignment.getContent());
        assertEquals(dto.links, assignment.getLinks());

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
