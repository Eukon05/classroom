package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
import com.eukon05.classroom.Exceptions.CourseNotFoundException;
import com.eukon05.classroom.Exceptions.MissingParametersException;
import com.eukon05.classroom.Exceptions.UserNotFoundException;
import com.eukon05.classroom.Exceptions.UsernameTakenException;
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
        catch (UsernameTakenException | MissingParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }

    @DeleteMapping("self")
    public ResponseEntity<Object> deleteUser(Principal principal){

        try{
            userService.deleteUser(principal.getName());
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }


    }


    @GetMapping("self/courses")
    public ResponseEntity<Object> getUserCourses(Principal principal){
        try{
            return new ResponseEntity<>(userService.getUserCourses(principal.getName()), HttpStatus.OK);
        }
        catch(UserNotFoundException ex){
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
        catch(UserNotFoundException | CourseNotFoundException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("self/courses/{id}")
    public ResponseEntity<Object> leaveCourse(Principal principal, @PathVariable int id){
        try{
            userService.leaveCourse(principal.getName(), id);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch(UserNotFoundException | CourseNotFoundException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
