package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.Account;
import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.Phone;
import ru.smartech.app.entity.User;
import ru.smartech.app.repository.UserRepository;
import ru.smartech.app.service.AccountService;
import ru.smartech.app.service.EmailService;
import ru.smartech.app.service.PhoneService;
import ru.smartech.app.service.UserService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class JpaUserService implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PhoneService phoneService;
    private final AccountService accountService;

    public JpaUserService(UserRepository userRepository,
                          EmailService emailService,
                          PhoneService phoneService,
                          AccountService accountService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.phoneService = phoneService;
        this.accountService = accountService;
    }

    @Override
    public Page<User> getByBirthDateFrom(LocalDate from, int page, int size) {
        log.debug("IN getByBirthDateFrom -> find users by date from {}, page {}, page size {}", from, page, size);
        var result = userRepository.getByBirthDateAfter(from, PageRequest.of(page, size));
        log.info("IN getByBirthDateFrom -> by date from {} was found {} users on page {}", from, result.stream().count(), page);
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
        return user.map(this::delegate);
    }

    @Override
    public Page<User> getByNameLike(String name, int page, int size) {
        log.debug("IN getByNameLike -> find users by name like {}, page {}, page size {}", name, page, size);
        var result = userRepository.getByNameLike(name, PageRequest.of(page, size));
        log.info("IN getByBirthDateFrom -> by name like {} was found {} users on page {}", name, result.stream().count(), page);
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
        return user.map(this::delegate);
    }

    private User delegate(User user){
        return new User(user){
            @Override
            public Optional<Account> loadAccount() {
                if (super.getAccount() != null)
                    return Optional.of(super.getAccount());
                else
                    return accountService.findByUser(super.getId())
                            .map(account -> {
                                super.setAccount(account);
                                return account;
                            });
            }

            @Override
            public Set<Email> loadEmails() {
                if (super.getEmails() == null)
                    super.setEmails(emailService.getByUserId(super.getId()));

                return super.getEmails();
            }

            @Override
            public Set<Phone> loadPhones() {
                if (super.getPhones() == null)
                    super.setPhones(phoneService.getByUserId(super.getId()));

                return super.getPhones();
            }
        };
    }
}
