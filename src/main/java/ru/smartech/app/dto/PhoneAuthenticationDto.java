package ru.smartech.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(description = "Номер телефона")
public class PhoneAuthenticationDto {

    @Schema(description = "Телефонный номер", example = "+7(800)555-3535")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Введите корректный номер телефона ")
    private final String phone;

    @Schema(description = "Пароль", example = "password")
    @NotBlank(message = "Введите пароль")
    @Size(min = 6, max = 32, message = "Минимальная длинна пароля - 6 символов, максимальная - 32")
    private final String password;
}
