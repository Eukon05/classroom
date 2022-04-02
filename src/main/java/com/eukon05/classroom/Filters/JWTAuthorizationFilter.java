package com.eukon05.classroom.Filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.eukon05.classroom.Utils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    //Code copied from my previous project, "Leopard"

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if(httpServletRequest.getServletPath().equals("/api/v1/authenticate") ||
           httpServletRequest.getServletPath().equals("/api/v1/refresh") ||
           httpServletRequest.getServletPath().equals("/api/v1/users"))
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        else{
            String auth = httpServletRequest.getHeader("Authorization");
            if(auth!=null && auth.startsWith("Bearer ")){

                try {
                    DecodedJWT jwt = Utils.verifyAndReturnToken(auth);

                    if(!jwt.getClaim("type").asString().equals("access"))
                        throw new Exception("Invalid token");

                    String username = jwt.getSubject();
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
