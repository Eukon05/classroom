package com.eukon05.classroom.domains;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ANSWER")
@EqualsAndHashCode
public class Answer {

    public Answer(AppUser author,String content, Set<String> links){
        this.author=author;
        this.content=content;
        this.links=links;
    }

    @Id
    @GenericGenerator(name = "inc", strategy = "increment")
    @GeneratedValue(generator = "inc")
    private Long id;

    @ManyToOne
    private AppUser author;

    @Setter
    private String content;

    @ElementCollection
    @Setter
    private Set<String> links = new HashSet<>();
}
