package com.eukon05.classroom.UnitTests;


import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.AppUserUpdateDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.AppUserRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import com.eukon05.classroom.Services.AssignmentService;
import com.eukon05.classroom.Services.CourseService;
import com.eukon05.classroom.Services.AppUserService;
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
        userOneDto.username = "testOne";
        userOneDto.password = "drowssap";
        userOneDto.name = "Test";
        userOneDto.surname = "One";
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
    void create_user_test() throws InvalidParametersException, UsernameTakenException, MissingParametersException {

        appUserService.createUser(userOneDto);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));

    }

    @Test
    void get_user_by_username_test() throws UserNotFoundException, InvalidParametersException, MissingParametersException {

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(new AppUser(userOneDto.username, userOneDto.password, userOneDto.name, userOneDto.surname)));

        assertEquals("Test", appUserService.getUserByUsername("testOne").getName());

    }

    @Test
    void update_user_test() throws UserNotFoundException, InvalidParametersException, MissingParametersException {

        AppUser user = new AppUser(userOneDto.username, userOneDto.password, userOneDto.name, userOneDto.surname);

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        AppUserUpdateDTO updatedDto = new AppUserUpdateDTO();
        updatedDto.password = "password";

        appUserService.updateUser("testOne", updatedDto);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
        assertTrue(passwordEncoder.matches("password", user.getPassword()));

    }

    @Test
    void get_user_courses_test() throws UserNotFoundException, InvalidParametersException, MissingParametersException {

        AppUser test = new AppUser(userOneDto.username, userOneDto.password, userOneDto.name, userOneDto.surname);
        test.getCourses().add(new AppUserCourse(test, new Course("course"), false));

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(test));

        assertFalse(appUserService.getUserCourses("testOne").isEmpty());
        assertEquals("course", appUserService.getUserCourses("testOne").get(0).getName());

    }

    @Test
    void join_course_test() throws UserNotFoundException, CourseNotFoundException, InvalidParametersException, MissingParametersException {

        AppUser user = new AppUser(userOneDto.username, userOneDto.password, userOneDto.name, userOneDto.surname);
        Course testCourse = new Course("TestCourse");
        testCourse.setId(1);

        Mockito.when(courseRepository.findCourseByInviteCode("invitecode"))
                .thenReturn(Optional.of(testCourse));

        Mockito.when(appUserRepository.findAppUserByUsername("testOne"))
                .thenReturn(Optional.of(user));

        appUserService.joinCourse("testOne", "invitecode");

        Mockito.verify(aucRepository).save(Mockito.any(AppUserCourse.class));
        //Mockito.verify(courseService).saveCourse(Mockito.any(Course.class));
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
        assertEquals(testCourse, user.getCourses().get(0).getCourse());

    }

    @Test
    void leave_course_test() throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParametersException, MissingParametersException {

        AppUser user = new AppUser(userOneDto.username, userOneDto.password, userOneDto.name, userOneDto.surname);

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
        //Mockito.verify(courseService).forceDeleteCourse(Mockito.any(Course.class));
        assertTrue(user.getCourses().isEmpty());

    }


}
