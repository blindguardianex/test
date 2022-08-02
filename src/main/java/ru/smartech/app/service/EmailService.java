package ru.smartech.app.service;

import ru.smartech.app.entity.Email;

public interface EmailService {

    Email add(Email email);

    Email update(Email email);

    void delete(Email email);
}
