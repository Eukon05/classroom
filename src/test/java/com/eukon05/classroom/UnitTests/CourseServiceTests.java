package com.eukon05.classroom.UnitTests;

import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import com.eukon05.classroom.Services.AppUserService;
import com.eukon05.classroom.Services.AssignmentService;
import com.eukon05.classroom.Services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CourseServiceTests {

    private CourseService courseService;
    private CourseRepository courseRepository;
    private AppUserCourseRepository aucRepository;
    private AssignmentService assignmentService;
    private AppUserService appUserService;



    @BeforeEach
    void initService(){

        courseRepository = Mockito.mock(CourseRepository.class);
        aucRepository = Mockito.mock(AppUserCourseRepository.class);
        assignmentService = Mockito.mock(AssignmentService.class);
        appUserService = Mockito.mock(AppUserService.class);

        courseService = new CourseService(courseRepository, aucRepository, assignmentService);
        courseService.setAppUserService(appUserService);

    }

    @Test
    void create_course_test(){

    }

}
