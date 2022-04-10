package com.eukon05.classroom.Controllers;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.CredentialsDTO;
import com.eukon05.classroom.Exceptions.AccessDeniedException;
import com.eukon05.classroom.Exceptions.InvalidParametersException;
import com.eukon05.classroom.Exceptions.MissingParametersException;
import com.eukon05.classroom.Utils;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    @PostMapping
    @Operation(summary = "Allows the user to retrieve an auth token to use for all other operations")
    ResponseEntity<Object> authenticate(@RequestBody CredentialsDTO dto, HttpServletRequest request) throws AccessDeniedException, InvalidParametersException {

        if(dto.username == null || dto.password == null)
            throw new InvalidParametersException();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.username, dto.password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if(!authentication.isAuthenticated())
            throw new AccessDeniedException();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", Utils.createAccessToken(dto.username, request.getRequestURL().toString()));
        tokens.put("refresh_token", Utils.createRefreshToken(dto.username, request.getRequestURL().toString()));

        return new ResponseEntity<>(tokens, HttpStatus.OK);

    }


}
