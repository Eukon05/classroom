package com.eukon05.classroom.UnitTests;

import com.eukon05.classroom.DTOs.CourseUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.AppUserRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import com.eukon05.classroom.Services.AppUserService;
import com.eukon05.classroom.Services.AssignmentService;
import com.eukon05.classroom.Services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CourseServiceTests {

    private CourseService courseService;
    private CourseRepository courseRepository;
    private AppUserCourseRepository aucRepository;
    private AssignmentService assignmentService;
    private AppUserService appUserService;
    private AppUserRepository appUserRepository;

    @BeforeEach
    void initService(){

        courseRepository = Mockito.mock(CourseRepository.class);
        aucRepository = Mockito.mock(AppUserCourseRepository.class);
        assignmentService = Mockito.mock(AssignmentService.class);
        appUserRepository = Mockito.mock(AppUserRepository.class);
        courseService = new CourseService(courseRepository, aucRepository, assignmentService);
        appUserService = Mockito.spy(new AppUserService(appUserRepository, aucRepository, courseService, new BCryptPasswordEncoder()));

    }

    @Test
    void create_course_test() throws UserNotFoundException, InvalidParametersException, MissingParametersException {

        AppUser user = new AppUser("testOne", "testpass", "Test", "One");

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        courseService.createCourse("testOne","Test Course");
        Mockito.verify(courseRepository, Mockito.times(2)).save(Mockito.any(Course.class));
        Mockito.verify(aucRepository).save(Mockito.any(AppUserCourse.class));

        assertEquals("Test Course", user.getCourses().get(0).getCourse().getName());
        assertEquals(user, user.getCourses().get(0).getCourse().getAppUsers().get(0).getAppUser());
        assertTrue(user.getCourses().get(0).getCourse().getInviteCode()!=null && user.getCourses().get(0).getCourse().getInviteCode().length()==6);

    }

    @Test
    void update_course_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException {

        AppUser user = new AppUser("testOne", "testpass", "Test", "One");

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        Course course = new Course("course");
        course.getAppUsers().add(new AppUserCourse(user, course, true));

        Mockito.when(courseRepository.findById(1))
                .thenReturn(Optional.of(course));

        courseService.updateCourse("testOne", 1, "updated");
        assertEquals("updated", course.getName());

    }

    @Test
    void get_course_users_test() throws UserNotFoundException, CourseNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException {

        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        AppUser user2 = new AppUser("testTwo", "testpass", "Test", "Two");

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        Course course = new Course("course");
        course.getAppUsers().add(new AppUserCourse(user, course, true));
        course.getAppUsers().add(new AppUserCourse(user2, course, false));

        Mockito.when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        List<CourseUserDTO> list = courseService.getCourseUsers("testOne", 1);
        assertEquals("testOne", list.get(0).username);
        assertEquals("testTwo", list.get(1).username);
        assertTrue(list.get(0).isTeacher);
        assertFalse(list.get(1).isTeacher);


    }

    @Test
    void update_user_role_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException {

        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        AppUser user2 = new AppUser("testTwo", "testpass", "Test", "Two");

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        Mockito.when(appUserRepository.findAppUserByUsername("testTwo"))
                .thenReturn(Optional.of(user2));

        Course course = new Course("course");
        course.getAppUsers().add(new AppUserCourse(user, course, true));
        course.getAppUsers().add(new AppUserCourse(user2, course, false));

        Mockito.when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        courseService.updateUserRoleInCourse(user.getUsername(), 1, user2.getUsername(), true);
        Mockito.verify(aucRepository).save(Mockito.any(AppUserCourse.class));
        assertTrue(course.getAppUsers().get(1).isTeacher());

    }

    @Test
    void delete_user_from_course_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParametersException, MissingParametersException {

        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        AppUser user2 = new AppUser("testTwo", "testpass", "Test", "Two");

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        Mockito.when(appUserRepository.findAppUserByUsername("testTwo"))
                .thenReturn(Optional.of(user2));

        Course course = new Course("course");
        course.setId(1);
        course.getAppUsers().add(new AppUserCourse(user, course, true));
        course.getAppUsers().add(new AppUserCourse(user2, course, false));

        user2.getCourses().add(new AppUserCourse(user2, course, false));

        assertEquals(2, course.getAppUsers().size());

        Mockito.when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        courseService.deleteUserFromCourse(user.getUsername(), user2.getUsername(), 1);

        assertEquals(1, course.getAppUsers().size());

    }

    @Test
    void delete_course_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException {

        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        Course course = new Course("course");
        //I'm assigning the ID manually here, because Hibernate is not active while using a mocked repo,
        //but the method forceDeleteCourse requires the Course ID to be not null;
        course.setId(1);
        course.getAppUsers().add(new AppUserCourse(user, course, true));
        user.getCourses().add(new AppUserCourse(user, course, true));

        Mockito.when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        courseService.deleteCourse("testOne", 1);
        Mockito.verify(aucRepository).delete(Mockito.any(AppUserCourse.class));
        assertTrue(user.getCourses().isEmpty());
    }

}
