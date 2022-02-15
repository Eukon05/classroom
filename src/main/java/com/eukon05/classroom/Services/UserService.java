package com.eukon05.classroom.Services;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.CourseNotFoundException;
import com.eukon05.classroom.Exceptions.MissingParametersException;
import com.eukon05.classroom.Exceptions.UserNotFoundException;
import com.eukon05.classroom.Exceptions.UsernameTakenException;
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
public class UserService implements UserDetailsService {

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

    public void updateUser(AppUser user){
        appUserRepository.save(user);
    }


    public void createUser(AppUserDTO appUserDTO) throws MissingParametersException, UsernameTakenException {
        Optional<AppUser> tmpUser = appUserRepository.findAppUserByUsername(appUserDTO.username);

        if(tmpUser.isPresent())
            throw new UsernameTakenException(appUserDTO.username);

        if(appUserDTO.username==null || appUserDTO.password==null || appUserDTO.name==null || appUserDTO.surname==null)
            throw new MissingParametersException();

        AppUser user = new AppUser(appUserDTO.username, appUserDTO.password, appUserDTO.name, appUserDTO.surname);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserRepository.save(user);

    }


    public AppUser getUserByUsername(String username) throws UserNotFoundException {
        Optional<AppUser> user = appUserRepository.findAppUserByUsername(username);
        if(user.isEmpty())
            throw new UserNotFoundException(username);
        return user.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {return getUserByUsername(username);}

        catch (UserNotFoundException e){throw new UsernameNotFoundException(e.getMessage());}

    }

    public void deleteUser(String username) throws UserNotFoundException {

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
                courseService.updateCourse(course);

            appUserRepository.save(user);

        }

        appUserRepository.delete(user);
    }


    public List<Course> getUserCourses(String username) throws UserNotFoundException {
        AppUser user = getUserByUsername(username);
        List<Course> courses = new ArrayList<>();

        for(AppUserCourse appUserCourse : user.getCourses()){
            courses.add(appUserCourse.getCourse());
        }

        return courses;
    }

    public void joinCourse(String username, String inviteCode) throws UserNotFoundException, CourseNotFoundException {
        AppUser user = getUserByUsername(username);
        Course course = courseService.getCourseByInviteCode(inviteCode);
        appUserCourseRepository.save(addCourse(user, course, false));
        courseService.updateCourse(course);
        appUserRepository.save(user);

    }

    public void leaveCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException {
        AppUser user = getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);
        appUserCourseRepository.delete(removeCourse(user, course));

        if(course.getAppUsers().isEmpty())
            courseService.forceDeleteCourse(course);
        else
            courseService.updateCourse(course);

        appUserRepository.save(user);
    }



    public AppUserCourse addCourse(AppUser user, Course course, boolean isTeacher){
        AppUserCourse appUserCourse = new AppUserCourse(user, course, isTeacher);
        user.getCourses().add(appUserCourse);
        course.getAppUsers().add(appUserCourse);

        return appUserCourse;
    }


    public AppUserCourse removeCourse(AppUser user, Course course){

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
