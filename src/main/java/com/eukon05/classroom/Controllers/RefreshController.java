package com.eukon05.classroom.Controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Services.AppUserService;
import com.eukon05.classroom.Utils;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/v1/refresh")
@RequiredArgsConstructor
public class RefreshController {

    private final AppUserService appUserService;
    private final Gson gson;

    @PostMapping
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Returns a new auth token after providing a refresh token")
    ResponseEntity<Object> refreshToken(HttpServletRequest request) {

        String auth = request.getHeader("Authorization");

        if(auth==null || !auth.startsWith("Bearer "))
            return new ResponseEntity<>("Missing refresh token", HttpStatus.BAD_REQUEST);

        try {
            DecodedJWT jwt = Utils.verifyAndReturnToken(auth);

            if(!jwt.getClaim("type").asString().equals("refresh"))
                throw new Exception("Invalid token");

            String username = jwt.getSubject();

            AppUser user = appUserService.getUserByUsername(username);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", Utils.createAccessToken(user.getUsername(), request.getRequestURL().toString()));
            tokens.put("refresh_token", auth.replace("Bearer ", ""));

            return new ResponseEntity<>(tokens, HttpStatus.OK);
        }
        catch(Exception ex){
            Map<String, String> error = new HashMap<>();
            error.put("error_message", ex.getMessage());
            return new ResponseEntity<>(gson.toJson(error), HttpStatus.FORBIDDEN);
        }

    }

}

