package com.eukon05.classroom.services;

import com.eukon05.classroom.builders.InvalidParameterExceptionBuilder;
import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.AppUserCourse;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AppUserDTO;
import com.eukon05.classroom.dtos.AppUserUpdateDTO;
import com.eukon05.classroom.enums.ExceptionType;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.repositories.AppUserCourseRepository;
import com.eukon05.classroom.repositories.AppUserRepository;
import com.eukon05.classroom.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.eukon05.classroom.ParamUtils.*;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final CourseRepository courseRepository;
    private final AppUserCourseRepository appUserCourseRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(AppUserDTO appUserDTO){
        checkCredential(appUserDTO.getUsername(), ParamType.username);

        appUserRepository.findById(appUserDTO.getUsername()).ifPresent(user -> {throw new UsernameTakenException(user.getUsername());});

        checkCredential(appUserDTO.getPassword(), ParamType.password);

        AppUser user = new AppUser(appUserDTO.getUsername(),
                passwordEncoder.encode(appUserDTO.getPassword()),
                checkStringAndTrim(appUserDTO.getName(), ParamType.name),
                checkStringAndTrim(appUserDTO.getSurname(), ParamType.surname));

        appUserRepository.save(user);
    }

    public AppUser getUserByUsername(String username){
        checkCredential(username, ParamType.username);
        return appUserRepository.findById(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        try {return getUserByUsername(username);}
        //This is necessary to override the loadUserByUsername method properly, I should think of a cleaner fix though
        catch (UserNotFoundException | InvalidParameterException | MissingParametersException e){throw new UsernameNotFoundException(e.getMessage());}
    }

    @Transactional
    public void updateUser(String username, AppUserUpdateDTO dto){
        AppUser user = getUserByUsername(username);

        if(dto.getName() == null && dto.getSurname() == null && dto.getPassword() == null) {
            throw new MissingParametersException();
        }

        Optional.ofNullable(dto.getPassword()).ifPresent(password -> {
            checkCredential(password, ParamType.password);
            user.setPassword(passwordEncoder.encode(password));
        });

        Optional.ofNullable(dto.getName()).ifPresent(name -> user.setName(checkStringAndTrim(name, ParamType.name)));

        Optional.ofNullable(dto.getSurname()).ifPresent(surname -> user.setName(checkStringAndTrim(surname, ParamType.name)));
    }

    @Transactional
    public void deleteUser(String username){
        AppUser user = getUserByUsername(username);

        //why am I required to use "toList()" here? Without it, I get a "concurrent modification exception" and I have no idea why
        user.getAppUserCourses().stream().map(AppUserCourse::getCourse).toList().forEach(course -> {
            appUserCourseRepository.delete(removeCourse(user, course)); //and why do I have to use the repository? Shouldn't this object be deleted by a cascade?

            if (course.getAppUserCourses().isEmpty()) {
                courseRepository.delete(course);
            } else {
                reassignTeacher(course);
            }
        });
        appUserRepository.delete(user);
    }

    public List<Course> getUserCourses(String username){
        return getUserByUsername(username).getAppUserCourses().stream().map(AppUserCourse::getCourse).toList();
    }

    @Transactional
    public void joinCourse(String username, String inviteCode){
        AppUser user = getUserByUsername(username);
        String trimmed = checkStringAndTrim(inviteCode, ParamType.inviteCode);

        if(trimmed.length()<6) {
            throw new InvalidParameterExceptionBuilder(ExceptionType.tooShort, ParamType.inviteCode).build();
        }

        Course course = courseRepository.findCourseByInviteCode(trimmed).orElseThrow(() -> new CourseNotFoundException(trimmed));
        addCourse(user, course, false);
    }

    @Transactional
    public void leaveCourse(String username, long courseId){
        AppUser user = getUserByUsername(username);

        checkObject(courseId, ParamType.courseId);

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
        appUserCourseRepository.delete(removeCourse(user, course));

        if(course.getAppUserCourses().isEmpty()) {
            courseRepository.delete(course);
            return;
        }

        reassignTeacher(course);
    }

    void addCourse(AppUser user, Course course, boolean isTeacher){
        AppUserCourse appUserCourse = new AppUserCourse(user, course, isTeacher);

        if(user.getAppUserCourses().contains(appUserCourse)){
            throw new UserAlreadyAttendingTheCourseException(user.getUsername(), course.getId());
        }

        user.getAppUserCourses().add(appUserCourse);
        course.getAppUserCourses().add(appUserCourse);
    }


    private AppUserCourse removeCourse(AppUser user, Course course){
        AppUserCourse appUserCourse = user.getAppUserCourses()
                .stream()
                .filter(auc -> auc.getCourse().equals(course))
                .findFirst()
                .orElseThrow(() -> new UserNotAttendingTheCourseException(user.getUsername(), course.getId()));

        user.getAppUserCourses().remove(appUserCourse);
        appUserCourse.getCourse().getAppUserCourses().remove(appUserCourse);
        return appUserCourse;
    }

    private void reassignTeacher(Course course) {
        course.getAppUserCourses().stream().filter(AppUserCourse::isTeacher)
                .findFirst().ifPresentOrElse(user -> {}, () -> course.getAppUserCourses().get(0).setTeacher(true));
    }

}
