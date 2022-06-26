package com.eukon05.classroom.dtos;

import java.util.Set;

public record AnswerDTO(String content, Set<String> links) {
}
