package com.eukon05.classroom;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
import com.eukon05.classroom.Repositories.AppUserRepository;
import com.eukon05.classroom.Repositories.AssignmentRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import com.eukon05.classroom.Services.CourseService;
import com.eukon05.classroom.Services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

@ActiveProfiles("test")
public abstract class AbstractTests {

    protected final AppUserRepository userRepository;
    protected final CourseRepository courseRepository;
    protected final AssignmentRepository assignmentRepository;
    protected final CourseService courseService;
    protected final UserService userService;

    protected static final AppUserDTO userOneDTO = new AppUserDTO();
    protected static final AppUserDTO userTwoDTO = new AppUserDTO();
    protected static final CourseDTO courseDTO = new CourseDTO();
    protected static final AssignmentDTO assignmentDTO = new AssignmentDTO();

    AbstractTests(AppUserRepository userRepository,
                            CourseRepository courseRepository,
                            AssignmentRepository assignmentRepository,
                            CourseService courseService,
                            UserService userService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseService = courseService;
        this.userService = userService;
    }

    @BeforeAll
    protected static void init(){
        userOneDTO.username = "testaccountpleasedontuse";
        userOneDTO.password = "junit";
        userOneDTO.name = "Admin One";
        userOneDTO.surname = "Junit";

        userTwoDTO.username = "secondaccount";
        userTwoDTO.password = "secretcode";
        userTwoDTO.name = "User Two";
        userTwoDTO.surname = "Junit";

        courseDTO.name = "Test Course";

        assignmentDTO.title = "Test Assignment";
        assignmentDTO.links = new ArrayList<>();
        assignmentDTO.links.add("https://google/com");
        assignmentDTO.links.add("https://github.com");
    }

    @BeforeEach
    protected void reset(){

        userRepository.deleteAll();
        courseRepository.deleteAll();
        assignmentRepository.deleteAll();

    }

}
