package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.AppUserUpdateDTO;
import com.eukon05.classroom.DTOs.CourseInviteCodeDTO;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Services.AppUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody AppUserDTO appUserDto){

        try{
            appUserService.createUser(appUserDto);
        }
        catch (UsernameTakenException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }

    @GetMapping("self")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Object> getYourself(Principal principal){

        try{
            return new ResponseEntity<>(appUserService.getUserByUsername(principal.getName()), HttpStatus.OK);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("self")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Object> updateYourself(Principal principal, @RequestBody AppUserUpdateDTO appUserUpdateDto){

        try{
            appUserService.updateUser(principal.getName(), appUserUpdateDto);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("self")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Object> deleteYourself(Principal principal){

        try{
            appUserService.deleteUser(principal.getName());
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }


    }


    @GetMapping("self/courses")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Object> getYourCourses(Principal principal){
        try{
            return new ResponseEntity<>(appUserService.getUserCourses(principal.getName()), HttpStatus.OK);
        }
        catch(UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("self/courses")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Object> joinCourse(Principal principal, @RequestBody CourseInviteCodeDTO dto){
        try{
            appUserService.joinCourse(principal.getName(), dto.inviteCode);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch(UserNotFoundException | CourseNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("self/courses/{id}")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Object> leaveCourse(Principal principal, @PathVariable int id) {
        try {
            appUserService.leaveCourse(principal.getName(), id);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (UserNotFoundException | CourseNotFoundException | UserNotAttendingTheCourseException | MissingParametersException | InvalidParametersException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
