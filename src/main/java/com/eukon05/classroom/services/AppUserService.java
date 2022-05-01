package com.eukon05.classroom.services;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AppUserDTO;
import com.eukon05.classroom.dtos.AppUserUpdateDTO;
import com.eukon05.classroom.enums.Param;
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
        isCredentialValid(appUserDTO.getUsername(), Param.username);

        Optional<AppUser> tmpUser = appUserRepository.findAppUserByUsername(appUserDTO.getUsername());

        if(tmpUser.isPresent())
            throw new UsernameTakenException(appUserDTO.getUsername());

        isCredentialValid(appUserDTO.getPassword(), Param.password);

        if(appUserDTO.getName()==null)
            throw new MissingParametersException(Param.name);

        isValid(appUserDTO.getName().trim(), Param.name);

        if(appUserDTO.getSurname()==null)
            throw new MissingParametersException(Param.surname);

        isValid(appUserDTO.getSurname().trim(), Param.surname);

        AppUser user = new AppUser(appUserDTO.getUsername(), appUserDTO.getPassword(), appUserDTO.getName().trim(), appUserDTO.getSurname().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserRepository.save(user);
    }

    public AppUser getUserByUsername(String username) throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        isCredentialValid(username, Param.username);

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
            isCredentialValid(dto.getPassword(), Param.password);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if(dto.getName()!=null) {
            isValid(dto.getName().trim(), Param.name);
            user.setName(dto.getName().trim());
        }

        if(dto.getSurname()!=null) {
            isValid(dto.getSurname(), Param.surname);
            user.setSurname(dto.getSurname().trim());
        }

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

        isValid(inviteCode, Param.inviteCode);

        Course course = courseService.getCourseByInviteCode(inviteCode);
        appUserCourseRepository.save(addCourse(user, course, false));
        courseService.saveCourse(course);
        appUserRepository.save(user);
    }

    public void leaveCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParameterException, MissingParametersException {
        AppUser user = getUserByUsername(username);

        isValid(courseId, Param.courseId);

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
