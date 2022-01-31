package com.eukon05.classroom;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class Utils {

    private static final Algorithm algorithm = Algorithm.HMAC256("TJA7wIo8os");

    public static String verifyTokenAndGetUsername(String authorization){

        authorization = authorization.replace("Bearer ", "");
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(authorization);
        return decodedJWT.getSubject();

    }

    public static String createAccessToken(String username, String issuer){
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .sign(algorithm);
    }

    /* I don't see a reason to use a refresh token in the current state of the app, however the code for it is ready, waiting to be uncommented

    public static String createRefreshToken(String username, String issuer){
        String refresh_token = JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .sign(algorithm);

        return refresh_token;
    }

    */
}
