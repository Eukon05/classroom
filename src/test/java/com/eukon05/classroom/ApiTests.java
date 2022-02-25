package com.eukon05.classroom;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
import com.eukon05.classroom.Domains.AppUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Gson gson = new Gson();
    private static final AppUserDTO userOneDTO = new AppUserDTO();
    private static final AppUserDTO userTwoDTO = new AppUserDTO();
    private static final CourseDTO courseDTO = new CourseDTO();
    private static final AssignmentDTO assignmentDTO = new AssignmentDTO();

    @BeforeAll
    static void init(){
        userOneDTO.username = "testaccountpleasedontuse";
        userOneDTO.password = "junit";
        userOneDTO.name = "Admin One";
        userOneDTO.surname = "Junit";

        userTwoDTO.username = "secondaccount";
        userTwoDTO.password = "secretcode";
        userTwoDTO.name = "User Two";
        userTwoDTO.surname = "Junit";

        courseDTO.name = "Test Course";

        assignmentDTO.title = "Test Assignment";
        assignmentDTO.links = new ArrayList<>();
        assignmentDTO.links.add("https://google/com");
        assignmentDTO.links.add("https://github.com");
    }

    @Test
    @Order(1)
    @DisplayName("Should create new users")
    void createUserTest() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(gson.toJson(userOneDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("SUCCESS"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(gson.toJson(userTwoDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("SUCCESS"));

        getAuthForUserOne();
        getAuthForUserTwo();

    }


    @Test
    @Order(2)
    @DisplayName("Should create a new test course")
    void createCourseTest() throws Exception {

        String auth = getAuthForUserOne();

        mockMvc.perform(post("/api/v1/courses")
                        .content(gson.toJson(courseDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))

                .andExpect(status().isCreated())
                .andExpect(content().string(equalTo("SUCCESS")));

        CourseDTO tmp = getCourse(auth);
        assertEquals(tmp.name, courseDTO.name);

    }

    @Test
    @Order(3)
    @DisplayName("Should add the second user to the test course")
    void joinCourseTest() throws Exception {

        String auth = getAuthForUserOne();
        String auth2 = getAuthForUserTwo();
        CourseDTO courseDTO = getCourse(auth);

        joinCourse(auth2, courseDTO);

        String json = mockMvc.perform(get("/api/v1/courses/" + courseDTO.id + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        AppUser user = gson.fromJson(gson.fromJson(json, JsonArray.class).get(0).getAsJsonObject(), AppUser.class);
        assertEquals(user.getUsername(), userOneDTO.username);

        AppUser userTwo = gson.fromJson(gson.fromJson(json, JsonArray.class).get(1).getAsJsonObject(), AppUser.class);
        assertEquals(userTwo.getUsername(), userTwoDTO.username);

    }

    @Test
    @Order(4)
    @DisplayName("Should assign the second user a teacher role automatically")
    void teacherAssignTest() throws Exception {

        String auth = getAuthForUserOne();
        String auth2 = getAuthForUserTwo();
        CourseDTO courseDTO = getCourse(auth);

        String json = mockMvc.perform(get("/api/v1/courses/" + courseDTO.id + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        AppUserDTO userTwo = gson.fromJson(gson.fromJson(json, JsonArray.class).get(1).getAsJsonObject(), AppUserDTO.class);
        assertFalse(userTwo.isTeacher);

        leaveCourse(auth, courseDTO);

        json = mockMvc.perform(get("/api/v1/courses/" + courseDTO.id + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth2))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        userTwo = gson.fromJson(gson.fromJson(json, JsonArray.class).get(0).getAsJsonObject(), AppUserDTO.class);
        assertTrue(userTwo.isTeacher);

        joinCourse(auth, courseDTO);

    }

    @Test
    @Order(5)
    @DisplayName("Should delete the first user from the test course")
    void deleteUserFromCourseTest() throws Exception {

        String auth = getAuthForUserTwo();
        CourseDTO courseDTO = getCourse(auth);

        mockMvc.perform(delete("/api/v1/courses/" + courseDTO.id + "/users")
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userOneDTO)))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(equalTo("SUCCESS")));

    }

    @Test
    @Order(6)
    @DisplayName("Should create an assignment linked to the test course")
    void createAssignmentTest() throws Exception {

        String auth = getAuthForUserTwo();
        CourseDTO courseDTO = getCourse(auth);

        mockMvc.perform(post("/api/v1/courses/" + courseDTO.id + "/assignments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", auth)
                    .content(gson.toJson(assignmentDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(equalTo("SUCCESS")));

        AssignmentDTO tmp = getAssignment(auth, courseDTO.id);
        assertEquals(tmp.title, assignmentDTO.title);
        assertEquals(tmp.links.get(0), assignmentDTO.links.get(0));
        assertEquals(tmp.links.get(1), assignmentDTO.links.get(1));
    }


    @Test
    @Order(7)
    @DisplayName("Should delete the test assignment")
    void deleteAssignmentTest() throws Exception {

        String auth = getAuthForUserTwo();
        CourseDTO courseDTO = getCourse(auth);
        AssignmentDTO assignmentDTO = getAssignment(auth, courseDTO.id);

        mockMvc.perform(delete("/api/v1/courses/" + courseDTO.id + "/assignments/" + assignmentDTO.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", auth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));

    }

    @Test
    @Order(8)
    @DisplayName("Should delete the test course")
    void deleteCourseTest() throws Exception {

        String auth = getAuthForUserTwo();
        CourseDTO courseDTO = getCourse(auth);

        mockMvc.perform(delete("/api/v1/courses/" + courseDTO.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));

    }


    @Test
    @Order(9)
    @DisplayName("Should delete the test user")
    void deleteUserTest() throws Exception {

        String auth = getAuthForUserOne();
        String auth2= getAuthForUserTwo();

        mockMvc.perform(delete("/api/v1/users/self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));

        mockMvc.perform(delete("/api/v1/users/self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));

    }


    private String getAuthForUserOne() throws Exception {
        return "Bearer " + gson.fromJson(mockMvc.perform(post("/api/v1/authenticate").content(gson.toJson(userOneDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andReturn().getResponse().getContentAsString(), JsonObject.class).get("access_token").getAsString();
    }

    private String getAuthForUserTwo() throws Exception {
        return "Bearer " + gson.fromJson(mockMvc.perform(post("/api/v1/authenticate").content(gson.toJson(userTwoDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andReturn().getResponse().getContentAsString(), JsonObject.class).get("access_token").getAsString();
    }

    private CourseDTO getCourse(String auth) throws Exception {

        return gson.fromJson(gson.fromJson(mockMvc.perform(get("/api/v1/users/self/courses")
                                .header("Authorization", auth))

                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse()
                        .getContentAsString(), JsonArray.class)
                .get(0).getAsJsonObject(), CourseDTO.class);

    }

    private void joinCourse(String auth, CourseDTO courseDTO) throws Exception {
        mockMvc.perform(post("/api/v1/users/self/courses")
                        .header("Authorization", auth)
                        .content(gson.toJson(courseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(equalTo("SUCCESS")));
    }

    private void leaveCourse(String auth, CourseDTO courseDTO) throws Exception {
        mockMvc.perform(delete("/api/v1/users/self/courses/" + courseDTO.id)
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(equalTo("SUCCESS")));
    }

    private AssignmentDTO getAssignment(String auth, int courseID) throws Exception {

        return gson.fromJson(gson.fromJson(mockMvc.perform(get("/api/v1/courses/" + courseID + "/assignments")
                        .header("Authorization", auth)).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject(), AssignmentDTO.class);

    }



}
