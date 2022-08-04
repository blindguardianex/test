package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.dto.PhoneDto;
import ru.smartech.app.entity.Phone;
import ru.smartech.app.entity.User;
import ru.smartech.app.resources.PhoneManagementResource;
import ru.smartech.app.service.PhoneService;
import ru.smartech.app.service.SecurityService;

@Slf4j
@RestController
public class PhoneManagementResourceImpl implements PhoneManagementResource {

    private final PhoneService phoneService;
    private final SecurityService securityService;

    @Autowired
    public PhoneManagementResourceImpl(PhoneService phoneService,
                                       SecurityService securityService) {
        this.phoneService = phoneService;
        this.securityService = securityService;
    }

    @Override
    public ResponseEntity<PhoneDto> linkPhone(String phone, long userId) {
        if (!securityService.isPrincipal(userId))
            return ResponseEntity.badRequest().build();

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
        var existed = phoneService.findById(phoneId);
        if (existed.isEmpty() || !securityService.isPrincipal(existed.get().getUser().getId()))
            return ResponseEntity.badRequest().build();

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
        if (!securityService.isPrincipal(userId))
            return ResponseEntity.badRequest().build();

        phoneService.delete(
                new Phone()
                        .setId(phoneId)
                        .setUser(new User().setId(userId))
        );
        return ResponseEntity.noContent().build();
    }
}
