package com.eukon05.classroom;

import com.eukon05.classroom.DTOs.AppUserDTO;
import com.eukon05.classroom.DTOs.AssignmentDTO;
import com.eukon05.classroom.DTOs.CourseDTO;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
//The @ActiveProfiles annotation cannot be linked to the DEV profile!
//You need to create a separate profile and a database for testing!
@ActiveProfiles(profiles = "dev")
public class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Gson gson = new Gson();
    private static final AppUserDTO dto = new AppUserDTO();
    private static final CourseDTO courseDTO = new CourseDTO();
    private static final AssignmentDTO assignmentDTO = new AssignmentDTO();

    @BeforeAll
    static void init(){
        dto.username = "testaccountpleasedontuse";
        dto.password = "junit";
        dto.name = "Admin One";
        dto.surname = "Junit";

        courseDTO.name = "Test Course";

        assignmentDTO.title = "Test Assignment";
    }

    @Test
    @Order(1)
    void createUserTest() throws Exception {

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(gson.toJson(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));

    }

    @Test
    @Order(2)
    void authenticateUserTest() throws Exception {
        getAuth();
    }

    @Test
    @Order(3)
    void getCoursesTest1() throws Exception {

        String auth = getAuth();

        mockMvc.perform(get("/api/v1/users/self/courses")
                        .header("Authorization", auth))

                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

    }

    @Test
    @Order(4)
    void createCourseTest() throws Exception {

        String auth = getAuth();

        mockMvc.perform(post("/api/v1/courses")
                        .content(gson.toJson(courseDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))

                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));


    }

    @Test
    @Order(5)
    void getCoursesTest2() throws Exception {

        String auth = getAuth();
        CourseDTO tmp = getCourse(auth);

        assertEquals(tmp.name, courseDTO.name);

    }

    @Test
    @Order(6)
    void createAssignmentTest() throws Exception {

        String auth = getAuth();
        CourseDTO courseDTO = getCourse(auth);

        mockMvc.perform(post("/api/v1/courses/" + courseDTO.id + "/assignments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", auth)
                    .content(gson.toJson(assignmentDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));

    }

    @Test
    @Order(7)
    void getAssignmentsTest() throws Exception {

        String auth = getAuth();
        CourseDTO courseDTO = getCourse(auth);
        AssignmentDTO tmp = getAssignment(auth, courseDTO.id);

        assertEquals(tmp.title, assignmentDTO.title);

    }

    @Test
    @Order(8)
    void deleteAssignmentTest() throws Exception {

        String auth = getAuth();
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
    @Order(9)
    void deleteCourseTest() throws Exception {

        String auth = getAuth();
        CourseDTO courseDTO = getCourse(auth);

        mockMvc.perform(delete("/api/v1/courses/" + courseDTO.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));

    }





    @Test
    @Order(10)
    void deleteUserTest() throws Exception {

        String auth = getAuth();

        mockMvc.perform(delete("/api/v1/users/self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("SUCCESS")));

    }


    private String getAuth() throws Exception {
        return "Bearer " + gson.fromJson(mockMvc.perform(post("/api/v1/authenticate").content(gson.toJson(dto)))
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

    private AssignmentDTO getAssignment(String auth, int courseID) throws Exception {

        return gson.fromJson(gson.fromJson(mockMvc.perform(get("/api/v1/courses/" + courseID + "/assignments")
                        .header("Authorization", auth)).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), JsonArray.class).get(0).getAsJsonObject(), AssignmentDTO.class);

    }



}
