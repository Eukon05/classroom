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
import com.eukon05.classroom.services.AssignmentService;
import com.eukon05.classroom.services.CourseService;
import org.junit.jupiter.api.BeforeAll;
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
    private CourseService courseService;
    private CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final AppUserDTO userOneDto = new AppUserDTO();

    @BeforeAll
    static void initDto(){
        userOneDto.setUsername("testOne");
        userOneDto.setPassword("drowssap");
        userOneDto.setName("Test");
        userOneDto.setSurname("One");
    }

    @BeforeEach
    void initService(){

        appUserRepository = Mockito.mock(AppUserRepository.class);
        aucRepository = Mockito.mock(AppUserCourseRepository.class);
        courseRepository = Mockito.mock(CourseRepository.class);
        courseService = Mockito.spy(new CourseService(courseRepository, aucRepository, Mockito.mock(AssignmentService.class)));

        appUserService = new AppUserService(appUserRepository, aucRepository, courseService, passwordEncoder);

    }

    @Test
    void create_user_test() throws InvalidParameterException, UsernameTakenException, MissingParametersException {

        appUserService.createUser(userOneDto);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));

    }

    @Test
    void get_user_by_username_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException {

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname())));

        assertEquals("Test", appUserService.getUserByUsername("testOne").getName());

    }

    @Test
    void update_user_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException {

        AppUser user = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        AppUserUpdateDTO updatedDto = new AppUserUpdateDTO();
        updatedDto.setPassword("password");

        appUserService.updateUser("testOne", updatedDto);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
        assertTrue(passwordEncoder.matches("password", user.getPassword()));

    }

    @Test
    void get_user_courses_test() throws UserNotFoundException, InvalidParameterException, MissingParametersException {

        AppUser test = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());
        test.getCourses().add(new AppUserCourse(test, new Course("course"), false));

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(test));

        assertFalse(appUserService.getUserCourses("testOne").isEmpty());
        assertEquals("course", appUserService.getUserCourses("testOne").get(0).getName());

    }

    @Test
    void join_course_test() throws UserNotFoundException, CourseNotFoundException, InvalidParameterException, MissingParametersException {

        AppUser user = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());
        Course testCourse = new Course("TestCourse");
        testCourse.setId(1);

        Mockito.when(courseRepository.findCourseByInviteCode("tstcod"))
                .thenReturn(Optional.of(testCourse));

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        appUserService.joinCourse("testOne", "tstcod");

        Mockito.verify(aucRepository).save(Mockito.any(AppUserCourse.class));
        Mockito.verify(courseRepository).save(Mockito.any(Course.class));
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
        assertEquals(testCourse, user.getCourses().get(0).getCourse());

    }

    @Test
    void leave_course_test() throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParameterException, MissingParametersException {

        AppUser user = new AppUser(userOneDto.getUsername(), userOneDto.getPassword(), userOneDto.getName(), userOneDto.getSurname());

        Course testCourse = new Course("TestCourse");
        testCourse.setId(1);

        AppUserCourse auc = new AppUserCourse(user, testCourse, false);

        user.getCourses().add(auc);
        testCourse.getAppUsers().add(auc);

        Mockito.when(courseRepository.findById(1))
                .thenReturn(Optional.of(testCourse));

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        appUserService.leaveCourse("testOne", testCourse.getId());
        Mockito.verify(aucRepository).delete(Mockito.any(AppUserCourse.class));
        Mockito.verify(courseRepository).delete(Mockito.any(Course.class));
        assertTrue(user.getCourses().isEmpty());

    }


}
