package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.*;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<Object> createCourse(Principal principal, @RequestBody CourseDataDTO dto){

        try{
            courseService.createCourse(principal.getName(), dto.name);
            return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateCourse(Principal principal, @PathVariable int id, @RequestBody CourseDataDTO dto){

        try{
            courseService.updateCourse(principal.getName(), id, dto.name);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteCourse(Principal principal, @PathVariable int id){

        try{
            courseService.deleteCourse(principal.getName(), id);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | InvalidParametersException | MissingParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }


    @PutMapping("{id}/users")
    public ResponseEntity<Object> updateUserRole(Principal principal, @PathVariable int id, @RequestBody CourseUserUpdateDTO dto){

        try{
            courseService.updateUserRoleInCourse(principal.getName(), id, dto.username, dto.isTeacher);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }


    }

    @GetMapping("{id}/users")
    public ResponseEntity<Object> getUsers(Principal principal, @PathVariable int id){

        try{
            return new ResponseEntity<>(courseService.getCourseUsers(principal.getName(), id), HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }


    }

    @DeleteMapping("{id}/users")
    public ResponseEntity<Object> deleteUserFromCourse(Principal principal, @RequestBody CourseUserDeleteDTO dto, @PathVariable int id){

        try{
            courseService.deleteUserFromCourse(principal.getName(), dto.username, id);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | UserNotAttendingTheCourseException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }


    }

}
