package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.dto.EmailDto;
import ru.smartech.app.dto.PhoneDto;
import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.Phone;
import ru.smartech.app.entity.User;
import ru.smartech.app.resources.PhoneManagementResource;
import ru.smartech.app.service.PhoneService;

@Slf4j
@RestController
public class PhoneManagementResourceImpl implements PhoneManagementResource {

    private final PhoneService phoneService;

    @Autowired
    public PhoneManagementResourceImpl(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @Override
    public ResponseEntity<PhoneDto> linkPhone(String phone, long userId) {
        return ResponseEntity.ok(
                PhoneDto.from(
                        phoneService.add(
                                new Phone()
                                        .setPhone(phone)
                                        .setUser(new User().setId(userId))
                        )
                )
        );
    }

    @Override
    public ResponseEntity<PhoneDto> updatePhone(long phoneId, String phone) {
        return ResponseEntity.ok(
                PhoneDto.from(
                        phoneService.update(
                                new Phone()
                                        .setPhone(phone)
                                        .setId(phoneId)
                        )
                )
        );
    }

    @Override
    public ResponseEntity<PhoneDto> unlinkPhone(long phoneId, long userId) {
        phoneService.delete(
                new Phone()
                        .setId(phoneId)
                        .setUser(new User().setId(userId))
        );
        return ResponseEntity.noContent().build();
    }
}
