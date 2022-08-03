package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.Email;
import ru.smartech.app.exceptions.EntityAlreadyExist;
import ru.smartech.app.exceptions.NonExistEntity;
import ru.smartech.app.repository.EmailRepository;
import ru.smartech.app.service.EmailService;

import java.util.Set;

@Slf4j
@Service
public class JpaEmailService implements EmailService {

    private final EmailRepository repository;

    @Autowired
    public JpaEmailService(EmailRepository repository) {
        this.repository = repository;
    }

    @Override
    public Email add(Email email) {
        log.debug("IN add -> adding email \"{}\" to user {}", email.getEmail(), email.getUser().getId());
        if (repository.findByEmail(email.getEmail()).isPresent()) {
            log.error(("IN add -> was attempt creating email  \"{}\", but: already exist"), email.getEmail());
            throw new EntityAlreadyExist("Email \"" + email.getEmail() + "\" already exist");
        }
        email = repository.save(email);
        log.info("IN add -> email \"{}\" to user {} successfully added with id: {}", email.getEmail(), email.getUser().getId(), email.getId());
        return email;
    }

    @Override
    public Email update(Email email) {
        if (email.getId() == null)
            throw new IllegalArgumentException("Cannot update email \"" + email.getId() + "\"without id");

        log.debug("IN update -> updating email \"{}\" with id {}", email.getEmail(), email.getId());
        return repository.findById(email.getId())
                .map(existed -> {
                    existed.setEmail(email.getEmail());
                    existed = repository.save(existed);
                    log.info("IN update -> email \"{}\" from user {} successfully updated with id: {}", existed.getEmail(), existed.getUser().getId(), existed.getId());
                    return existed;
                })
                .orElseThrow(() -> {
                    log.error("IN update ->  email \"{}\" not exist", email.getEmail());
                    throw new NonExistEntity("Email \"" + email.getEmail() + "\" not exist");
                });
    }

    @Override
    public void delete(Email email) {
        log.debug("IN delete -> deleting email \"{}\" with id {}", email.getEmail(), email.getId());
        if (repository.findByUserId(email.getUser().getId()).size() < 2){
            log.warn("Cannot removed single email from user");
            throw new IllegalArgumentException("Cannot removed single email from user");
        }
        repository.delete(email);
        log.info("IN delete -> email \"{}\" with id {} successfully deleted", email.getEmail(), email.getId());
    }

    @Override
    public Set<Email> getByUserId(long userId) {
        log.debug("IN getByUserId -> find emails by user with ID {}", userId);
        var result = repository.findByUserId(userId);
        log.info("IN getByUserId -> by user with ID {} was found {} emails", userId, result.size());
        return result;
    }
}
