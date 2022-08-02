package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.smartech.app.dto.UserProfileDto;

@Tag(
        name = "email-management",
        description = "Управление адресами электронной почты")
@RequestMapping("/api/v1/email-management")
public interface EmailManagementResource {

    @Operation(summary = "Присоединить почту к пользователю")
    @PostMapping("/email/{mail}/link/{userId}")
    ResponseEntity<UserProfileDto> linkMail(
            @PathVariable("mail") String mail,
            @PathVariable("userId") long userId);

    @Operation(summary = "Обновить почту пользователя")
    @PutMapping("/email/{mailId}/update/{mail}")
    ResponseEntity<UserProfileDto> updateMail(
            @PathVariable("mailId") long mailId,
            @PathVariable("mail") String mail);

    @Operation(summary = "Удалить почту пользователя")
    @DeleteMapping("/email/{mailId}/unlink")
    ResponseEntity<UserProfileDto> unlinkMail(@PathVariable("mailId") long mailId);
}
