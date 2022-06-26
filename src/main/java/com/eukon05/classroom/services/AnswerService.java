package com.eukon05.classroom.services;

import com.eukon05.classroom.domains.Answer;
import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.domains.Assignment;
import com.eukon05.classroom.domains.Course;
import com.eukon05.classroom.dtos.AnswerDTO;
import com.eukon05.classroom.enums.ParamType;
import com.eukon05.classroom.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.eukon05.classroom.statics.ParamUtils.checkStringAndTrim;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AppUserService appUserService;
    private final CourseService courseService;
    private final AssignmentService assignmentService;

    public Set<Answer> getAnswers(String username, long courseId, long assignmentId) {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        courseService.teacherCheck(appUser, course);

        return assignmentService.getAssignmentById(assignmentId, course).getAnswers();
    }

    @Transactional
    public void addAnswer(String username, long courseId, long assignmentId, AnswerDTO dto) {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        try {
            courseService.teacherCheck(appUser, course);
            throw new TeacherCantAnswerAssignmentsException();
        }
        catch (AccessDeniedException ignored){}

        Assignment assignment = assignmentService.getAssignmentById(assignmentId, course);

        assignment.getAnswers().stream().filter(answer -> answer.getAuthor().equals(appUser)).findAny().ifPresent(answer -> {throw new AnswerAlreadyGivenException();});

        if(dto.content() == null && dto.links() == null) {
            throw new MissingParametersException();
        }

        final StringBuilder contentBuilder = new StringBuilder();
        Optional.ofNullable(dto.content()).ifPresent(content -> {
            //This line bypasses the empty string check in checkStringAndTrim()
            if(!content.trim().isEmpty()) {
                contentBuilder.append(checkStringAndTrim(content, ParamType.CONTENT));
            }
        });

        assignment.getAnswers().add(new Answer(appUser, contentBuilder.toString(), dto.links()));
    }

    @Transactional
    public void updateAnswer(String username, long courseId, long assignmentId, AnswerDTO dto) {
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        try {
            courseService.teacherCheck(appUser, course);
            throw new TeacherCantAnswerAssignmentsException();
        }
        catch (AccessDeniedException ignored){}

        Assignment assignment = assignmentService.getAssignmentById(assignmentId, course);

        Answer answer = assignment.getAnswers().stream().filter(tmp -> tmp.getAuthor().equals(appUser)).findAny().orElseThrow(AnswerNotGivenException::new);

        if(dto.content() == null && dto.links() == null) {
            throw new MissingParametersException();
        }

        Optional.ofNullable(dto.content()).ifPresent(content -> {
            //This line bypasses the empty string check in checkStringAndTrim()
            if(content.trim().isEmpty()) {
                answer.setContent("");
            }
            else {
                answer.setContent(checkStringAndTrim(content, ParamType.CONTENT));
            }
        });

        Optional.ofNullable(dto.links()).ifPresent(assignment::setLinks);
    }

    @Transactional
    public void deleteAnswer(String username, long courseId, long assignmentId){
        AppUser appUser = appUserService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);

        try {
            courseService.teacherCheck(appUser, course);
            throw new TeacherCantAnswerAssignmentsException();
        }
        catch (AccessDeniedException ignored){}

        Assignment assignment = assignmentService.getAssignmentById(assignmentId, course);

        assignment
                .getAnswers()
                .remove(assignment.getAnswers().stream().filter(answer -> answer.getAuthor().equals(appUser)).findAny().orElseThrow(AnswerNotGivenException::new));

    }

}
