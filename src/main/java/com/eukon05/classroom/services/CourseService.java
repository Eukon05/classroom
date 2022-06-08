package com.eukon05.classroom.services;

import com.eukon05.classroom.builders.InvalidParameterExceptionBuilder;
import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.CourseUserDTO;
import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.AccessDeniedException;
import com.eukon05.classroom.exceptions.CourseNotFoundException;
import com.eukon05.classroom.exceptions.UserNotAttendingTheCourseException;
import com.eukon05.classroom.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.eukon05.classroom.ParamUtils.checkObject;
import static com.eukon05.classroom.ParamUtils.checkStringAndTrim;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final AppUserService appUserService;

    Course getCourseById(long id){
        checkObject(id, ParamType.courseId);
        return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
    }

    @Transactional
    public void createCourse(String username, String courseName){
        AppUser appUser = appUserService.getUserByUsername(username);

        courseName = checkStringAndTrim(courseName, ParamType.courseName);

        Optional<Course> tmp;
        String random;

        do {
            random = generateCourseKey();
            tmp = courseRepository.findCourseByInviteCode(random);
        }
        while (tmp.isPresent());

        Course course = new Course(courseName, random);

        appUserService.addCourse(appUser,course,true);
        courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(String username, long courseId){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher()) {
            throw new AccessDeniedException();
        }

        courseRepository.delete(course);
    }

    @Transactional
    public void updateCourse(String username, long courseId, String newName){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher()) {
            throw new AccessDeniedException();
        }

        course.setName(checkStringAndTrim(newName, ParamType.courseName));
    }


    public List<CourseUserDTO> getCourseUsers(String username, long courseId){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        getAppUserCourse(appUser, course);

        //We need to return the users in their DTO form,
        //because we have to include the isTeacher value

        return course.getAppUserCourses().stream()
                .map(auc -> new CourseUserDTO(auc.getAppUser().getUsername(), auc.getAppUser().getName(), auc.getAppUser().getSurname(), auc.isTeacher()))
                .toList();
    }

    @Transactional
    public void updateUserRoleInCourse(String principalUsername, long courseId, String username, boolean isTeacher){
        if(principalUsername.equals(username)) {
            throw new InvalidParameterExceptionBuilder(ExceptionType.selfRoleChange, ParamType.username).build();
        }

        checkObject(isTeacher, ParamType.isTeacher);

        AppUser appUser = appUserService.getUserByUsername(principalUsername);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher()) {
            throw new AccessDeniedException();
        }

        AppUser tmpAppUser = appUserService.getUserByUsername(username);

        getAppUserCourse(tmpAppUser, course).setTeacher(isTeacher);
    }

    public void deleteUserFromCourse(String principalUsername, String username, long courseId){
        AppUser appUser = appUserService.getUserByUsername(principalUsername);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher() && !principalUsername.equals(username)) {
            throw new AccessDeniedException();
        }

        appUserService.leaveCourse(username, courseId);
    }

    AppUserCourse getAppUserCourse(AppUser appUser, Course course){
        return course.getAppUserCourses()
                .stream()
                .filter(auc -> auc.getAppUser().equals(appUser))
                .findFirst()
                .orElseThrow(() -> new UserNotAttendingTheCourseException(appUser.getUsername(), course.getId()));
    }


    private String generateCourseKey(){
        StringBuilder result = new StringBuilder();
        String characters = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        Random random = new Random();

        for(int i = 0; i< 6; i++){
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

}
