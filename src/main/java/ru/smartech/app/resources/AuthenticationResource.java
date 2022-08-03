package ru.smartech.app.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.smartech.app.dto.MailAuthenticationDto;
import ru.smartech.app.dto.PhoneAuthenticationDto;

import javax.validation.Valid;

@Tag(
        name = "authenticate",
        description = "Контроллер идентификации")
@RequestMapping("/api/v1/authenticate")
public interface AuthenticationResource {

    @Operation(summary = "Аутентификация пользователя по адресу электронной почты")
    @PostMapping("/sign/mail")
    ResponseEntity<String> mailSignIn(@RequestBody @Valid MailAuthenticationDto authRequest);

    @Operation(summary = "Аутентификация пользователя по телефонному номеру")
    @PostMapping("/sign/phone")
    ResponseEntity<String> phoneSignIn(@RequestBody @Valid PhoneAuthenticationDto authRequest);
}
