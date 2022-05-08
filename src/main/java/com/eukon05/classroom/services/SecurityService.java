package com.eukon05.classroom.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.eukon05.classroom.domains.AppUser;
import com.eukon05.classroom.enums.JWTFinals;
import com.eukon05.classroom.exceptions.AccessDeniedException;
import com.eukon05.classroom.exceptions.InvalidTokenException;
import com.eukon05.classroom.exceptions.MissingParametersException;
import com.eukon05.classroom.exceptions.MissingRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AppUserService appUserService;

    public Map<String, String> authenticate(String username, String password, String requestUrl){
        if(username == null || password == null)
            throw new MissingParametersException();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if(!authentication.isAuthenticated())
            throw new AccessDeniedException();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", jwtService.createAccessToken(username, requestUrl));
        tokens.put("refresh_token", jwtService.createRefreshToken(username, requestUrl));

        return tokens;
    }

    public Map<String, String> refresh(String auth, String requestUrl){
        if(auth==null || !auth.startsWith(JWTFinals.TOKEN_PREFIX.value))
            throw new MissingRefreshTokenException();

        DecodedJWT jwt = jwtService.verifyAndReturnToken(auth);

        if(!JWTFinals.REFRESH.value.equals(jwt.getClaim("type").asString()))
            throw new InvalidTokenException();

        String username = jwt.getSubject();

        //This line isn't required in the authenticate() method, since authenticationManager automatically checks if the user exists
        //Here, however, we need to check if the refresh token, even if valid, corresponds to a registered user
        AppUser user = appUserService.getUserByUsername(username);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", jwtService.createAccessToken(user.getUsername(), requestUrl));
        tokens.put("refresh_token", auth.replace(JWTFinals.TOKEN_PREFIX.value, ""));

        return tokens;
    }

}
