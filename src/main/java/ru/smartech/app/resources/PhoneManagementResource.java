package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.smartech.app.dto.PhoneDto;

@Tag(
        name = "phone-management",
        description = "Управление телефонными номерами")
@RequestMapping("/api/v1/phone-management")
public interface PhoneManagementResource {

    @Operation(summary = "Присоединить телефонный номер к пользователю")
    @PostMapping("/phone/{phone}/link/{userId}")
    ResponseEntity<PhoneDto> linkPhone(
            @PathVariable("phone") String phone,
            @PathVariable("userId") long userId);

    @Operation(summary = "Обновить телефонный номер пользователя")
    @PutMapping("/phone/{phoneId}/update/{mail}")
    ResponseEntity<PhoneDto> updatePhone(
            @PathVariable("phoneId") long phoneId,
            @PathVariable("mail") String phone);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить телефонный номер пользователя")
    @DeleteMapping("/phone/{phoneId}/unlink/{userId}")
    ResponseEntity<PhoneDto> unlinkPhone(
            @PathVariable("phoneId") long phoneId,
            @PathVariable("userId") long userId);
}