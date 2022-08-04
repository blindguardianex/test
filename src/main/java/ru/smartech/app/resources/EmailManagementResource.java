package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.smartech.app.dto.EmailDto;

import javax.validation.constraints.Email;

@Tag(
        name = "email-management",
        description = "Управление адресами электронной почты")
@Validated
@RequestMapping("/api/v1/email-management")
public interface EmailManagementResource {

    @Operation(summary = "Присоединить почту к пользователю")
    @PostMapping("/email/{mail}/link/{userId}")
    ResponseEntity<EmailDto> linkMail(
            @PathVariable("mail") @Email(message = "Введите корректный адрес электронной почты") String mail,
            @PathVariable("userId") long userId);

    @Operation(summary = "Обновить почту пользователя")
    @PutMapping("/email/{mailId}/update/{mail}")
    ResponseEntity<EmailDto> updateMail(
            @PathVariable("mailId") long mailId,
            @PathVariable("mail") @Email(message = "Введите корректный адрес электронной почты") String mail);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить почту пользователя")
    @DeleteMapping("/email/{mailId}/unlink/{userId}")
    ResponseEntity<EmailDto> unlinkMail(
            @PathVariable("mailId") long mailId,
            @PathVariable("userId") long userId);
}

