package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.smartech.app.dto.UserDto;
import ru.smartech.app.dto.UserProfileDto;

import java.time.LocalDate;

@Tag(
        name = "user-management",
        description = "Управление пользователями")
@RequestMapping("/api/v1/user-management")
public interface UserManagementResource {

    @Operation(summary = "Получить профиль пользователя по email")
    @GetMapping("/user/byMail/{email}")
    ResponseEntity<UserProfileDto> profileByMail(@PathVariable("email") String email);

    @Operation(summary = "Получить профиль пользователя по номеру телефона")
    @GetMapping("/user/byPhone/{phone}")
    ResponseEntity<UserProfileDto> profileByPhone(@PathVariable("phone") String phone);

    @Operation(summary = "Получить список пользователей по дате рождения")
    @GetMapping("/user/fromDate")
    ResponseEntity<Page<UserDto>> profileFromDate(
            @RequestParam("dateFrom") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateFrom,
            @Parameter(description = "Номер страницы") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(value = "size", defaultValue = "20") int size);

    @Operation(summary = "Получить список пользователей по имени")
    @GetMapping("/user/likeName")
    ResponseEntity<Page<UserDto>> likeName(
            @RequestParam("namePrefix") String namePrefix,
            @Parameter(description = "Номер страницы") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(value = "size", defaultValue = "20") int size);
}
