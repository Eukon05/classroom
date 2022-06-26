package com.eukon05.classroom.integrationTests;

import com.eukon05.classroom.dtos.CourseDataDTO;
import com.eukon05.classroom.dtos.CourseInviteCodeDTO;
import com.eukon05.classroom.dtos.CourseUserDeleteDTO;
import com.eukon05.classroom.dtos.CourseUserUpdateDTO;
import com.eukon05.classroom.repositories.AppUserRepository;
import com.eukon05.classroom.repositories.CourseRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.eukon05.classroom.integrationTests.IntegrationTestsUtils.COURSES_URL;
import static com.eukon05.classroom.integrationTests.IntegrationTestsUtils.SELF_COURSES_URL;
import static com.eukon05.classroom.statics.SecurityFinals.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CourseControllerTests {

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

    @BeforeEach
    void init() throws Exception {
        appUserRepository.deleteAll();
        courseRepository.deleteAll();

        utils.createUser();
        utils.createCourse();
    }

    @Test
    void update_course_test() throws Exception {
        mockMvc.perform(put(COURSES_URL + utils.getCourseId())
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString())
                        .content(gson.toJson(new CourseDataDTO("new name")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        assertEquals("new name", gson.fromJson(mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject().get("name").getAsString());
    }

    @Test
    void delete_course_test() throws Exception {
        mockMvc.perform(delete(COURSES_URL + utils.getCourseId())
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        assertEquals("[]", mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    void delete_user_from_course_test() throws Exception {
        mockMvc.perform(delete(COURSES_URL + utils.getCourseId() + "/users")
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString())
                        .content(gson.toJson(new CourseUserDeleteDTO("test")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        assertEquals("[]", mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    void get_course_users_test() throws Exception {
        assertEquals("test", gson.fromJson(mockMvc.perform(get(COURSES_URL + utils.getCourseId()+"/users")
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject().get("username").getAsString());
    }

    @Test
    void change_users_role_test() throws Exception {
        utils.createUser2();

        String inviteCode = gson.fromJson(mockMvc.perform(get(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString()))
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject().get("inviteCode").getAsString();

        mockMvc.perform(post(SELF_COURSES_URL)
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens2().get(ACCESS_TOKEN).getAsString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(new CourseInviteCodeDTO(inviteCode))))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        mockMvc.perform(put(COURSES_URL + utils.getCourseId() + "/users")
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN).getAsString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(new CourseUserUpdateDTO("test2", true))))
                .andExpectAll(status().isOk(), content().string("SUCCESS"));

        JsonObject user = gson.fromJson(mockMvc.perform(get(COURSES_URL + utils.getCourseId() + "/users")
                        .header(AUTHORIZATION, TOKEN_PREFIX + utils.getTokens().get(ACCESS_TOKEN)
                                .getAsString())).andReturn().getResponse().getContentAsString(), JsonArray.class)
                .get(1).getAsJsonObject();

        assertTrue(user.get("username").getAsString().equals("test2") && user.get("isTeacher").getAsBoolean());
    }

}
