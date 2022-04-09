package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.DTOs.AssignmentDataDTO;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Services.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/courses/{courseId}/")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping("assignments")
    public ResponseEntity<Object> getAssignments(Principal principal, @PathVariable int courseId){

        try{
            return new ResponseEntity<>(assignmentService.getAssignmentsForCourse(principal.getName(), courseId), HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | InvalidParametersException | MissingParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("assignments")
    public ResponseEntity<Object> createAssignment(Principal principal, @PathVariable int courseId, @RequestBody AssignmentDataDTO dto){

        try{
            assignmentService.createAssignment(principal.getName(), courseId, dto);
            return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
        }
        catch (UserNotFoundException | MissingParametersException | CourseNotFoundException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PutMapping("assignments/{assignmentId}")
    public ResponseEntity<Object> updateAssignment(Principal principal, @PathVariable int courseId, @PathVariable int assignmentId, @RequestBody AssignmentDataDTO dto){

        try{
            assignmentService.updateAssignment(principal.getName(), courseId, assignmentId, dto);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | AssignmentNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @DeleteMapping("assignments/{assignmentId}")
    public ResponseEntity<Object> deleteAssignment(Principal principal, @PathVariable int courseId, @PathVariable int assignmentId){

        try{
            assignmentService.deleteAssignment(principal.getName(), courseId, assignmentId);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (UserNotFoundException | CourseNotFoundException | AssignmentNotFoundException | MissingParametersException | InvalidParametersException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AccessDeniedException | UserNotAttendingTheCourseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }


}
