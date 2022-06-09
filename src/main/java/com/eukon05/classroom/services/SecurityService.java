package com.eukon05.classroom.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.eukon05.classroom.enums.SecurityFinals;
import com.eukon05.classroom.exceptions.InvalidTokenException;
import com.eukon05.classroom.exceptions.MissingParametersException;
import com.eukon05.classroom.exceptions.MissingRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.eukon05.classroom.enums.SecurityFinals.*;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AppUserService appUserService;

    public Map<String, String> authenticate(String username, String password, String requestUrl){
        if(username == null || password == null)
            throw new MissingParametersException();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Map<String, String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN, jwtService.createAccessToken(username, requestUrl));
        tokens.put(REFRESH_TOKEN, jwtService.createRefreshToken(username, requestUrl));

        return tokens;
    }

    public Map<String, String> refresh(String auth, String requestUrl){
        if(auth==null || !auth.startsWith(TOKEN_PREFIX))
            throw new MissingRefreshTokenException();

        DecodedJWT jwt = jwtService.verifyAndReturnToken(auth);

        if(!SecurityFinals.REFRESH.equals(jwt.getClaim(SecurityFinals.TYPE).asString()))
            throw new InvalidTokenException();

        String username = jwt.getSubject();

        //This line isn't required in the "authenticate()" method, since authenticationManager automatically checks if the user exists
        //Here, however, we need to check if the refresh token, even if valid, corresponds to a registered user
        appUserService.getUserByUsername(username);
        Map<String, String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN, jwtService.createAccessToken(username, requestUrl));
        tokens.put(REFRESH_TOKEN, auth.replace(TOKEN_PREFIX, ""));

        return tokens;
    }

}
