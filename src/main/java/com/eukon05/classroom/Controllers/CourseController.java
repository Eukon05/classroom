package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.*;
import com.eukon05.classroom.Exceptions.*;
import com.eukon05.classroom.Services.CourseService;
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
    public ResponseEntity<Object> createCourse(Principal principal, @RequestBody CourseDataDTO dto) throws UserNotFoundException, MissingParametersException, InvalidParametersException {

        courseService.createCourse(principal.getName(), dto.name);
        return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);

    }

    @PutMapping("{courseId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Updates the course with provided details")
    public ResponseEntity<Object> updateCourse(Principal principal, @PathVariable int courseId, @RequestBody CourseDataDTO dto) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParametersException {

        courseService.updateCourse(principal.getName(), courseId, dto.name);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);

    }

    @DeleteMapping("{courseId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes the course")
    public ResponseEntity<Object> deleteCourse(Principal principal, @PathVariable int courseId) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParametersException {

        courseService.deleteCourse(principal.getName(), courseId);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);

    }


    @PutMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Changes a specified user role from a student to a teacher or vice versa")
    public ResponseEntity<Object> updateUserRole(Principal principal, @PathVariable int courseId, @RequestBody CourseUserUpdateDTO dto) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParametersException {

        courseService.updateUserRoleInCourse(principal.getName(), courseId, dto.username, dto.isTeacher);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);

    }

    @GetMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a list of all attending users")
    public ResponseEntity<Object> getUsers(Principal principal, @PathVariable int courseId) throws UserNotFoundException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParametersException {

        return new ResponseEntity<>(courseService.getCourseUsers(principal.getName(), courseId), HttpStatus.OK);

    }

    @DeleteMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes a user from the course")
    public ResponseEntity<Object> deleteUserFromCourse(Principal principal, @RequestBody CourseUserDeleteDTO dto, @PathVariable int courseId) throws UserNotFoundException, AccessDeniedException, CourseNotFoundException, UserNotAttendingTheCourseException, MissingParametersException, InvalidParametersException {

        courseService.deleteUserFromCourse(principal.getName(), dto.username, courseId);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);

    }

}
