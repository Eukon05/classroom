package com.eukon05.classroom.services;

import com.eukon05.classroom.builders.InvalidParameterExceptionBuilder;
import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.CourseUserDTO;
import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.AppUserCourseRepository;
import com.eukon05.classroom.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CourseService extends AbstractResourceService{

    private final CourseRepository courseRepository;
    private final AppUserCourseRepository appUserCourseRepository;
    private final AssignmentService assignmentService;
    private AppUserService appUserService;

    public CourseService(CourseRepository courseRepository, AppUserCourseRepository appUserCourseRepository, AssignmentService assignmentService){
        this.courseRepository=courseRepository;
        this.appUserCourseRepository=appUserCourseRepository;
        this.assignmentService=assignmentService;
        this.assignmentService.setCourseService(this);
    }

    public void setAppUserService(AppUserService appUserService){
        this.appUserService = appUserService;
        this.assignmentService.setAppUserService(appUserService);
    }

    Course getCourseByInviteCode(String inviteCode) throws CourseNotFoundException, MissingParametersException, InvalidParameterException {
        String trimmed = checkStringAndTrim(inviteCode, ParamType.inviteCode);

        if(trimmed.length()<6)
            throw new InvalidParameterExceptionBuilder(ExceptionType.tooShort, ParamType.inviteCode).build();

        Optional<Course> courseOptional = courseRepository.findCourseByInviteCode(trimmed);
        return courseOptional.orElseThrow(() -> new CourseNotFoundException(trimmed));
    }

    Course getCourseById(int id) throws CourseNotFoundException, MissingParametersException {
        checkObject(id, ParamType.courseId);
        Optional<Course> courseOptional = courseRepository.findById(id);
        return courseOptional.orElseThrow(() -> new CourseNotFoundException(id));
    }
    
    public void createCourse(String username, String courseName) throws MissingParametersException, UserNotFoundException, InvalidParameterException {
        AppUser appUser = appUserService.getUserByUsername(username);

        courseName = checkStringAndTrim(courseName, ParamType.courseName);

        Course course = new Course(courseName);
        Optional<Course> tmp;
        String random;

        do {
            random = generateCourseKey();
            tmp = courseRepository.findCourseByInviteCode(random);
        }
        while (tmp.isPresent());

        course.setInviteCode(random);

        courseRepository.save(course);
        appUserCourseRepository.save(appUserService.addCourse(appUser, course, true));
        courseRepository.save(course);
        appUserService.saveUser(appUser);
    }

    public void deleteCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, InvalidParameterException, MissingParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        for(int i = course.getAppUsers().size()-1; i>=0; i--){
            AppUserCourse appUserCourse = course.getAppUsers().get(i);
            AppUser user = appUserCourse.getAppUser();
            appUserCourseRepository.delete(appUserService.removeCourse(user, course));
            appUserService.saveUser(user);
        }

        forceDeleteCourse(course);
    }

    public void updateCourse(String username, int courseId, String newName) throws CourseNotFoundException, UserNotFoundException, AccessDeniedException, MissingParametersException, InvalidParameterException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        newName = checkStringAndTrim(newName.trim(), ParamType.courseName);

        course.setName(newName.trim());

        saveCourse(course);
    }


    public List<CourseUserDTO> getCourseUsers(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, InvalidParameterException, MissingParametersException, UserNotAttendingTheCourseException {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        getAppUserCourse(appUser, course);

        //We need to return the users in their DTO form,
        //because we have to include the isTeacher value

        List<CourseUserDTO> users = new ArrayList<>();

        for(AppUserCourse auc : course.getAppUsers()){
            CourseUserDTO dto = new CourseUserDTO();
            AppUser au = auc.getAppUser();
            dto.setUsername(au.getUsername());
            dto.setName(au.getName());
            dto.setSurname(au.getSurname());
            dto.setTeacher(auc.isTeacher());
            users.add(dto);
        }

        return users;
    }

    public void updateUserRoleInCourse(String principalUsername, int courseId, String username, boolean isTeacher) throws UserNotFoundException, InvalidParameterException, MissingParametersException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException {
        if(principalUsername.equals(username))
            throw new InvalidParameterExceptionBuilder(ExceptionType.selfRoleChange, ParamType.username).build();

        checkObject(isTeacher, ParamType.isTeacher);

        AppUser appUser = appUserService.getUserByUsername(principalUsername);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        AppUser tmpAppUser = appUserService.getUserByUsername(username);

        AppUserCourse tmpAuc = getAppUserCourse(tmpAppUser, course);

        tmpAuc.setTeacher(isTeacher);

        appUserCourseRepository.save(tmpAuc);
    }

    public void deleteUserFromCourse(String principalUsername, String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException, InvalidParameterException, MissingParametersException {
        AppUser appUser = appUserService.getUserByUsername(principalUsername);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher() && !principalUsername.equals(username))
            throw new AccessDeniedException();

        appUserService.leaveCourse(username, courseId);
    }

    //-------------------------------------------------------------------------------
    //INTERNAL METHODS
    //-------------------------------------------------------------------------------

    AppUserCourse getAppUserCourse(AppUser appUser, Course course) throws UserNotAttendingTheCourseException {
        for(AppUserCourse auc : course.getAppUsers()){
            if(auc.getAppUser().equals(appUser)){
                return  auc;
            }
        }
        throw new UserNotAttendingTheCourseException(appUser.getUsername(), course.getId());
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

    void saveCourse(Course course) {
        courseRepository.save(course);
    }

    void forceDeleteCourse(Course course) {
        assignmentService.deleteAllAssignmentsFromCourse(course);
        courseRepository.delete(course);
    }


}
