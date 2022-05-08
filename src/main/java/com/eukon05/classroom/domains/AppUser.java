package com.eukon05.classroom.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonIgnoreProperties(value={"password", "appUserCourses", "credentialsNonExpired", "accountNonExpired", "enabled", "authorities", "accountNonLocked"})
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "APPUSER")
public class AppUser implements UserDetails {

    @Id
    private String username;
    @Setter
    private String password;
    @Setter
    private String name;
    @Setter
    private String surname;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appUser")
    private final List<AppUserCourse> appUserCourses = new ArrayList<>();

    public AppUser(String username, String password, String name, String surname){
        this.username=username;
        this.password=password;
        this.name=name;
        this.surname=surname;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
