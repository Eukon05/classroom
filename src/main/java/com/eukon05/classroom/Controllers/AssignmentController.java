package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AssignmentDataDTO;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Services.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns all assignments for a course")
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
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new assignment")
    public ResponseEntity<Object> createAssignment(Principal principal, @PathVariable int courseId, @RequestBody AssignmentDataDTO dto) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException {

        assignmentService.createAssignment(principal.getName(), courseId, dto);
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);

    }

    @PutMapping("assignments/{assignmentId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Updates the assignment with provided details")
    public ResponseEntity<Object> updateAssignment(Principal principal, @PathVariable int courseId, @PathVariable int assignmentId, @RequestBody AssignmentDataDTO dto) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, AssignmentNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException {

        assignmentService.updateAssignment(principal.getName(), courseId, assignmentId, dto);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);

    }

    @DeleteMapping("assignments/{assignmentId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes the assignment")
    public ResponseEntity<Object> deleteAssignment(Principal principal, @PathVariable int courseId, @PathVariable int assignmentId) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, AssignmentNotFoundException, InvalidParametersException, UserNotAttendingTheCourseException, MissingParametersException {

        assignmentService.deleteAssignment(principal.getName(), courseId, assignmentId);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);

    }


}
