package com.eukon05.classroom.Services;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.AppUserCourse;
import com.eukon05.classroom.Domains.Assignment;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final AppUserCourseRepository appUserCourseRepository;
    private final AssignmentService assignmentService;

    @Setter
    private UserService userService;

    public Course getCourseByInviteCode(String inviteCode) throws CourseNotFoundException {

        Optional<Course> courseOptional = courseRepository.findCourseByInviteCode(inviteCode);
        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();
        return courseOptional.get();
        
    }

    public Course getCourseById(int id) throws CourseNotFoundException {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();
        return courseOptional.get();
    }
    
    public void createCourse(String username, String courseName) throws MissingParametersException, UserNotFoundException {

        if(courseName==null)
            throw new MissingParametersException();

        AppUser appUser = userService.getUserByUsername(username);
        Course course = new Course(courseName);
        Optional<Course> tmp;
        String random;

        while(true) {
            random = generateCourseKey(6);
            tmp = courseRepository.findCourseByInviteCode(random);

            if(tmp.isEmpty()){
                break;
            }

        }

        course.setInviteCode(random);

        int id = (int) (courseRepository.count() + 1);

        course.setId(id);
        courseRepository.save(course);
        appUserCourseRepository.save(userService.addCourse(appUser, course, true));
        courseRepository.save(course);
        userService.updateUser(appUser);

    }

    public void deleteCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        for(int i = course.getAppUsers().size()-1; i>=0; i--){
            AppUserCourse appUserCourse = course.getAppUsers().get(i);
            AppUser user = appUserCourse.getAppUser();
            appUserCourseRepository.delete(userService.removeCourse(user, course));
            userService.updateUser(user);
        }

        forceDeleteCourse(course);

    }


    public List<Assignment> getAssignments(String username, int id) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(id);

        if(getAppUserCourse(appUser, course)==null)
            throw new AccessDeniedException();

        return assignmentService.getAssignmentsForCourse(id);

    }

    public void createAssignment(String username, int courseId, AssignmentDTO assignmentDTO)
            throws UserNotFoundException, CourseNotFoundException, MissingParametersException, AccessDeniedException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        if(assignmentDTO.title==null)
            throw new MissingParametersException();

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        assignmentService.createAssignment(courseId, assignmentDTO.title, assignmentDTO.content, assignmentDTO.link);

    }

    public void deleteAssignment(String username, int courseId, int assignmentId)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        assignmentService.deleteAssignment(assignmentId);
    }

    public List<AppUserDTO> getCourseUsers(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        if(getAppUserCourse(appUser, course)==null)
            throw new AccessDeniedException();

        List<AppUserDTO> users = new ArrayList<>();

        for(AppUserCourse auc : course.getAppUsers()){
            AppUserDTO dto = new AppUserDTO();
            dto.username = appUser.getUsername();
            dto.name = appUser.getName();
            dto.surname = appUser.getSurname();
            dto.isTeacher = auc.isTeacher();
            users.add(dto);
        }

        return users;

    }

    private AppUserCourse getAppUserCourse(AppUser appUser, Course course) {

        for(AppUserCourse auc : course.getAppUsers()){
            if(auc.getAppUser().equals(appUser)){
                return  auc;
            }
        }

        return null;
    }


    private String generateCourseKey(int length){
        String result = "";
        String characters = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        Random random = new Random();

        for(int i=0; i<length; i++){
            result+=characters.charAt(random.nextInt(characters.length()));
        }

        return result;
    }

    public void updateCourse(Course course) {
        courseRepository.save(course);
    }

    public void forceDeleteCourse(Course course) {
        courseRepository.delete(course);
        assignmentService.deleteAllAssignmentsFromCourse(course.getId());
    }


}
