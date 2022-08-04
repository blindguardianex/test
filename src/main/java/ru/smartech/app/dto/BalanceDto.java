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
@Schema(description = "Баланс пользователя>")
public class BalanceDto {

    @Schema(description = "ID пользователя", example = "1")
    private final long userId;
    @Schema(description = "Текущий баланс", example = "300")
    private final BigDecimal currentAmount;
}
