package com.eukon05.classroom;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class Utils {

    private static final Algorithm algorithm = Algorithm.HMAC256("TJA7wIo8os");

    public static DecodedJWT verifyAndReturnToken(String authorization){

        authorization = authorization.replace("Bearer ", "");
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(authorization);

    }

    public static String createAccessToken(String username, String issuer){
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withClaim("type", "access")
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .sign(algorithm);
    }

    public static String createRefreshToken(String username, String issuer){

        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withClaim("type", "refresh")
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .sign(algorithm);
    }

}
