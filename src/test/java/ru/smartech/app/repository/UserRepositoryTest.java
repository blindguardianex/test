package ru.smartech.app.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.smartech.app.entity.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Disabled("Manual tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByPhone() {
        var opt = userRepository.findByPhone("+1(111)111-1111")
                .map(user -> {
                    System.out.println(user);
                    return user;
                });
        assertTrue(opt.isPresent());
    }

    @Test
    void findByEmail() {
        var opt = userRepository.findByEmail("veteran@mail.ru")
                .map(user -> {
                    System.out.println(user);
                    return user;
                });
        assertTrue(opt.isPresent());
    }

    @Test
    void getByBirthDateAfter() {
        userRepository.getByBirthDateAfter(LocalDate.of(2022,8,2), PageRequest.of(0,20)).forEach(System.out::println);
    }

    @Test
    void getByNameLike() {
        userRepository.getByNameLike("mo", PageRequest.of(0,20)).forEach(System.out::println);
    }
}