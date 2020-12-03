package com.user.management.service;

import com.user.management.application.CreateUserCommand;
import com.user.management.application.UpdateUserCommand;
import com.user.management.domai.model.User;
import com.user.management.domai.model.UserRepository;
import com.user.management.exception.ResourceNotFoundException;
import com.user.management.infrastructure.LogMessage;
import com.user.management.infrastructure.RabbitMQSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter converter;

    @Mock
    private RabbitMQSender rabbitMQSender;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository, converter, rabbitMQSender);
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
            .thenReturn(Arrays.asList(new User("user1", "user1email@gmail.com"), new User("user2", "user2email@gmail.com")));

        List<User> result = userService.getAllUsers();

        assertThat(result)
            .extracting(User::getName, User::getEmail)
            .containsExactly(tuple("user1", "user1email@gmail.com"), tuple("user2", "user2email@gmail.com"));
        verify(rabbitMQSender).send(LogMessage.retrievedAllUsers(result));
    }

    @Test
    void getUserById() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User("user1", "user1email@gmail.com")));

        User result = userService.getUserById(userId);

        assertThat(result)
            .extracting(User::getName, User::getEmail)
            .containsExactly("user1", "user1email@gmail.com");
        verify(rabbitMQSender).send(LogMessage.retrievedUser(result));
    }

    @Test
    void getUserByIdThrowsResourceNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
            .isThrownBy(() -> userService.getUserById(userId));
        verifyNoInteractions(rabbitMQSender);
    }

    @Test
    void createUser() {
        CreateUserCommand command = new CreateUserCommand();
        command.setName("user1");
        command.setEmail("user1email@gmail.com");

        User convertedUser = new User("user1", "user1email@gmail.com");
        User expectedUser = new User("user1", "user1email@gmail.com");

        when(converter.convert(command)).thenReturn(convertedUser);
        when(userRepository.save(convertedUser)).thenReturn(expectedUser);

        User result = userService.createUser(command);

        assertThat(result).isEqualTo(expectedUser);
        verify(rabbitMQSender).send(LogMessage.createdUser(result));
    }

    @Test
    void updateUser() {
        long userId = 1L;
        UpdateUserCommand command = new UpdateUserCommand();
        command.setName("user2");
        command.setEmail("user2email@gmail.com");

        User userToUpdate = new User("user1", "user1email@gmail.com");
        User updatedUser = new User("user2", "user2email@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.save(userToUpdate)).thenReturn(updatedUser);

        User result = userService.updateUser(userId, command);

        assertThat(result).isEqualTo(updatedUser);
        verify(rabbitMQSender).send(LogMessage.updatedUser(result));
    }

    @Test
    void updateUserThrowsResourceNotFoundException() {
        long userId = 1L;
        UpdateUserCommand command = new UpdateUserCommand();
        command.setName("user2");
        command.setEmail("user2email@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
            .isThrownBy(() -> userService.updateUser(userId, command));

        verifyNoInteractions(rabbitMQSender);
    }

    @Test
    void deleteUser() {
        long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(rabbitMQSender).send(LogMessage.deletedUser(userId));
    }

    @Test
    void deleteUserThrowsResourceNotFoundException() {
        long userId = 1L;
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(userId);

        assertThatExceptionOfType(ResourceNotFoundException.class)
            .isThrownBy(() -> userService.deleteUser(userId));
        verifyNoInteractions(rabbitMQSender);
    }
}