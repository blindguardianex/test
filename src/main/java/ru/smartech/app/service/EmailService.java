package ru.smartech.app.service;

import ru.smartech.app.entity.Email;

import java.util.Set;

public interface EmailService {

    Email add(Email email);

    Email update(Email email);

    void delete(Email email);

    Set<Email> getByUserId(long userId);
}
