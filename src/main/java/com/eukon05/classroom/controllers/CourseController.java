package com.eukon05.classroom.controllers;

import com.eukon05.classroom.dtos.CourseDataDTO;
import com.eukon05.classroom.dtos.CourseUserDeleteDTO;
import com.eukon05.classroom.dtos.CourseUserUpdateDTO;
import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new course")
    public ResponseEntity<Object> createCourse(Principal principal, @RequestBody CourseDataDTO dto) throws UserNotFoundException, MissingParametersException, InvalidParameterException {
        courseService.createCourse(principal.getName(), dto.getCourseName());
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
    }

    @PutMapping("{courseId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Updates the course with provided details")
    public ResponseEntity<Object> updateCourse(Principal principal, @PathVariable int courseId, @RequestBody CourseDataDTO dto) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParameterException {
        courseService.updateCourse(principal.getName(), courseId, dto.getCourseName());
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @DeleteMapping("{courseId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes the course")
    public ResponseEntity<Object> deleteCourse(Principal principal, @PathVariable int courseId) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParameterException {
        courseService.deleteCourse(principal.getName(), courseId);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }


    @PutMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Changes a specified user's role from a student to a teacher or vice versa")
    public ResponseEntity<Object> updateUserRole(Principal principal, @PathVariable int courseId, @RequestBody CourseUserUpdateDTO dto) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParameterException {
        courseService.updateUserRoleInCourse(principal.getName(), courseId, dto.getUsername(), dto.isTeacher());
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @GetMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a list of all attending users")
    public ResponseEntity<Object> getUsers(Principal principal, @PathVariable int courseId) throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParameterException {
        return new ResponseEntity<>(courseService.getCourseUsers(principal.getName(), courseId), HttpStatus.OK);
    }

    @DeleteMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes a user from the course")
    public ResponseEntity<Object> deleteUserFromCourse(Principal principal, @RequestBody CourseUserDeleteDTO dto, @PathVariable int courseId) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParameterException {
        courseService.deleteUserFromCourse(principal.getName(), dto.getUsername(), courseId);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

}
