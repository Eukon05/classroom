package com.eukon05.classroom.controllers;

import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AppUserDTO;
import com.eukon05.classroom.dtos.AppUserUpdateDTO;
import com.eukon05.classroom.dtos.CourseInviteCodeDTO;
import com.eukon05.classroom.services.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new user (registers an account)")
    public String createUser(@RequestBody AppUserDTO appUserDto){
        appUserService.createUser(appUserDto);
        return"SUCCESS";
    }

    @GetMapping("self")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Gets the details of the authenticated user")
    @ResponseStatus(HttpStatus.OK)
    public AppUser getYourself(Principal principal){
        return appUserService.getUserByUsername(principal.getName());
    }

    @PutMapping("self")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Updates the user's account with provided details")
    @ResponseStatus(HttpStatus.OK)
    public String updateYourself(Principal principal, @RequestBody AppUserUpdateDTO appUserUpdateDto){
        appUserService.updateUser(principal.getName(), appUserUpdateDto);
        return "SUCCESS";
    }

    @DeleteMapping("self")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes the user's account")
    @ResponseStatus(HttpStatus.OK)
    public String deleteYourself(Principal principal){
        appUserService.deleteUser(principal.getName());
        return "SUCCESS";
    }


    @GetMapping("self/courses")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a list of all courses that the user attends")
    @ResponseStatus(HttpStatus.OK)
    public List<Course> getYourCourses(Principal principal){
        return appUserService.getUserCourses(principal.getName());
    }

    @PostMapping("self/courses")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Allows the user to join a course after providing a valid invite code")
    @ResponseStatus(HttpStatus.OK)
    public String joinCourse(Principal principal, @RequestBody CourseInviteCodeDTO dto){
        appUserService.joinCourse(principal.getName(), dto.getInviteCode());
        return "SUCCESS";
    }

    @DeleteMapping("self/courses/{id}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Allows the user to leave a specified course")
    @ResponseStatus(HttpStatus.OK)
    public String leaveCourse(Principal principal, @PathVariable int id){
        appUserService.leaveCourse(principal.getName(), id);
        return "SUCCESS";
    }

}
