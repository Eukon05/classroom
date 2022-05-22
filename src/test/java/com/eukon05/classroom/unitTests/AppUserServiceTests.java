package com.eukon05.classroom.unitTests;


import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AppUserDTO;
import com.eukon05.classroom.dtos.AppUserUpdateDTO;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.AppUserCourseRepository;
import com.eukon05.classroom.repositories.AppUserRepository;
import com.eukon05.classroom.repositories.CourseRepository;
import com.eukon05.classroom.services.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AppUserServiceTests {

    private AppUserService appUserService;
    private AppUserRepository appUserRepository;
    private AppUserCourseRepository aucRepository;
    private CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AppUserDTO userOneDto = new AppUserDTO("testOne", "drowssap", "Test", "One");

    @BeforeEach
    void initService(){
        appUserRepository = Mockito.mock(AppUserRepository.class);
        aucRepository = Mockito.mock(AppUserCourseRepository.class);
        courseRepository = Mockito.mock(CourseRepository.class);
        appUserService = new AppUserService(appUserRepository, courseRepository, aucRepository, passwordEncoder);
    }

    @Test
    void create_user_test() throws InvalidParameterException, UsernameTakenException, MissingParametersException {
        appUserService.createUser(userOneDto);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
    }

    @Test
    void get_user_by_username_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname())));

        assertEquals("Test", appUserService.getUserByUsername("testOne").getName());
    }

    @Test
    void update_user_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        AppUser user = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        AppUserUpdateDTO updatedDto = new AppUserUpdateDTO("password", null, null);

        appUserService.updateUser("testOne", updatedDto);
        assertTrue(passwordEncoder.matches("password", user.getPassword()));
    }

    @Test
    void get_user_courses_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        AppUser test = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());
        test.getAppUserCourses().add(new AppUserCourse(test, new Course("course", "tstcod"), false));

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(test));

        assertFalse(appUserService.getUserCourses("testOne").isEmpty());
        assertEquals("course", appUserService.getUserCourses("testOne").get(0).getName());
    }

    @Test
    void join_course_test() throws UserNotFoundException, CourseNotFoundException, InvalidParameterException, MissingParametersException {
        AppUser user = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());
        Course testCourse = new Course("TestCourse", "tstcod");

        Mockito.when(courseRepository.findCourseByInviteCode("tstcod"))
                .thenReturn(Optional.of(testCourse));

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        appUserService.joinCourse("testOne", "tstcod");
        assertEquals(testCourse, user.getAppUserCourses().get(0).getCourse());
    }

    @Test
    void leave_course_test() throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParameterException, MissingParametersException {
        AppUser user = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());
        Course testCourse = new Course("TestCourse", "tstcod");

        AppUserCourse auc = new AppUserCourse(user, testCourse, false);

        user.getAppUserCourses().add(auc);
        testCourse.getAppUserCourses().add(auc);

        Mockito.when(courseRepository.findById(1L))
                .thenReturn(Optional.of(testCourse));

        Mockito.when(appUserRepository.findById("testOne"))
                .thenReturn(Optional.of(user));

        appUserService.leaveCourse("testOne", 1L);
        Mockito.verify(aucRepository).delete(Mockito.any(AppUserCourse.class));
        Mockito.verify(courseRepository).delete(Mockito.any(Course.class));
        assertTrue(user.getAppUserCourses().isEmpty());
    }


}
