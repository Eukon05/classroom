package com.eukon05.classroom.Filters;

import com.google.gson.Gson;
import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        StringBuilder builder = new StringBuilder();
        String line;

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                builder.append(line);
        }
        catch (Exception e) {System.out.println("ERROR while reading request data in JWTAuthenticationFilter!!!");}


        AppUserDTO user = new Gson().fromJson(builder.toString(), AppUserDTO.class);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.username, user.password);

        return authenticationManager.authenticate(authenticationToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        AppUser user = (AppUser) authResult.getPrincipal();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", Utils.createAccessToken(user.getUsername(), request.getRequestURL().toString()));
        //tokens.put("refresh_token", SecurityUtils.createRefreshToken(user.getUsername(), request.getRequestURL().toString()));

        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(new Gson().toJson(tokens));
    }
}