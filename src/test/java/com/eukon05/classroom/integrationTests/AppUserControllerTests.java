package com.eukon05.classroom.integrationTests;

import com.eukon05.classroom.dtos.AppUserUpdateDTO;
import com.eukon05.classroom.dtos.CourseInviteCodeDTO;
import com.eukon05.classroom.repositories.AppUserRepository;
import com.eukon05.classroom.repositories.CourseRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.eukon05.classroom.integrationTests.IntegrationTestsUtils.SELF_COURSES_URL;
import static com.eukon05.classroom.integrationTests.IntegrationTestsUtils.USERS_URL;
import static com.eukon05.classroom.statics.SecurityFinals.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AppUserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private IntegrationTestsUtils utils;

    private final String SELF_USER_URL = USERS_URL + "/self";

    @BeforeEach
    void init() throws Exception {
        appUserRepository.deleteAll();
        courseRepository.deleteAll();
        utils.createUser();
    }

    @Test
    void get_yourself_test() throws Exception {
        mockMvc.perform(get(SELF_USER_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andExpectAll(status().isOk(), jsonPath("$.username").value("test"));
    }

    @Test
    void update_yourself_test() throws Exception {
        mockMvc.perform(put(SELF_USER_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString())
                        .content(gson.toJson(new AppUserUpdateDTO(null, "new", "name")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        mockMvc.perform(get(SELF_USER_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andExpectAll(status().isOk(), jsonPath("$.name").value("new"), jsonPath("$.surname").value("name"));
    }

    @Test
    void get_user_courses_test() throws Exception {
        utils.createCourse();

        assertEquals("testCourse", gson.fromJson(mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject().get("name").getAsString());
    }

    @Test
    void delete_user_test() throws Exception {
        utils.createCourse();

        mockMvc.perform(delete(SELF_USER_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        assertEquals(0, appUserRepository.count());
        assertEquals(0, courseRepository.count());
    }

    @Test
    void join_course_test() throws Exception {
        utils.createCourse();
        utils.createUser2();

        String inviteCode = gson.fromJson(mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject().get("inviteCode").getAsString();

        mockMvc.perform(post(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens2().get(ACCESS_TOKEN).getAsString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(new CourseInviteCodeDTO(inviteCode))))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        assertEquals("testCourse", gson.fromJson(mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens2().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject().get("name").getAsString());
    }

}
