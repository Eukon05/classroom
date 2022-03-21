package com.eukon05.classroom;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserRepository;
import com.eukon05.classroom.Repositories.AssignmentRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import com.eukon05.classroom.Services.CourseService;
import com.eukon05.classroom.Services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class UserServiceTests extends AbstractTests{

    @Autowired
    public UserServiceTests(AppUserRepository userRepository,
                            CourseRepository courseRepository,
                            AssignmentRepository assignmentRepository,
                            CourseService courseService,
                            UserService userService) {
        super(userRepository, courseRepository, assignmentRepository, courseService, userService);
    }

    @Test
    void createUserTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException {

        userService.createUser(userOneDTO);
        assertTrue(userRepository.findAppUserByUsername(userOneDTO.username).isPresent());

    }

    @Test
    void updateUserTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException {

        createUserTest();
        AppUserDTO dto = new AppUserDTO();
        dto.name = "Update";
        dto.surname = "Test";
        dto.password = "newPass";

        userService.updateUser(userOneDTO.username, dto);
        AppUser user = userRepository.findAppUserByUsername(userOneDTO.username).get();

        assertEquals(user.getName(), dto.name);
        assertEquals(user.getSurname(), dto.surname);
        assertTrue(new BCryptPasswordEncoder().matches(dto.password, user.getPassword()));

    }

    @Test
    @Transactional
    void deleteUserTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException, CourseNotFoundException {

        //creates two users
        createUserTest();
        userService.createUser(userTwoDTO);

        //creates a course for them to join
        courseService.createCourse(userOneDTO.username, "testcourse");

        //checks if the course exists
        List<Course> courses = userService.getUserCourses(userOneDTO.username);
        assertTrue(!courses.isEmpty() && courses.get(0).getName().equals("testcourse"));

        //joins the course on the second user's account
        userService.joinCourse(userTwoDTO.username, courses.get(0).getInviteCode());

        //checks if the user joined the course
        courses = userService.getUserCourses(userTwoDTO.username);
        assertTrue(!courses.isEmpty() && courses.get(0).getName().equals("testcourse"));
        assertEquals(courses.get(0).getAppUsers().size(), 2);

        //deletes the user
        userService.deleteUser(userOneDTO.username);
        assertTrue(userRepository.findAppUserByUsername(userOneDTO.username).isEmpty());

        //checks if the course ownership has been transferred to the second user
        courses = userService.getUserCourses(userTwoDTO.username);
        assertEquals(courses.get(0).getAppUsers().size(), 1);
        assertTrue(courses.get(0).getAppUsers().get(0).isTeacher());


    }

    @Test
    @Transactional
    void leaveCourseTest() throws UserNotFoundException, InvalidParametersException, MissingParametersException, CourseNotFoundException, UserNotAttendingTheCourseException, UsernameTakenException {

        //creates two users
        createUserTest();
        userService.createUser(userTwoDTO);

        //creates a course for them to join
        courseService.createCourse(userOneDTO.username, "testcourse");

        //checks if the course exists
        List<Course> courses = userService.getUserCourses(userOneDTO.username);
        assertTrue(!courses.isEmpty() && courses.get(0).getName().equals("testcourse"));

        //joins the course on the second user's account
        userService.joinCourse(userTwoDTO.username, courses.get(0).getInviteCode());

        //checks if the user joined the course
        courses = userService.getUserCourses(userTwoDTO.username);
        assertTrue(!courses.isEmpty() && courses.get(0).getName().equals("testcourse"));
        assertEquals(courses.get(0).getAppUsers().size(), 2);

        //user one leaves the course
        userService.leaveCourse(userOneDTO.username, courses.get(0).getId());
        assertTrue(userService.getUserCourses(userOneDTO.username).isEmpty());

        //checks if the course ownership has been transferred to the second user
        courses = userService.getUserCourses(userTwoDTO.username);
        assertEquals(courses.get(0).getAppUsers().size(), 1);
        assertTrue(courses.get(0).getAppUsers().get(0).isTeacher());
    }

}
