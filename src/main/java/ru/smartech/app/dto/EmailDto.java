package ru.smartech.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import ru.smartech.app.entity.Email;

@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(description = "Электронная почта")
public class EmailDto {

    @Schema(description = "ID", example = "1")
    private final long id;
    @Schema(description = "Почта", example = "mail@mail.ru")
    private final String email;

    public static EmailDto from(Email email){
        return new EmailDto(
                email.getId(),
                email.getEmail()
        );
    }
}
