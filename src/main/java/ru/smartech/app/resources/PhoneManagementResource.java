package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.smartech.app.dto.PhoneDto;

import javax.validation.constraints.Pattern;

@Tag(
        name = "phone-management",
        description = "Управление телефонными номерами")
@Validated
@RequestMapping("/api/v1/phone-management")
public interface PhoneManagementResource {

    @Operation(summary = "Присоединить телефонный номер к пользователю")
    @PostMapping("/phone/{phone}/link/{userId}")
    ResponseEntity<PhoneDto> linkPhone(
            @PathVariable("phone") @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Введите корректный номер телефона ") String phone,
            @PathVariable("userId") long userId);

    @Operation(summary = "Обновить телефонный номер пользователя")
    @PutMapping("/phone/{phoneId}/update/{mail}")
    ResponseEntity<PhoneDto> updatePhone(
            @PathVariable("phoneId") long phoneId,
            @PathVariable("mail") @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Введите корректный номер телефона ") String phone);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить телефонный номер пользователя")
    @DeleteMapping("/phone/{phoneId}/unlink/{userId}")
    ResponseEntity<PhoneDto> unlinkPhone(
            @PathVariable("phoneId") long phoneId,
            @PathVariable("userId") long userId);
}