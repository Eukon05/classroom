package com.eukon05.classroom.unitTests;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.CourseUserDTO;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.AppUserCourseRepository;
import com.eukon05.classroom.repositories.AppUserRepository;
import com.eukon05.classroom.repositories.CourseRepository;
import com.eukon05.classroom.services.AppUserService;
import com.eukon05.classroom.services.CourseService;
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
    private AppUserService appUserService;
    private AppUserRepository appUserRepository;

    @BeforeEach
    void initService(){

        courseRepository = Mockito.mock(CourseRepository.class);
        AppUserCourseRepository aucRepository = Mockito.mock(AppUserCourseRepository.class);
        appUserRepository = Mockito.mock(AppUserRepository.class);
        appUserService = Mockito.spy(new AppUserService(appUserRepository, courseRepository, aucRepository, new BCryptPasswordEncoder()));
        courseService = new CourseService(courseRepository, appUserService);

    }

    @Test
    void create_course_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        AppUser user = new AppUser("testOne", "testpass", "Test", "One");

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        courseService.createCourse("testOne","Test Course");
        Mockito.verify(courseRepository).save(Mockito.any(Course.class));

        assertEquals("Test Course", user.getAppUserCourses().get(0).getCourse().getName());
        assertEquals(user, user.getAppUserCourses().get(0).getCourse().getAppUserCourses().get(0).getAppUser());
        assertTrue(user.getAppUserCourses().get(0).getCourse().getInviteCode()!=null && user.getAppUserCourses().get(0).getCourse().getInviteCode().length()==6);
    }

    @Test
    void update_course_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParameterException, UserNotAttendingTheCourseException, MissingParametersException {
        AppUser user = new AppUser("testOne", "testpass", "Test", "One");

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        Course course = new Course("course", "tstcod");
        course.getAppUserCourses().add(new AppUserCourse(user, course, true));

        Mockito.when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        courseService.updateCourse("testOne", 1, "updated");
        assertEquals("updated", course.getName());
    }

    @Test
    void get_course_users_test() throws UserNotFoundException, CourseNotFoundException, InvalidParameterException, UserNotAttendingTheCourseException, MissingParametersException {
        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        AppUser user2 = new AppUser("testTwo", "testpass", "Test", "Two");

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        Course course = new Course("course", "tstcod");
        course.getAppUserCourses().add(new AppUserCourse(user, course, true));
        course.getAppUserCourses().add(new AppUserCourse(user2, course, false));

        Mockito.when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        List<CourseUserDTO> list = courseService.getCourseUsers("testOne", 1);
        assertEquals("testOne", list.get(0).getUsername());
        assertEquals("testTwo", list.get(1).getUsername());
        assertTrue(list.get(0).getIsTeacher());
        assertFalse(list.get(1).getIsTeacher());
    }

    @Test
    void update_user_role_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParameterException, UserNotAttendingTheCourseException, MissingParametersException {
        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        AppUser user2 = new AppUser("testTwo", "testpass", "Test", "Two");

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        Mockito.when(appUserRepository.findById("testTwo"))
                .thenReturn(Optional.of(user2));

        Course course = new Course("course", "tstcod");
        course.getAppUserCourses().add(new AppUserCourse(user, course, true));
        course.getAppUserCourses().add(new AppUserCourse(user2, course, false));

        Mockito.when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.updateUserRoleInCourse(user.getUsername(), 1, user2.getUsername(), true);
        assertTrue(course.getAppUserCourses().get(1).isTeacher());
    }

    @Test
    void delete_user_from_course_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParameterException, MissingParametersException {
        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        AppUser user2 = new AppUser("testTwo", "testpass", "Test", "Two");

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        Mockito.when(appUserRepository.findById("testTwo"))
                .thenReturn(Optional.of(user2));

        Course course = new Course("course", "tstcod");
        course.getAppUserCourses().add(new AppUserCourse(user, course, true));
        course.getAppUserCourses().add(new AppUserCourse(user2, course, false));

        user2.getAppUserCourses().add(new AppUserCourse(user2, course, false));

        assertEquals(2, course.getAppUserCourses().size());

        Mockito.when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteUserFromCourse(user.getUsername(), user2.getUsername(), 1);
        Mockito.verify(appUserService).leaveCourse("testTwo", 1);

        assertEquals(1, course.getAppUserCourses().size());
    }

    @Test
    void delete_course_test() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParameterException, UserNotAttendingTheCourseException, MissingParametersException {
        AppUser user = new AppUser("testOne", "testpass", "Test", "One");
        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        Course course = new Course("course", "tstcod");
        course.getAppUserCourses().add(new AppUserCourse(user, course, true));
        user.getAppUserCourses().add(new AppUserCourse(user, course, true));

        Mockito.when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse("testOne", 1);
        Mockito.verify(courseRepository).delete(Mockito.any(Course.class));
    }

}
