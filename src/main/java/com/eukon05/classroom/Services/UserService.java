package com.eukon05.classroom.Services;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.AppUserRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final CourseService courseService;
    private final AppUserCourseRepository appUserCourseRepository;

    @Autowired
    public UserService(AppUserRepository repository, AppUserCourseRepository appUserCourseRepository, CourseService courseService){
        appUserRepository=repository;
        this.appUserCourseRepository=appUserCourseRepository;
        this.courseService=courseService;
        this.courseService.setUserService(this);
    }

    public void updateUser(AppUser user){
        appUserRepository.save(user);
    }


    public void createUser(AppUserDTO appUserDTO) throws Exception {
        Optional<AppUser> tmpUser = appUserRepository.findAppUserByUsername(appUserDTO.username);

        if(tmpUser.isPresent())
            throw new Exception("User with that username already exists");

        if(appUserDTO.username==null || appUserDTO.password==null || appUserDTO.name==null || appUserDTO.surname==null)
            throw new Exception("Missing parameters");

        AppUser user = new AppUser(appUserDTO.username, appUserDTO.password, appUserDTO.name, appUserDTO.surname);
        appUserRepository.save(user);

    }


    public AppUser getUserByUsername(String username){
        Optional<AppUser> user = appUserRepository.findAppUserByUsername(username);
        if(user.isEmpty())
            throw new UsernameNotFoundException("User with username " + username + " not found");
        return user.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }

    public void deleteUser(String username) {

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


    public List<Course> getUserCourses(String username) {
        AppUser user = getUserByUsername(username);
        List<Course> courses = new ArrayList<>();

        for(AppUserCourse appUserCourse : user.getCourses()){
            courses.add(appUserCourse.getCourse());
        }

        return courses;
    }

    public void joinCourse(String username, String inviteCode) throws Exception {
        AppUser user = getUserByUsername(username);
        Course course = courseService.getCourseByInviteCode(inviteCode);
        appUserCourseRepository.save(addCourse(user, course, false));
        courseService.updateCourse(course);
        appUserRepository.save(user);

    }

    public void leaveCourse(String username, int courseId) throws Exception {
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
