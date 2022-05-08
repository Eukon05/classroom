package com.eukon05.classroom.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.eukon05.classroom.enums.JWTFinals;
import com.eukon05.classroom.exceptions.InvalidTokenException;
import com.eukon05.classroom.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String auth = httpServletRequest.getHeader("Authorization");

        if(auth==null || !auth.startsWith(JWTFinals.TOKEN_PREFIX.value)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        try {
            DecodedJWT jwt = jwtService.verifyAndReturnToken(auth);

            if(!JWTFinals.ACCESS.value.equals(jwt.getClaim("type").asString()))
                throw new InvalidTokenException();

            String username = jwt.getSubject();
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(token);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
        catch(Exception ex){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.getWriter().write(ex.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        List<String> excludedUrls = new ArrayList<>();
        excludedUrls.add("/api/v1/authenticate");
        excludedUrls.add("/api/v1/refresh");
        excludedUrls.add("/api/v1/users");

        return excludedUrls.contains(request.getServletPath());
    }

}
