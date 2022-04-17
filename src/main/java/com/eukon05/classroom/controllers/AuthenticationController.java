package com.eukon05.classroom.controllers;

import com.eukon05.classroom.dtos.CredentialsDTO;
import com.eukon05.classroom.exceptions.AccessDeniedException;
import com.eukon05.classroom.exceptions.InvalidParametersException;
import com.eukon05.classroom.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/v1/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SecurityService securityService;

    @PostMapping
    @Operation(summary = "Allows the user to retrieve an auth token to use for all other operations")
    ResponseEntity<Object> authenticate(@RequestBody CredentialsDTO dto, HttpServletRequest request) throws AccessDeniedException, InvalidParametersException {
        return new ResponseEntity<>(securityService.authenticate(dto.getUsername(), dto.getPassword(), request.getRequestURL().toString()), HttpStatus.OK);
    }


}
