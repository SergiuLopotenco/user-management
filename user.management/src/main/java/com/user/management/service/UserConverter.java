package com.user.management.service;

import com.user.management.application.CreateUserCommand;
import com.user.management.domai.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements Converter<CreateUserCommand, User> {

    @Override
    public User convert(CreateUserCommand command) {
        return new User(command.getName(), command.getEmail());
    }
}
