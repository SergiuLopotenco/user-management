package com.user.management.service;

import com.user.management.application.CreateUserCommand;
import com.user.management.application.UpdateUserCommand;
import com.user.management.domai.model.User;
import com.user.management.domai.model.UserRepository;
import com.user.management.exception.ResourceNotFoundException;
import com.user.management.infrastructure.LogMessage;
import com.user.management.infrastructure.RabbitMQSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConverter converter;

    @Autowired
    RabbitMQSender rabbitMQSender;

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        rabbitMQSender.send(LogMessage.retrievedAllUsers(allUsers));
        return allUsers;
    }

    @Override
    public User getUserById(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            rabbitMQSender.send(LogMessage.retrievedUser(user));
            return user;
        }
        throw new ResourceNotFoundException();
    }

    @Override
    @Transactional
    public User createUser(CreateUserCommand command) {
        User user = userRepository.save(converter.convert(command));
        rabbitMQSender.send(LogMessage.createdUser(user));
        return user;
    }

    @Override
    @Transactional
    public User updateUser(long userId, UpdateUserCommand command) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(command.getName());
            user.setEmail(command.getEmail());
            User updatedUser = userRepository.save(user);
            rabbitMQSender.send(LogMessage.updatedUser(updatedUser));
            return updatedUser;
        }
        throw new ResourceNotFoundException();
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        try {
            userRepository.deleteById(userId);
            rabbitMQSender.send(LogMessage.deletedUser(userId));
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Cacheable(value = "usersCache", key = "#userId")
    @Override
    public User getUserByIdFromCacheIfExists(long userId) {
        log.info("Cache is empty --> retrieve user from data base");
        return getUserById(userId);
    }
}
