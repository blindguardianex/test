package ru.smartech.app.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(description = "Номер телефона")
public class MailAuthenticationDto{

        @Schema(description = "Адрес электронной почты", example = "veteran@mail.ru")
        @Email(message = "Введите корректный адрес электронной почты")
        private final String email;

        @Schema(description = "Пароль", example = "password")
        @NotBlank(message = "Введите пароль")
        @Size(min = 6, max = 32, message = "Минимальная длинна пароля - 6 символов, максимальная - 32")
        private final String password;
}
