package com.eukon05.classroom.controllers;

import com.eukon05.classroom.dtos.AppUserDTO;
import com.eukon05.classroom.dtos.AppUserUpdateDTO;
import com.eukon05.classroom.dtos.CourseInviteCodeDTO;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.services.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new user (registers an account)")
    public ResponseEntity<Object> createUser(@RequestBody AppUserDTO appUserDto) throws InvalidParameterException, UsernameTakenException, MissingParametersException {
        appUserService.createUser(appUserDto);
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }

    @GetMapping("self")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Gets the details of the authenticated user")
    public ResponseEntity<Object> getYourself(Principal principal) throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        return new ResponseEntity<>(appUserService.getUserByUsername(principal.getName()), HttpStatus.OK);
    }

    @PutMapping("self")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Updates the user's account with provided details")
    public ResponseEntity<Object> updateYourself(Principal principal, @RequestBody AppUserUpdateDTO appUserUpdateDto) throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        appUserService.updateUser(principal.getName(), appUserUpdateDto);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @DeleteMapping("self")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes the user's account")
    public ResponseEntity<Object> deleteYourself(Principal principal) throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        appUserService.deleteUser(principal.getName());
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }


    @GetMapping("self/courses")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a list of all courses that the user attends")
    public ResponseEntity<Object> getYourCourses(Principal principal) throws UserNotFoundException, InvalidParameterException, MissingParametersException {
        return new ResponseEntity<>(appUserService.getUserCourses(principal.getName()), HttpStatus.OK);
    }

    @PostMapping("self/courses")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Allows the user to join a course after providing a valid invite code")
    public ResponseEntity<Object> joinCourse(Principal principal, @RequestBody CourseInviteCodeDTO dto) throws UserNotFoundException, CourseNotFoundException, InvalidParameterException, MissingParametersException {
        appUserService.joinCourse(principal.getName(), dto.getInviteCode());
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @DeleteMapping("self/courses/{id}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Allows the user to leave a specified course")
    public ResponseEntity<Object> leaveCourse(Principal principal, @PathVariable int id) throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, InvalidParameterException, MissingParametersException {
        appUserService.leaveCourse(principal.getName(), id);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

}
