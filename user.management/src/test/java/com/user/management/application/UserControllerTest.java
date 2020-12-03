package com.user.management.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.management.domai.model.User;
import com.user.management.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(new User("user1", "user1@gmail.com"), new User("user2", "user2@gmail.com")));

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("user1")))
            .andExpect(jsonPath("$[1].name", is("user2")))
            .andExpect(jsonPath("$[0].email", is("user1@gmail.com")))
            .andExpect(jsonPath("$[1].email", is("user2@gmail.com")));
    }

    @Test
    void getUserById() throws Exception {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(new User("user1", "user1@gmail.com"));

        mockMvc.perform(get("/users/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"id\": null, \"name\": \"user1\", \"email\": \"user1@gmail.com\"}"));
    }

    @Test
    void createUser() throws Exception {
        CreateUserCommand command = new CreateUserCommand();
        command.setName("user1");
        command.setEmail("user1@gmail.com");

        when(userService.createUser(command)).thenReturn(new User("user1", "user1@gmail.com"));

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsBytes(command))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"id\": null, \"name\": \"user1\", \"email\": \"user1@gmail.com\"}"));
    }

    @Test
    void updateUser() throws Exception {
        long userId = 1L;
        UpdateUserCommand command = new UpdateUserCommand();
        command.setName("updatedUser");
        command.setEmail("updatedMail@gmail.com");

        when(userService.updateUser(userId, command)).thenReturn(new User("updatedUser", "updatedMail@gmail.com"));

        mockMvc.perform(put("/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsBytes(command))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{\"name\": \"updatedUser\", \"email\": \"updatedMail@gmail.com\"}"));
    }

    @Test
    void deleteUser() throws Exception {
        long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
            .andExpect(status().isOk());
    }

}