package com.eukon05.classroom.controllers;

import com.eukon05.classroom.dtos.CredentialsDTO;
import com.eukon05.classroom.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("api/v1/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SecurityService securityService;

    @PostMapping
    @Operation(summary = "Allows the user to retrieve an auth token to use for all other operations")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> authenticate(@RequestBody CredentialsDTO dto, HttpServletRequest request){
        return securityService.authenticate(dto.username(), dto.password(), request.getRequestURL().toString());
    }


}
