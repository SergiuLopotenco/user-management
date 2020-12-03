package com.user.management.domai.model;

import com.user.management.Application;
import com.user.management.configuration.H2JpaConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, H2JpaConfig.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void save() {
        User persistedUser = userRepository.save(new User("John", "john@gmail.com"));
        User foundUser = userRepository.getOne(persistedUser.getId());

        assertThat(persistedUser)
            .usingRecursiveComparison()
            .isEqualTo(foundUser);
    }

    @Test
    @Transactional
    public void update() {
        User persistedUser = userRepository.save(new User("John", "john@gmail.com"));
        User foundUser = userRepository.getOne(persistedUser.getId());

        foundUser.setName("John-Updated");
        foundUser.setEmail("john-updated@gmail.com");

        User updatedUser = userRepository.save(foundUser);

        assertThat(userRepository.getOne(persistedUser.getId()))
            .usingRecursiveComparison()
            .isEqualTo(updatedUser);
    }

    @Test
    @Transactional
    public void findById() {
        User persistedUser = userRepository.save(new User("John", "john@gmail.com"));
        Optional<User> foundUser = userRepository.findById(persistedUser.getId());

        assertThat(foundUser.get())
            .usingRecursiveComparison()
            .isEqualTo(persistedUser);
    }

    @Test
    @Transactional
    public void findAll() {
        User persistedUser1 = userRepository.save(new User("John1", "john1@gmail.com"));
        User persistedUser2 = userRepository.save(new User("John2", "john2@gmail.com"));

        List<User> users = userRepository.findAll();

        assertThat(users).containsExactly(persistedUser1, persistedUser2);
    }

    @Test
    @Transactional
    public void deleteById() {
        User persistedUser = userRepository.save(new User("John", "john@gmail.com"));

        userRepository.deleteById(persistedUser.getId());

        Optional<User> optionalUser = userRepository.findById(persistedUser.getId());

        assertThat(optionalUser)
            .isEmpty();
    }

}