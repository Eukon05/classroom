package com.eukon05.classroom.Services;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.AppUserRepository;
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
public class UserService extends AbstractResourceService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final CourseService courseService;
    private final AppUserCourseRepository appUserCourseRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(AppUserRepository repository, AppUserCourseRepository appUserCourseRepository, CourseService courseService, PasswordEncoder passwordEncoder){
        appUserRepository=repository;
        this.appUserCourseRepository=appUserCourseRepository;
        this.courseService=courseService;
        this.courseService.setUserService(this);
        this.passwordEncoder = passwordEncoder;
    }

    void saveUser(AppUser user){
        appUserRepository.save(user);
    }


    public void createUser(AppUserDTO appUserDTO) throws MissingParametersException, UsernameTakenException, InvalidParametersException {

        credentialCheck(appUserDTO.username);

        Optional<AppUser> tmpUser = appUserRepository.findAppUserByUsername(appUserDTO.username);

        if(tmpUser.isPresent())
            throw new UsernameTakenException(appUserDTO.username);

        credentialCheck(appUserDTO.password);
        valueCheck(appUserDTO.name);
        valueCheck(appUserDTO.surname);

        AppUser user = new AppUser(appUserDTO.username, appUserDTO.password, appUserDTO.name, appUserDTO.surname);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserRepository.save(user);

    }

    public AppUser getUserByUsername(String username) throws UserNotFoundException, InvalidParametersException, MissingParametersException {

        credentialCheck(username);

        Optional<AppUser> user = appUserRepository.findAppUserByUsername(username);
        if(user.isEmpty())
            throw new UserNotFoundException(username);
        return user.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {return getUserByUsername(username);}

        catch (UserNotFoundException | InvalidParametersException | MissingParametersException e){throw new UsernameNotFoundException(e.getMessage());}

    }

    public void updateUser(String username, AppUserDTO dto) throws UserNotFoundException, MissingParametersException, InvalidParametersException {

        AppUser user = getUserByUsername(username);

        if(dto.name == null && dto.surname == null && dto.password == null)
            throw new MissingParametersException();

        if((dto.password!=null)) {
            credentialCheck(dto.password);
            user.setPassword(passwordEncoder.encode(dto.password));
        }

        if(dto.name!=null)
            user.setName(dto.name);

        if(dto.surname!=null)
            user.setSurname(dto.surname);

        saveUser(user);

    }

    public void deleteUser(String username) throws UserNotFoundException, MissingParametersException, InvalidParametersException {

        AppUser user = getUserByUsername(username);
        List<Course> courses = new ArrayList<>();

        for(AppUserCourse appUserCourse : user.getCourses()){
            courses.add(appUserCourse.getCourse());
        }

        for(Course course : courses) {

            appUserCourseRepository.delete(removeCourse(user, course));

            if (course.getAppUsers().isEmpty())
                courseService.forceDeleteCourse(course);
            else {

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

        appUserRepository.delete(user);
    }


    public List<Course> getUserCourses(String username) throws UserNotFoundException, InvalidParametersException, MissingParametersException {

        AppUser user = getUserByUsername(username);
        List<Course> courses = new ArrayList<>();

        for(AppUserCourse appUserCourse : user.getCourses()){
            courses.add(appUserCourse.getCourse());
        }

        return courses;
    }

    public void joinCourse(String username, String inviteCode) throws UserNotFoundException, CourseNotFoundException, InvalidParametersException, MissingParametersException {

        AppUser user = getUserByUsername(username);

        valueCheck(inviteCode);

        Course course = courseService.getCourseByInviteCode(inviteCode);
        appUserCourseRepository.save(addCourse(user, course, false));
        courseService.saveCourse(course);
        appUserRepository.save(user);

    }

    public void leaveCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParametersException, MissingParametersException {

        AppUser user = getUserByUsername(username);

        valueCheck(courseId);

        Course course = courseService.getCourseById(courseId);

        AppUserCourse auc = removeCourse(user, course);

        if(auc==null)
            throw new UserNotAttendingTheCourseException(username, courseId);

        appUserCourseRepository.delete(auc);

        if(course.getAppUsers().isEmpty()) {
            courseService.forceDeleteCourse(course);
            return;
        }

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



}
