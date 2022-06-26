package com.eukon05.classroom.integrationTests;

import com.eukon05.classroom.dtos.AppUserDTO;
import com.eukon05.classroom.dtos.CourseDataDTO;
import com.eukon05.classroom.dtos.CredentialsDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import static com.eukon05.classroom.statics.SecurityFinals.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Component
@RequiredArgsConstructor
public final class IntegrationTestsUtils {
    private final Gson gson;
    private final MockMvc mockMvc;

    static final String USERS_URL = "/api/v1/users";
    static final String COURSES_URL = "/api/v1/courses/";
    static final String SELF_COURSES_URL = "/api/v1/users/self/courses/";
    static final String AUTHENTICATE_URL = "/api/v1/authenticate";

    JsonObject getTokens() throws Exception {
        return gson.fromJson(mockMvc.perform(post(AUTHENTICATE_URL)
                        .content(gson.toJson(new CredentialsDTO("test", "test")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), jsonPath("$.access_token").isNotEmpty(), jsonPath("$.refresh_token").isNotEmpty())
                .andReturn().getResponse().getContentAsString(), JsonObject.class);
    }

    JsonObject getTokens2() throws Exception {
        return gson.fromJson(mockMvc.perform(post(AUTHENTICATE_URL)
                        .content(gson.toJson(new CredentialsDTO("test2", "test")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), jsonPath("$.access_token").isNotEmpty(), jsonPath("$.refresh_token").isNotEmpty())
                .andReturn().getResponse().getContentAsString(), JsonObject.class);
    }

    void createUser() throws Exception {
        mockMvc.perform(post(USERS_URL)
                        .content(gson.toJson(new AppUserDTO("test", "test", "Test", "User")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isCreated(), content().string("SUCCESS"));
    }

    void createUser2() throws Exception {
        mockMvc.perform(post(USERS_URL)
                        .content(gson.toJson(new AppUserDTO("test2", "test", "Test", "User")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isCreated(), content().string("SUCCESS"));
    }

    void createCourse() throws Exception {
        mockMvc.perform(post(COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + getTokens().get(ACCESS_TOKEN).getAsString())
                        .content(gson.toJson(new CourseDataDTO("testCourse")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isCreated(), content().string("SUCCESS"));
    }

    long getCourseId() throws Exception {
        return gson.fromJson(mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject().get("id").getAsLong();
    }

}
