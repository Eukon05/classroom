package com.eukon05.classroom.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.eukon05.classroom.statics.SecurityFinals.*;

@Service
public class JWTService {

    private final Algorithm algorithm;
    @Value("${jwt.accessLifetime}")
    private long accessLifetime;
    @Value("${jwt.refreshLifetime}")
    private long refreshLifetime;

    public JWTService(@Value("${jwt.secret}") String secret){
        algorithm = Algorithm.HMAC256(secret);
    }

    public DecodedJWT verifyAndReturnToken(String authorization){
        authorization = authorization.replace(TOKEN_PREFIX, "");
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(authorization);
    }

    public String createAccessToken(String username, String issuer){
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withClaim(TYPE, ACCESS)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessLifetime))
                .sign(algorithm);
    }

    public String createRefreshToken(String username, String issuer){
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withClaim(TYPE, REFRESH)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshLifetime))
                .sign(algorithm);
    }

}
