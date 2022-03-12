package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
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
    public ResponseEntity<Object> createCourse(Principal principal, @RequestBody CourseDTO courseDTO){

        try{
            courseService.createCourse(principal.getName(), courseDTO.name);
            return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
        }
        catch (UserNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateCourse(Principal principal, @PathVariable int id, @RequestBody CourseDTO dto){

        try{
            courseService.updateCourse(principal.getName(), id, dto);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("{id}/assignments")
    public ResponseEntity<Object> getAssignments(Principal principal, @PathVariable int id){

        try{
            return new ResponseEntity<>(courseService.getAssignments(principal.getName(), id), HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | InvalidParametersException | MissingParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("{id}/assignments")
    public ResponseEntity<Object> createAssignment(Principal principal, @PathVariable int id, @RequestBody AssignmentDTO dto){

        try{
            courseService.createAssignment(principal.getName(), id, dto);
            return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
        }
        catch (UserNotFoundException | MissingParametersException | CourseNotFoundException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PutMapping("{id}/assignments/{assignmentId}")
    public ResponseEntity<Object> updateAssignment(Principal principal, @PathVariable int id, @PathVariable int assignmentId, @RequestBody AssignmentDTO dto){

        try{
            courseService.updateAssignment(principal.getName(), id, assignmentId, dto);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | AssignmentNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @DeleteMapping("{id}/assignments/{assignmentId}")
    public ResponseEntity<Object> deleteAssignment(Principal principal, @PathVariable int id, @PathVariable int assignmentId){

        try{
            courseService.deleteAssignment(principal.getName(), id, assignmentId);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | AssignmentNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("{id}/users")
    public ResponseEntity<Object> getUsers(Principal principal, @PathVariable int id){

        try{
            return new ResponseEntity<>(courseService.getCourseUsers(principal.getName(), id), HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }


    }

    @DeleteMapping("{id}/users/{username}")
    public ResponseEntity<Object> deleteUserFromCourse(Principal principal, @PathVariable String username, @PathVariable int id){

        try{
            courseService.deleteUserFromCourse(principal.getName(), username, id);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | UserNotAttendingTheCourseException | MissingParametersException | InvalidParametersException ex){
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }


    }

}
