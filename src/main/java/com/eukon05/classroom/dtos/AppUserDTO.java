package com.eukon05.classroom.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {

    private String username;
    private String password;
    private String name;
    private String surname;

}
