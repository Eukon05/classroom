package com.eukon05.classroom.Services;

import com.eukon05.classroom.DTOs.CourseUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
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

    Course getCourseByInviteCode(String inviteCode) throws CourseNotFoundException, MissingParametersException {

        valueCheck(inviteCode);

        Optional<Course> courseOptional = courseRepository.findCourseByInviteCode(inviteCode);
        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();
        return courseOptional.get();
        
    }

    Course getCourseById(int id) throws CourseNotFoundException, MissingParametersException {

        valueCheck(id);

        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();
        return courseOptional.get();
    }
    
    public void createCourse(String username, String courseName) throws MissingParametersException, UserNotFoundException, InvalidParametersException {

        AppUser appUser = appUserService.getUserByUsername(username);

        valueCheck(courseName);

        Course course = new Course(courseName);
        Optional<Course> tmp;
        String random;

        do {
            random = generateCourseKey(6);
            tmp = courseRepository.findCourseByInviteCode(random);

        } while (tmp.isPresent());

        course.setInviteCode(random);

        courseRepository.save(course);
        appUserCourseRepository.save(appUserService.addCourse(appUser, course, true));
        courseRepository.save(course);
        appUserService.saveUser(appUser);

    }

    public void deleteCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, InvalidParametersException, MissingParametersException, UserNotAttendingTheCourseException {

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

    public void updateCourse(String username, int courseId, String newName) throws CourseNotFoundException, UserNotFoundException, AccessDeniedException, MissingParametersException, InvalidParametersException, UserNotAttendingTheCourseException {

        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        valueCheck(newName);

        course.setName(newName);

        saveCourse(course);

    }


    public List<CourseUserDTO> getCourseUsers(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, InvalidParametersException, MissingParametersException, UserNotAttendingTheCourseException {

        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        getAppUserCourse(appUser, course);

        List<CourseUserDTO> users = new ArrayList<>();

        for(AppUserCourse auc : course.getAppUsers()){
            CourseUserDTO dto = new CourseUserDTO();
            AppUser au = auc.getAppUser();
            dto.username = au.getUsername();
            dto.name = au.getName();
            dto.surname = au.getSurname();
            dto.isTeacher = auc.isTeacher();
            users.add(dto);
        }

        return users;

    }

    public void updateUserRoleInCourse(String principalUsername, int courseId, String username, boolean isTeacher) throws UserNotFoundException, InvalidParametersException, MissingParametersException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException {

        AppUser appUser = appUserService.getUserByUsername(principalUsername);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
            throw new AccessDeniedException();

        AppUser tmpAppUser = appUserService.getUserByUsername(username);

        if(appUser.equals(tmpAppUser))
            throw new InvalidParametersException();

        valueCheck(isTeacher);

        AppUserCourse tmpAuc = getAppUserCourse(tmpAppUser, course);

        tmpAuc.setTeacher(isTeacher);

        appUserCourseRepository.save(tmpAuc);

    }

    public void deleteUserFromCourse(String principalUsername, String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException, InvalidParametersException, MissingParametersException {

        AppUser appUser = appUserService.getUserByUsername(principalUsername);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(!auc.isTeacher())
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


    private String generateCourseKey(int length){
        StringBuilder result = new StringBuilder();
        String characters = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        Random random = new Random();

        for(int i=0; i<length; i++){
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    void saveCourse(Course course) {
        courseRepository.save(course);
    }

    void forceDeleteCourse(Course course) {
        courseRepository.delete(course);
        assignmentService.deleteAllAssignmentsFromCourse(course.getId());
    }


}
