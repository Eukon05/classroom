package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        catch (Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }


    @GetMapping("self/courses")
    public ResponseEntity<Object> getUserCourses(Principal principal){
        try{
            return new ResponseEntity<>(userService.getUserCourses(principal.getName()), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("self/courses")
    public ResponseEntity<Object> joinCourse(Principal principal, @RequestParam String inviteCode){
        try{
            userService.joinCourse(principal.getName(), inviteCode);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("self/courses")
    public ResponseEntity<Object> leaveCourse(Principal principal, @RequestParam int courseId){
        try{
            userService.leaveCourse(principal.getName(), courseId);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
