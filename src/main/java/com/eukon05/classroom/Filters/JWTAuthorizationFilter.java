package com.eukon05.classroom.Filters;

import com.google.gson.Gson;
import com.eukon05.classroom.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if(httpServletRequest.getServletPath().equals("/api/v1/authenticate"))
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        else{
            String auth = httpServletRequest.getHeader("Authorization");
            if(auth!=null && auth.startsWith("Bearer ")){

                try {
                    String username = Utils.verifyTokenAndGetUsername(auth);
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(token);
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                }
                catch(Exception ex){
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", ex.getMessage());
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpServletResponse.getWriter().write(new Gson().toJson(error));
                }

            }
            else
                filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

    }


}
