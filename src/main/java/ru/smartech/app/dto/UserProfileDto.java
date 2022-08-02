package ru.smartech.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import ru.smartech.app.entity.User;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(description = "Полный профиль пользователя")
public class UserProfileDto {

    @Schema(description = "ID", example = "1")
    private final long id;
    @Schema(description = "Имя пользователя", example = "Афанасий")
    private final String name;
    @Schema(description = "Адреса электронных почт")
    private final Set<EmailDto> emails;
    @Schema(description = "Номера телефонов")
    private final Set<PhoneDto> phones;
    @Schema(description = "Баланс", example = "1000000")
    private final BigDecimal balance;

    public static UserProfileDto from(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.loadEmails() != null ? user.loadEmails().stream().map(EmailDto::from).collect(Collectors.toSet()) : Collections.emptySet(),
                user.loadPhones() != null ? user.loadPhones().stream().map(PhoneDto::from).collect(Collectors.toSet()) : Collections.emptySet(),
                user.loadAccount().isPresent() ? user.loadAccount().get().getBalance() : null
        );
    }

}
