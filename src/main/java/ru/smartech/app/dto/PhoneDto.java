package ru.smartech.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import ru.smartech.app.entity.Email;
import ru.smartech.app.entity.Phone;

@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(description = "Номер телефона")
public class PhoneDto {

    @Schema(description = "ID", example = "1")
    private final long id;
    @Schema(description = "Номер", example = "+7(777)777-7777")
    private final String phone;

    public static PhoneDto from(Phone phone){
        return new PhoneDto(
                phone.getId(),
                phone.getPhone()
        );
    }
}
