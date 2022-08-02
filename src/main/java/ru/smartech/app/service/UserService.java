package ru.smartech.app.service;

import org.springframework.data.domain.Page;
import ru.smartech.app.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Page<User> getByBirthDateFrom(LocalDate from, int page, int size);

    Optional<User> getByPhone(String phone);

    Page<User> getByNameLike(String name, int page, int size);

    Optional<User> getByEmail(String email);
}
