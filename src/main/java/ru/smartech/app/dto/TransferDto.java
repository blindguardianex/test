package ru.smartech.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Jacksonized
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Schema(description = "Номер телефона")
public class TransferDto {

    @Schema(description = "ID получателя", example = "1")
    private final long userId;
    @Schema(description = "Сумма перевода", example = "300")
    private final BigDecimal amount;
}
