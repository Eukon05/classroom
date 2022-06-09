package com.eukon05.classroom.controllers;

import com.eukon05.classroom.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.eukon05.classroom.statics.SecurityFinals.AUTHORIZATION;

@RestController
@RequestMapping("api/v1/refresh")
@RequiredArgsConstructor
public class RefreshController {

    private final SecurityService securityService;

    @PostMapping
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a new auth token after providing a refresh token")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> refreshToken(HttpServletRequest request){
        return securityService.refresh(request.getHeader(AUTHORIZATION), request.getRequestURL().toString());
    }

}

