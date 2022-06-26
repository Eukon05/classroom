package com.eukon05.classroom.controllers;

import com.eukon05.classroom.domains.Answer;
import com.eukon05.classroom.dtos.AnswerDTO;
import com.eukon05.classroom.services.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("api/v1/courses/{courseId}/assignments/{assignmentId}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns all answers for the given assignment (available only for teachers)")
    public Set<Answer> getAnswers(Principal principal, @PathVariable long courseId, @PathVariable long assignmentId){
        return answerService.getAnswers(principal.getName(), courseId, assignmentId);
    }

    @PostMapping
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adds a new answer to the given assignment")
    public String createAnswer(Principal principal, @PathVariable long courseId, @PathVariable long assignmentId, @RequestBody AnswerDTO dto){
        answerService.addAnswer(principal.getName(), courseId, assignmentId, dto);
        return "SUCCESS";
    }

    @PutMapping
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Updates the answer")
    public String updateAnswer(Principal principal, @PathVariable long courseId, @PathVariable long assignmentId, @RequestBody AnswerDTO dto){
        answerService.updateAnswer(principal.getName(), courseId, assignmentId, dto);
        return "SUCCESS";
    }


    @DeleteMapping
    @SecurityRequirement(name = "JWT")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Deletes the answer")
    public String deleteAnswer(Principal principal, @PathVariable long courseId, @PathVariable long assignmentId){
        answerService.deleteAnswer(principal.getName(), courseId, assignmentId);
        return "SUCCESS";
    }

}
