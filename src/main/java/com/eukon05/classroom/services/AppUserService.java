package com.eukon05.classroom.services;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AppUserDTO;
import com.eukon05.classroom.dtos.AppUserUpdateDTO;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.AppUserCourseRepository;
import com.eukon05.classroom.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserService extends AbstractResourceService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final CourseService courseService;
    private final AppUserCourseRepository appUserCourseRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository repository, AppUserCourseRepository appUserCourseRepository, CourseService courseService, PasswordEncoder passwordEncoder){
        appUserRepository=repository;
        this.appUserCourseRepository=appUserCourseRepository;
        this.courseService=courseService;
        this.courseService.setAppUserService(this);
        this.passwordEncoder = passwordEncoder;
    }

    void saveUser(AppUser user){
        appUserRepository.save(user);
    }

    public void createUser(AppUserDTO appUserDTO) throws MissingParametersException, UsernameTakenException, InvalidParameterException {
        checkCredential(appUserDTO.getUsername(), ParamType.username);

        Optional<AppUser> tmpUser = appUserRepository.findAppUserByUsername(appUserDTO.getUsername());

        if(tmpUser.isPresent())
            throw new UsernameTakenException(appUserDTO.getUsername());

        checkCredential(appUserDTO.getPassword(), ParamType.password);

        appUserDTO.setName(checkStringAndTrim(appUserDTO.getName(), ParamType.name));
        appUserDTO.setSurname(checkStringAndTrim(appUserDTO.getSurname(), ParamType.surname));

        AppUser user = new AppUser(appUserDTO.getUsername(), passwordEncoder.encode(appUserDTO.getPassword()), appUserDTO.getName(), appUserDTO.getSurname());
        appUserRepository.save(user);
    }

    public AppUser getUserByUsername(String username) throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        checkCredential(username, ParamType.username);
        Optional<AppUser> user = appUserRepository.findAppUserByUsername(username);
        return user.orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {return getUserByUsername(username);}
        //This is necessary to override the loadUserByUsername method properly, I should think of a cleaner fix though
        catch (UserNotFoundException | InvalidParameterException | MissingParametersException e){throw new UsernameNotFoundException(e.getMessage());}
    }

    public void updateUser(String username, AppUserUpdateDTO dto) throws UserNotFoundException, MissingParametersException, InvalidParameterException {
        AppUser user = getUserByUsername(username);

        if(dto.getName() == null && dto.getSurname() == null && dto.getPassword() == null)
            throw new MissingParametersException();

        if(dto.getPassword()!=null) {
            checkCredential(dto.getPassword(), ParamType.password);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if(dto.getName()!=null)
            user.setName(checkStringAndTrim(dto.getName(), ParamType.name));

        if(dto.getSurname()!=null)
            user.setSurname(checkStringAndTrim(dto.getSurname(), ParamType.surname));

        saveUser(user);
    }

    public void deleteUser(String username) throws UserNotFoundException, MissingParametersException, InvalidParameterException {
        AppUser user = getUserByUsername(username);
        List<Course> courses = new ArrayList<>();

        for(AppUserCourse appUserCourse : user.getCourses()){
            courses.add(appUserCourse.getCourse());
        }

        for(Course course : courses) {
            appUserCourseRepository.delete(removeCourse(user, course));

            if (course.getAppUsers().isEmpty())
                courseService.forceDeleteCourse(course);
            else
                reassignTeacher(course);
        }

        appUserRepository.delete(user);
    }

    public List<Course> getUserCourses(String username) throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        AppUser user = getUserByUsername(username);
        List<Course> courses = new ArrayList<>();

        for(AppUserCourse appUserCourse : user.getCourses()){
            courses.add(appUserCourse.getCourse());
        }

        return courses;
    }

    public void joinCourse(String username, String inviteCode) throws UserNotFoundException, CourseNotFoundException, InvalidParameterException, MissingParametersException {
        AppUser user = getUserByUsername(username);
        inviteCode = checkStringAndTrim(inviteCode, ParamType.inviteCode);

        Course course = courseService.getCourseByInviteCode(inviteCode);
        appUserCourseRepository.save(addCourse(user, course, false));
        courseService.saveCourse(course);
        appUserRepository.save(user);
    }

    public void leaveCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParameterException, MissingParametersException {
        AppUser user = getUserByUsername(username);

        checkObject(courseId, ParamType.courseId);

        Course course = courseService.getCourseById(courseId);
        AppUserCourse auc = removeCourse(user, course);

        if(auc==null)
            throw new UserNotAttendingTheCourseException(username, courseId);

        appUserCourseRepository.delete(auc);

        if(course.getAppUsers().isEmpty()) {
            courseService.forceDeleteCourse(course);
            return;
        }

        reassignTeacher(course);
    }


    AppUserCourse addCourse(AppUser user, Course course, boolean isTeacher){
        AppUserCourse appUserCourse = new AppUserCourse(user, course, isTeacher);
        user.getCourses().add(appUserCourse);
        course.getAppUsers().add(appUserCourse);

        return appUserCourse;
    }


    AppUserCourse removeCourse(AppUser user, Course course){
        for(AppUserCourse appUserCourse : user.getCourses()){
            if(appUserCourse.getCourse().equals(course) && appUserCourse.getAppUser().equals(user)){
                user.getCourses().remove(appUserCourse);
                appUserCourse.getCourse().getAppUsers().remove(appUserCourse);
                appUserCourse.setAppUser(null);
                appUserCourse.setCourse(null);
                return appUserCourse;
            }
        }

        return null;
    }

    private void reassignTeacher(Course course) {
        boolean courseStillHasATeacher = false;

        for(AppUserCourse auc2 : course.getAppUsers()){
            if(auc2.isTeacher()) {
                courseStillHasATeacher = true;
                break;
            }
        }

        if(!courseStillHasATeacher){
            AppUserCourse newTeacher = course.getAppUsers().get(0);
            newTeacher.setTeacher(true);
            appUserCourseRepository.save(newTeacher);
        }
    }



}
