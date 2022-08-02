package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.User;
import ru.smartech.app.repository.EmailRepository;
import ru.smartech.app.repository.PhoneRepository;
import ru.smartech.app.repository.UserRepository;
import ru.smartech.app.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JpaUserService implements UserService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public JpaUserService(UserRepository userRepository,
                          EmailRepository emailRepository,
                          PhoneRepository phoneRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.phoneRepository = phoneRepository;
    }

    @Override
    public List<User> getByBirthDateFrom(LocalDate from) {
        log.debug("IN getByBirthDateFrom -> find users by date from {}", from);
        var result = getByBirthDateFrom(from);
        log.info("IN getByBirthDateFrom -> by date from {} was found {} users", from, result.size());
        return result;
    }

    @Override
    public Optional<User> getByPhone(String phone) {
        log.debug("IN getByPhone -> find user by phone {}", phone);
        var user = userRepository.findByPhone(phone);
        if (user.isPresent()){
            log.info("IN getByPhone -> by phone {} was found user with name {}", phone, user.get().getName());
        } else {
            log.info("IN getByPhone -> by phone {} was NOT found user", phone);
        }
        return user;
    }

    @Override
    public List<User> getByNameLike(String name) {
        log.debug("IN getByNameLike -> find users by name like {}", name);
        var result = userRepository.getByNameLike(name);
        log.info("IN getByBirthDateFrom -> by name like {} was found {} users", name, result.size());
        return result;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        log.debug("IN getByEmail -> find user by mail {}", email);
        var user = userRepository.findByEmail(email);
        if (user.isPresent()){
            log.info("IN getByEmail -> by mail {} was found user with name {}", email, user.get().getName());
        } else {
            log.info("IN getByEmail -> by mail {} was NOT found user", email);
        }
        return user;
    }
}
