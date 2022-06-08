package com.eukon05.classroom.enums;

public enum SecurityFinals {

    ACCESS("access"),
    REFRESH("refresh"),
    TOKEN_PREFIX("Bearer "),
    AUTHORIZATION("Authorization"),
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
    TYPE("type");

    public final String value;

    SecurityFinals(String value){
        this.value = value;
    }

}
