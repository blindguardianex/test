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
@Schema(description = "Краткий профиль пользователя")
public class UserDto {

    @Schema(description = "ID", example = "1")
    private final long id;
    @Schema(description = "Имя пользователя", example = "Афанасий")
    private final String name;

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName()
        );
    }
}
