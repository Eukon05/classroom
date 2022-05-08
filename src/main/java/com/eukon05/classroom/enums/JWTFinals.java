package com.eukon05.classroom.enums;

public enum JWTFinals {

    ACCESS("access"),
    REFRESH("refresh"),
    TOKEN_PREFIX("Bearer ");

    public final String value;

    JWTFinals(String value){
        this.value = value;
    }

}
