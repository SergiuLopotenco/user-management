package com.user.management.service;

import com.user.management.application.CreateUserCommand;
import com.user.management.domai.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @InjectMocks
    private UserConverter converter;

    @Test
    void convert() {
        CreateUserCommand command = new CreateUserCommand();
        command.setName("user1");
        command.setEmail("user1email@gmail.com");

        User result = converter.convert(command);

        assertThat(result)
            .extracting(User::getName, User::getEmail)
            .containsExactly("user1", "user1email@gmail.com");
    }
}