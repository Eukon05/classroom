package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody AppUserDTO appUserDto){

        try{
            userService.createUser(appUserDto);
        }
        catch (UsernameTakenException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }

    @GetMapping("self")
    public ResponseEntity<Object> getYourself(Principal principal){

        try{
            return new ResponseEntity<>(userService.getUserByUsername(principal.getName()), HttpStatus.OK);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("self")
    public ResponseEntity<Object> updateYourself(Principal principal, @RequestBody AppUserDTO appUserDto){

        try{
            userService.updateUser(principal.getName(), appUserDto);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("self")
    public ResponseEntity<Object> deleteYourself(Principal principal){

        try{
            userService.deleteUser(principal.getName());
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }


    }


    @GetMapping("self/courses")
    public ResponseEntity<Object> getYourCourses(Principal principal){
        try{
            return new ResponseEntity<>(userService.getUserCourses(principal.getName()), HttpStatus.OK);
        }
        catch(UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("self/courses")
    public ResponseEntity<Object> joinCourse(Principal principal, @RequestBody CourseDTO courseDTO){
        try{
            userService.joinCourse(principal.getName(), courseDTO.inviteCode);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch(UserNotFoundException | CourseNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("self/courses/{id}")
    public ResponseEntity<Object> leaveCourse(Principal principal, @PathVariable int id) {
        try {
            userService.leaveCourse(principal.getName(), id);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (UserNotFoundException | CourseNotFoundException | UserNotAttendingTheCourseException | MissingParametersException | InvalidParametersException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
