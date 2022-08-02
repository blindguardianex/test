package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.dto.UserProfileDto;
import ru.smartech.app.resources.EmailManagementResource;

@Slf4j
@RestController
public class EmailManagementResourceImpl implements EmailManagementResource {

    @Override
    public ResponseEntity<UserProfileDto> linkMail(String mail, long userId) {
        return null;
    }

    @Override
    public ResponseEntity<UserProfileDto> updateMail(long mailId, String mail) {
        return null;
    }

    @Override
    public ResponseEntity<UserProfileDto> unlinkMail(long mailId) {
        return null;
    }
}
