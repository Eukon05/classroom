package com.eukon05.classroom.controllers;

import com.eukon05.classroom.exceptions.*;
import com.eukon05.classroom.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/v1/refresh")
@RequiredArgsConstructor
public class RefreshController {

    private final SecurityService securityService;

    @PostMapping
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a new auth token after providing a refresh token")
    ResponseEntity<Object> refreshToken(HttpServletRequest request) throws InvalidTokenException, UserNotFoundException, InvalidParametersException, MissingParametersException, MissingRefreshTokenException {
        return new ResponseEntity<>(securityService.refresh(request.getHeader("Authorization"), request.getRequestURL().toString()), HttpStatus.OK);
    }

}

