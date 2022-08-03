package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.dto.UserDto;
import ru.smartech.app.dto.UserProfileDto;
import ru.smartech.app.resources.UserManagementResource;
import ru.smartech.app.service.UserService;

import java.time.LocalDate;

@Slf4j
@RestController
public class UserManagementResourceImpl implements UserManagementResource {

    private final UserService userService;

    public UserManagementResourceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserProfileDto> profileByMail(String email) {
        var user = userService.getByEmail(email);
        return user
                .map(value -> ResponseEntity.ok(UserProfileDto.from(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UserProfileDto> profileByPhone(String phone) {
        var user = userService.getByPhone(phone);
        return user
                .map(value -> ResponseEntity.ok(UserProfileDto.from(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Page<UserDto>> profileFromDate(LocalDate dateFrom, int page, int size) {
        return ResponseEntity.ok(userService.getByBirthDateFrom(dateFrom, page, size).map(UserDto::from));
    }

    @Override
    public ResponseEntity<Page<UserDto>> likeName(String namePrefix, int page, int size) {
        return ResponseEntity.ok(userService.getByNameLike(namePrefix, page, size).map(UserDto::from));
    }
}
