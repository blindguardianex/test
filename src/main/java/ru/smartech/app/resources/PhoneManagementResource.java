package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.smartech.app.dto.UserProfileDto;

@Tag(
        name = "phone-management",
        description = "Управление телефонными номерами")
@RequestMapping("/api/v1/phone-management")
public interface PhoneManagementResource {

    @Operation(summary = "Присоединить телефонный номер к пользователю")
    @PostMapping("/phone/{phone}/link/{userId}")
    ResponseEntity<UserProfileDto> linkPhone(
            @PathVariable("phone") String phone,
            @PathVariable("userId") long userId);

    @Operation(summary = "Обновить телефонный номер пользователя")
    @PutMapping("/phone/{phoneId}/update/{mail}")
    ResponseEntity<UserProfileDto> updatePhone(
            @PathVariable("phoneId") long phoneId,
            @PathVariable("mail") String mail);

    @Operation(summary = "Удалить телефонный номер пользователя")
    @DeleteMapping("/phone/{phoneId}/unlink")
    ResponseEntity<UserProfileDto> unlinkPhone(@PathVariable("phoneId") long phoneId);
}