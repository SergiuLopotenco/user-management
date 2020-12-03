package com.user.management.infrastructure;

import com.user.management.domai.model.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class LogMessage implements Serializable {

    private Operation operation;

    private Object operationResult;

    private LogMessage(Operation operation, Object operationResult) {
        this.operation = operation;
        this.operationResult = operationResult;
    }

    public static LogMessage retrievedAllUsers(List<User> allUsers) {
        return new LogMessage(Operation.RETRIEVED_ALL_USERS, allUsers);
    }

    public static LogMessage retrievedUser(User user) {
        return new LogMessage(Operation.RETRIEVED_USER_BY_ID, user);
    }

    public static LogMessage createdUser(User user) {
        return new LogMessage(Operation.USER_CREATED, user);
    }

    public static LogMessage updatedUser(User user) {
        return new LogMessage(Operation.USER_UPDATED, user);
    }

    public static LogMessage deletedUser(long userId) {
        return new LogMessage(Operation.USER_DELETED, "Deleted user with id = " + userId);
    }

    public enum Operation {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        RETRIEVED_USER_BY_ID,
        RETRIEVED_ALL_USERS
    }
}
