package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.Phone;
import ru.smartech.app.exceptions.EntityAlreadyExist;
import ru.smartech.app.exceptions.NonExistEntity;
import ru.smartech.app.repository.PhoneRepository;
import ru.smartech.app.service.PhoneService;

@Slf4j
@Service
public class JpaPhoneService implements PhoneService {

    private final PhoneRepository repository;

    @Autowired
    public JpaPhoneService(PhoneRepository repository) {
        this.repository = repository;
    }

    @Override
    public Phone add(Phone phone) {
        log.debug("IN add -> adding phone \"{}\" to user {}", phone.getPhone(), phone.getUser().getId());
        if (repository.findByPhone(phone.getPhone()).isPresent()) {
            log.error(("IN add -> was attempt creating phone  \"{}\", but: already exist"), phone.getPhone());
            throw new EntityAlreadyExist("Email \"" + phone.getPhone() + "\" already exist");
        }
        phone = repository.save(phone);
        log.info("IN add -> phone \"{}\" to user {} successfully added with id: {}", phone.getPhone(), phone.getUser().getId(), phone.getId());
        return phone;
    }

    @Override
    public Phone update(Phone phone) {
        if (phone.getId() == null)
            throw new IllegalArgumentException("Cannot update phone \"" + phone.getId() + "\"without id");

        log.debug("IN update -> updating phone \"{}\" with id {}", phone.getPhone(), phone.getId());
        return repository.findById(phone.getId())
                .map(existed -> {
                    existed.setPhone(phone.getPhone());
                    existed = repository.save(existed);
                    log.info("IN update -> phone \"{}\" from user {} successfully updated with id: {}", phone.getPhone(), phone.getUser().getId(), phone.getId());
                    return existed;
                })
                .orElseThrow(()->{
                    log.error("IN update ->  phone \"{}\" not exist", phone.getPhone());
                    throw new NonExistEntity("Email \"" + phone.getPhone() + "\" not exist");
                });
    }

    @Override
    public void delete(Phone phone) {
        log.debug("IN delete -> deleting email \"{}\" with id {}", phone.getPhone(), phone.getId());
        repository.delete(phone);
        log.info("IN delete -> email \"{}\" with id {} successfully deleted", phone.getPhone(), phone.getId());
    }
}
