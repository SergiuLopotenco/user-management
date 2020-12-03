package com.user.management.application;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode
public class CreateUserCommand {

    @NotBlank(message = "User name should not be blank")
    private String name;

    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w]{2,4}$", message = "Invalid email address.")
    private String email;
}
