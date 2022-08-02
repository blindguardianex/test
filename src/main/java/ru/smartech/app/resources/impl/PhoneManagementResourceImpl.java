package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.dto.UserProfileDto;
import ru.smartech.app.resources.PhoneManagementResource;

@Slf4j
@RestController
public class PhoneManagementResourceImpl implements PhoneManagementResource {

    @Override
    public ResponseEntity<UserProfileDto> linkPhone(String phone, long userId) {
        return null;
    }

    @Override
    public ResponseEntity<UserProfileDto> updatePhone(long phoneId, String mail) {
        return null;
    }

    @Override
    public ResponseEntity<UserProfileDto> unlinkPhone(long phoneId) {
        return null;
    }
}
