package ru.smartech.app.service.impl;

import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.Phone;
import ru.smartech.app.exceptions.EntityAlreadyExist;
import ru.smartech.app.exceptions.NonExistEntity;
import ru.smartech.app.repository.PhoneRepository;
import ru.smartech.app.service.PhoneService;
import ru.smartech.app.utils.Caches;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class JpaPhoneService implements PhoneService {

    private final PhoneRepository repository;
    private final LoadingCache<Long, Optional<Phone>> cacheByPhoneId;
    private final LoadingCache<Long, Set<Phone>> cacheByUserId;

    @Autowired
    public JpaPhoneService(PhoneRepository repository) {
        this.repository = repository;
        this.cacheByPhoneId = Caches.simpleCache(
                repository::findById,
                10,
                Duration.ofDays(1)
        );
        this.cacheByUserId = Caches.simpleCache(
                repository::findByUserId,
                10,
                Duration.ofDays(1)
        );
    }

    @Override
    public Phone add(Phone phone) {
        log.debug("IN add -> adding phone \"{}\" to user {}", phone.getPhone(), phone.getUser().getId());
        if (repository.findByPhone(phone.getPhone()).isPresent()) {
            log.error(("IN add -> was attempt creating phone  \"{}\", but: already exist"), phone.getPhone());
            throw new EntityAlreadyExist("Email \"" + phone.getPhone() + "\" already exist");
        }
        phone = repository.save(phone);
        if (cacheByUserId.getIfPresent(phone.getId()) != null){
            Set<Phone> phones = cacheByUserId.getUnchecked(phone.getId());
            phones.add(phone);
            cacheByUserId.put(phone.getId(), phones);
        }
        log.info("IN add -> phone \"{}\" to user {} successfully added with id: {}", phone.getPhone(), phone.getUser().getId(), phone.getId());
        return phone;
    }

    @Override
    public Phone update(Phone phone) {
        if (phone.getId() == null)
            throw new IllegalArgumentException("Cannot update phone \"" + phone.getId() + "\"without id");

        log.debug("IN update -> updating phone \"{}\" with id {}", phone.getPhone(), phone.getId());
        return cacheByPhoneId.getUnchecked(phone.getId())
                .map(existed -> {
                    existed.setPhone(phone.getPhone());
                    existed = repository.save(existed);
                    cacheByPhoneId.put(phone.getId(), Optional.of(existed));
                    log.info("IN update -> phone \"{}\" from user {} successfully updated with id: {}", existed.getPhone(), existed.getUser().getId(), existed.getId());
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
        if (cacheByUserId.getUnchecked(phone.getUser().getId()).size() < 2){
            log.warn("Cannot removed single phone from user");
            throw new IllegalArgumentException("Cannot removed single phone from user");
        }
        repository.delete(phone);
        Set<Phone> phones = cacheByUserId.getUnchecked(phone.getId());
        phones.remove(phone);
        cacheByUserId.put(phone.getId(), phones);
        log.info("IN delete -> email \"{}\" with id {} successfully deleted", phone.getPhone(), phone.getId());
    }

    @Override
    public Set<Phone> getByUserId(long userId) {
        log.debug("IN getByUserId -> find phones by user with ID {}", userId);
        var result = cacheByUserId.getUnchecked(userId);
        log.info("IN getByUserId -> by user with ID {} was found {} phones", userId, result.size());
        return result;
    }

    @Override
    public Optional<Phone> findById(long phoneId) {
        return repository.findById(phoneId);
    }
}
