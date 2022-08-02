package ru.smartech.app.service;

import ru.smartech.app.entity.Phone;

public interface PhoneService {

    Phone add(Phone phone);

    Phone update(Phone phone);

    void delete(Phone phone);
}
