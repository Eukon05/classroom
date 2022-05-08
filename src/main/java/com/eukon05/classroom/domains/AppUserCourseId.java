package com.eukon05.classroom.domains;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AppUserCourseId implements Serializable {

    private String username;
    private Long courseId;

}
