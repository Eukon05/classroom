package com.eukon05.classroom.controllers;

import com.eukon05.classroom.dtos.CourseDataDTO;
import com.eukon05.classroom.dtos.CourseUserDTO;
import com.eukon05.classroom.dtos.CourseUserDeleteDTO;
import com.eukon05.classroom.dtos.CourseUserUpdateDTO;
import com.eukon05.classroom.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new course")
    public String createCourse(Principal principal, @RequestBody CourseDataDTO dto){
        courseService.createCourse(principal.getName(), dto.getCourseName());
        return "SUCCESS";
    }

    @PutMapping("{courseId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Updates the course with provided details")
    @ResponseStatus(HttpStatus.OK)
    public String updateCourse(Principal principal, @PathVariable int courseId, @RequestBody CourseDataDTO dto){
        courseService.updateCourse(principal.getName(), courseId, dto.getCourseName());
        return "SUCCESS";
    }

    @DeleteMapping("{courseId}")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes the course")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCourse(Principal principal, @PathVariable int courseId){
        courseService.deleteCourse(principal.getName(), courseId);
        return "SUCCESS";
    }


    @PutMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Changes a specified user's role from a student to a teacher or vice versa")
    @ResponseStatus(HttpStatus.OK)
    public String updateUserRole(Principal principal, @PathVariable int courseId, @RequestBody CourseUserUpdateDTO dto){
        courseService.updateUserRoleInCourse(principal.getName(), courseId, dto.getUsername(), dto.getIsTeacher());
        return "SUCCESS";
    }

    @GetMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a list of all attending users")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseUserDTO> getUsers(Principal principal, @PathVariable int courseId){
        return courseService.getCourseUsers(principal.getName(), courseId);
    }

    @DeleteMapping("{courseId}/users")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Deletes a user from the course")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUserFromCourse(Principal principal, @RequestBody CourseUserDeleteDTO dto, @PathVariable int courseId){
        courseService.deleteUserFromCourse(principal.getName(), dto.getUsername(), courseId);
        return "SUCCESS";
    }

}
