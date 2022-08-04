package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.dto.EmailDto;
import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.User;
import ru.smartech.app.resources.EmailManagementResource;
import ru.smartech.app.service.EmailService;
import ru.smartech.app.service.SecurityService;

import java.util.Optional;

@Slf4j
@RestController
public class EmailManagementResourceImpl implements EmailManagementResource {

    private final EmailService emailService;
    private final SecurityService securityService;

    @Autowired
    public EmailManagementResourceImpl(EmailService emailService,
                                       SecurityService securityService) {
        this.emailService = emailService;
        this.securityService = securityService;
    }

    @Override
    public ResponseEntity<EmailDto> linkMail(String mail, long userId) {
        if (!securityService.isPrincipal(userId))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(
                EmailDto.from(
                        emailService.add(
                                new Email()
                                        .setEmail(mail)
                                        .setUser(new User().setId(userId))
                        )
                )
        );
    }

    @Override
    public ResponseEntity<EmailDto> updateMail(long mailId, String mail) {
        var existed = emailService.findById(mailId);
        if (existed.isEmpty() || !securityService.isPrincipal(existed.get().getUser().getId()))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(
                EmailDto.from(
                        emailService.update(
                                new Email()
                                        .setEmail(mail)
                                        .setId(mailId)
                        )
                )
        );
    }

    @Override
    public ResponseEntity<EmailDto> unlinkMail(long mailId, long userId) {
        if (!securityService.isPrincipal(userId))
            return ResponseEntity.badRequest().build();

        emailService.delete(
                new Email()
                        .setId(mailId)
                        .setUser(new User().setId(userId))
        );
        return ResponseEntity.noContent().build();
    }
}
