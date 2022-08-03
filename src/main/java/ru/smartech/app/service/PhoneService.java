package ru.smartech.app.service;

import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.Phone;

import java.util.Set;

public interface PhoneService {

    Phone add(Phone phone);

    Phone update(Phone phone);

    void delete(Phone phone);

    Set<Phone> getByUserId(long userId);
}
