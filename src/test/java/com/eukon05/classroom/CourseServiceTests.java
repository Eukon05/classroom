package com.eukon05.classroom;

import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
import com.eukon05.classroom.Domains.Assignment;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserRepository;
import com.eukon05.classroom.Repositories.AssignmentRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import com.eukon05.classroom.Services.CourseService;
import com.eukon05.classroom.Services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CourseServiceTests extends AbstractTests{

    @Autowired
    public CourseServiceTests(AppUserRepository userRepository,
                              CourseRepository courseRepository,
                              AssignmentRepository assignmentRepository,
                              CourseService courseService,
                              UserService userService) {
        super(userRepository, courseRepository, assignmentRepository, courseService, userService);
    }

    void createUserTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException {

        userService.createUser(userOneDTO);
        assertTrue(userRepository.findAppUserByUsername(userOneDTO.username).isPresent());

    }

    List<Course> createCourseTest() throws UserNotFoundException, InvalidParametersException, MissingParametersException {

        courseService.createCourse(userOneDTO.username, "testcourse");

        //checks if the course exists
        List<Course> courses = userService.getUserCourses(userOneDTO.username);
        assertTrue(!courses.isEmpty() && courses.get(0).getName().equals("testcourse"));

        return courses;

    }

    @Test
    @Transactional
    void updateCourseTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException {

        createUserTest();
        Course course = createCourseTest().get(0);

        CourseDTO dto = new CourseDTO();
        dto.name = "newname";

        courseService.updateCourse(userOneDTO.username, course.getId(), dto);

        assertEquals(courseRepository.findById(course.getId()).get().getName(), dto.name);

    }

    @Test
    @Transactional
    void deleteCourseTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException {

        createUserTest();
        Course course = createCourseTest().get(0);

        courseService.deleteCourse(userOneDTO.username, course.getId());

        assertTrue(courseRepository.findById(course.getId()).isEmpty());
        assertTrue(userService.getUserCourses(userOneDTO.username).isEmpty());

    }

    @Test
    @Transactional
    void createAssignmentTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException {

        createUserTest();
        Course course = createCourseTest().get(0);

        courseService.createAssignment(userOneDTO.username, course.getId(), assignmentDTO);

        assertFalse(courseService.getAssignments(userOneDTO.username, course.getId()).isEmpty());
        assertEquals(courseService.getAssignments(userOneDTO.username, course.getId()).get(0).getTitle(), assignmentDTO.title);
        assertEquals(courseService.getAssignments(userOneDTO.username, course.getId()).get(0).getContent(), assignmentDTO.content);
        assertTrue(courseService.getAssignments(userOneDTO.username, course.getId()).get(0).getLinks().contains(assignmentDTO.links.get(0)));

    }

    @Test
    @Transactional
    void updateAssignmentTest() throws UserNotFoundException, InvalidParametersException, MissingParametersException, UsernameTakenException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, AssignmentNotFoundException {

        createUserTest();
        Course course = createCourseTest().get(0);

        courseService.createAssignment(userOneDTO.username, course.getId(), assignmentDTO);

        List<Assignment> assignments = courseService.getAssignments(userOneDTO.username, course.getId());

        assertFalse(assignments.isEmpty());
        assertEquals(assignments.get(0).getTitle(), assignmentDTO.title);
        assertEquals(assignments.get(0).getContent(), assignmentDTO.content);

        AssignmentDTO dto = new AssignmentDTO();
        dto.title = "testtitle";
        dto.content = "testcontent";
        dto.links = assignments.get(0).getLinks();
        dto.links.add("testlink");

        courseService.updateAssignment(userOneDTO.username, course.getId(), assignments.get(0).getId(), dto);

        assertFalse(assignments.isEmpty());
        assertEquals(assignments.get(0).getTitle(), dto.title);
        assertEquals(assignments.get(0).getContent(), dto.content);
        assertTrue(assignments.get(0).getLinks().contains("testlink"));

    }

    @Test
    @Transactional
    void deleteAssignmentTest() throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException, AssignmentNotFoundException, UsernameTakenException {

        createUserTest();
        Course course = createCourseTest().get(0);

        courseService.createAssignment(userOneDTO.username, course.getId(), assignmentDTO);

        List<Assignment> assignments = courseService.getAssignments(userOneDTO.username, course.getId());

        assertFalse(assignments.isEmpty());
        assertEquals(assignments.get(0).getTitle(), assignmentDTO.title);
        assertEquals(assignments.get(0).getContent(), assignmentDTO.content);

        courseService.deleteAssignment(userOneDTO.username, course.getId(), assignments.get(0).getId());

        assignments = courseService.getAssignments(userOneDTO.username, course.getId());

        assertTrue(assignments.isEmpty());

    }

    @Test
    @Transactional
    void getCourseUsersTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException {

        createUserTest();
        Course course = createCourseTest().get(0);

        userService.createUser(userTwoDTO);

        //joins the course on the second user's account
        userService.joinCourse(userTwoDTO.username, course.getInviteCode());

        assertEquals(courseService.getCourseUsers(userOneDTO.username, course.getId()).get(0).username, userOneDTO.username);
        assertEquals(courseService.getCourseUsers(userOneDTO.username, course.getId()).get(1).username, userTwoDTO.username);

    }

    @Test
    @Transactional
    void updateUserRoleInCourseTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException {

        createUserTest();
        Course course = createCourseTest().get(0);

        userService.createUser(userTwoDTO);

        //joins the course on the second user's account
        userService.joinCourse(userTwoDTO.username, course.getInviteCode());

        //checks if the user joined
        assertEquals(courseService.getCourseUsers(userOneDTO.username, course.getId()).get(1).username, userTwoDTO.username);

        courseService.updateUserRoleInCourse(userOneDTO.username, course.getId(), userTwoDTO.username, true);
        assertTrue(courseService.getCourseUsers(userOneDTO.username, course.getId()).get(1).isTeacher);

    }

    @Test
    @Transactional
    void deleteUserFromCourseTest() throws InvalidParametersException, UsernameTakenException, MissingParametersException, UserNotFoundException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException {

        createUserTest();
        Course course = createCourseTest().get(0);

        userService.createUser(userTwoDTO);

        //joins the course on the second user's account
        userService.joinCourse(userTwoDTO.username, course.getInviteCode());

        //checks if the user joined
        assertEquals(courseService.getCourseUsers(userOneDTO.username, course.getId()).get(1).username, userTwoDTO.username);

        courseService.deleteUserFromCourse(userOneDTO.username, userTwoDTO.username, course.getId());
        assertEquals(courseService.getCourseUsers(userOneDTO.username, course.getId()).size(), 1);

    }



}
