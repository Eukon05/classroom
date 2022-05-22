package com.eukon05.classroom.controllers;

import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.dtos.AssignmentDataDTO;
import com.eukon05.classroom.services.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/courses/{courseId}/")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping("assignments")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns all assignments for a course")
    @ResponseStatus(HttpStatus.OK)
    public List<Assignment> getAssignments(Principal principal, @PathVariable long courseId){
        return assignmentService.getAssignmentsForCourse(principal.getName(), courseId);
    }

    @PostMapping("assignments")
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new assignment")
    public String createAssignment(Principal principal, @PathVariable long courseId, @RequestBody AssignmentDataDTO dto){
        assignmentService.createAssignment(principal.getName(), courseId, dto);
        return "SUCCESS";
    }

    @PutMapping("assignments/{assignmentId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Updates the assignment with provided details")
    @ResponseStatus(HttpStatus.OK)
    public String updateAssignment(Principal principal, @PathVariable long courseId, @PathVariable long assignmentId, @RequestBody AssignmentDataDTO dto){
        assignmentService.updateAssignment(principal.getName(), courseId, assignmentId, dto);
        return "SUCCESS";
    }

    @DeleteMapping("assignments/{assignmentId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes the assignment")
    @ResponseStatus(HttpStatus.OK)
    public String deleteAssignment(Principal principal, @PathVariable long courseId, @PathVariable long assignmentId){
        assignmentService.deleteAssignment(principal.getName(), courseId, assignmentId);
        return "SUCCESS";
    }


}
