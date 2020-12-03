package com.user.management.service;

import com.user.management.application.CreateUserCommand;
import com.user.management.application.UpdateUserCommand;
import com.user.management.domai.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(long userId);

    User createUser(CreateUserCommand command);

    User updateUser(long userId, UpdateUserCommand command);

    void deleteUser(long userId);

    User getUserByIdFromCacheIfExists(long id);
}
