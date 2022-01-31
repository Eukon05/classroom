package com.eukon05.classroom.Services;

import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Domains.Course;
import com.eukon05.classroom.Repositories.AppUserCourseRepository;
import com.eukon05.classroom.Repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final AppUserCourseRepository appUserCourseRepository;

    @Setter
    private UserService userService;

    public Course getCourseByInviteCode(String inviteCode) throws Exception {

        Optional<Course> courseOptional = courseRepository.findCourseByInviteCode(inviteCode);
        if(courseOptional.isEmpty())
            throw new Exception("Course with this invite code doesn't exist");
        return courseOptional.get();
        
    }

    public Course getCourseById(int id) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isEmpty())
            throw new Exception("Course with this id doesn't exist");
        return courseOptional.get();
    }
    
    public void createCourse(String username, String courseName){

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


        /*
        The problem is as follows: the AppUserCourseId isn't saved to the DB after adding the course to the user.
        You need to save the ID to the DB after calling the addCourse() method from user object
         */

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
}
