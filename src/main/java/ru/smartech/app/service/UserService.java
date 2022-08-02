package ru.smartech.app.service;

import ru.smartech.app.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getByBirthDateFrom(LocalDate from);

    Optional<User> getByPhone(String phone);

    List<User> getByNameLike(String name);

    Optional<User> getByEmail(String email);
}
