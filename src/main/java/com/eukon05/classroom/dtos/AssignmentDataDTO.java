package com.eukon05.classroom.dtos;

import java.util.Set;

public record AssignmentDataDTO(String title, String content, Set<String> links) {
}
