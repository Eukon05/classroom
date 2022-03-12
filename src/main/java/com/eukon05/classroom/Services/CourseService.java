package com.eukon05.classroom.Services;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
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
public class CourseService extends AbstractResourceService{

    private final CourseRepository courseRepository;
    private final AppUserCourseRepository appUserCourseRepository;
    private final AssignmentService assignmentService;

    @Setter
    private UserService userService;

    public Course getCourseByInviteCode(String inviteCode) throws CourseNotFoundException, MissingParametersException {

        valueCheck(inviteCode);

        Optional<Course> courseOptional = courseRepository.findCourseByInviteCode(inviteCode);
        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();
        return courseOptional.get();
        
    }

    public Course getCourseById(int id) throws CourseNotFoundException, MissingParametersException {

        valueCheck(id);

        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();
        return courseOptional.get();
    }
    
    public void createCourse(String username, String courseName) throws MissingParametersException, UserNotFoundException, InvalidParametersException {

        if(courseName==null)
            throw new MissingParametersException();

        AppUser appUser = userService.getUserByUsername(username);

        valueCheck(courseName);

        Course course = new Course(courseName);
        Optional<Course> tmp;
        String random;

        do {
            random = generateCourseKey(6);
            tmp = courseRepository.findCourseByInviteCode(random);

        } while (tmp.isPresent());

        course.setInviteCode(random);

        //It should be possible to simply generate the id with an annotation in the Course class, but this should work for now
        int id = courseRepository.findMaxCourseId().orElse(1);

        course.setId(id);
        courseRepository.save(course);
        appUserCourseRepository.save(userService.addCourse(appUser, course, true));
        courseRepository.save(course);
        userService.saveUser(appUser);

    }

    public void deleteCourse(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, InvalidParametersException, MissingParametersException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        for(int i = course.getAppUsers().size()-1; i>=0; i--){
            AppUserCourse appUserCourse = course.getAppUsers().get(i);
            AppUser user = appUserCourse.getAppUser();
            appUserCourseRepository.delete(userService.removeCourse(user, course));
            userService.saveUser(user);
        }

        forceDeleteCourse(course);

    }

    public void updateCourse(String username, int courseId, CourseDTO dto) throws CourseNotFoundException, UserNotFoundException, AccessDeniedException, MissingParametersException, InvalidParametersException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        valueCheck(dto.name);

        course.setName(dto.name);

        saveCourse(course);


    }


    public List<Assignment> getAssignments(String username, int id) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, InvalidParametersException, MissingParametersException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(id);

        if(getAppUserCourse(appUser, course)==null)
            throw new AccessDeniedException();

        return assignmentService.getAssignmentsForCourse(id);

    }

    public void createAssignment(String username, int courseId, AssignmentDTO assignmentDTO)
            throws UserNotFoundException, CourseNotFoundException, MissingParametersException, AccessDeniedException, InvalidParametersException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        valueCheck(assignmentDTO.title);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        assignmentService.createAssignment(courseId, assignmentDTO.title, assignmentDTO.content, assignmentDTO.links);

    }

    public void updateAssignment(String username, int courseId, int assignmentId, AssignmentDTO dto)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, MissingParametersException, InvalidParametersException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        Assignment assignment = assignmentService.getAssignmentById(assignmentId);

        if(dto.title == null && dto.content == null && dto.links == null)
            throw new MissingParametersException();

        if(dto.title != null)
            assignment.setTitle(dto.title);

        if(dto.content != null)
            assignment.setContent(dto.content);

        if(dto.links != null)
            assignment.setLinks(dto.links);

        assignmentService.saveAssignment(assignment);

    }

    public void deleteAssignment(String username, int courseId, int assignmentId)
            throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, AssignmentNotFoundException, InvalidParametersException, MissingParametersException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        valueCheck(assignmentId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        assignmentService.deleteAssignment(assignmentId);
    }

    public List<AppUserDTO> getCourseUsers(String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, InvalidParametersException, MissingParametersException {

        AppUser appUser = userService.getUserByUsername(username);
        Course course = getCourseById(courseId);

        if(getAppUserCourse(appUser, course)==null)
            throw new AccessDeniedException();

        List<AppUserDTO> users = new ArrayList<>();

        for(AppUserCourse auc : course.getAppUsers()){
            AppUserDTO dto = new AppUserDTO();
            AppUser au = auc.getAppUser();
            dto.username = au.getUsername();
            dto.name = au.getName();
            dto.surname = au.getSurname();
            dto.isTeacher = auc.isTeacher();
            users.add(dto);
        }

        return users;

    }

    public void deleteUserFromCourse(String principalUsername, String username, int courseId) throws UserNotFoundException, CourseNotFoundException, AccessDeniedException, UserNotAttendingTheCourseException, InvalidParametersException, MissingParametersException {

        AppUser appUser = userService.getUserByUsername(principalUsername);
        Course course = getCourseById(courseId);

        AppUserCourse auc = getAppUserCourse(appUser, course);

        if(auc==null || !auc.isTeacher())
            throw new AccessDeniedException();

        userService.leaveCourse(username, courseId);
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
        StringBuilder result = new StringBuilder();
        String characters = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        Random random = new Random();

        for(int i=0; i<length; i++){
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    public void forceDeleteCourse(Course course) {
        courseRepository.delete(course);
        assignmentService.deleteAllAssignmentsFromCourse(course.getId());
    }


}
