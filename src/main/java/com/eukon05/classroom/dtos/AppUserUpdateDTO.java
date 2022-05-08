package com.eukon05.classroom.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserUpdateDTO {

    private String password;
    private String name;
    private String surname;

}
