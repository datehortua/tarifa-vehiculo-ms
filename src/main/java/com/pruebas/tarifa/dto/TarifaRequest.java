package com.pruebas.tarifa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de entrada para calcular la tarifa del seguro")
public class TarifaRequest {

    @Schema(description = "Valor comercial del vehículo", example = "50000000")
    @NotNull(message = "PNU_VALCOM_VEH es obligatorio")
    @Min(value = 0, message = "PNU_VALCOM_VEH debe ser mayor o igual a 0")
    private BigDecimal pnuValcomVeh;

    @Schema(description = "Antigüedad del vehículo en años", example = "10")
    @NotNull(message = "PNU_ANTG_VEH es obligatorio")
    @Min(value = 0, message = "PNU_ANTG_VEH debe ser mayor o igual a 0")
    private Integer pnuAntgVeh;

    @Schema(description = "Edad del cliente", example = "20")
    @NotNull(message = "PNU_EDACLI es obligatorio")
    @Min(value = 18, message = "La edad del cliente debe ser mayor o igual a 18 años")
    @Max(value = 99, message = "La edad del cliente no puede superar los 99 años")
    private Integer pnuEdacli;

    @Schema(description = "Cantidad de siniestros en el último año", example = "2")
    @NotNull(message = "PNU_CANSINI es obligatorio")
    @Min(value = 0, message = "PNU_CANSINI debe ser mayor o igual a 0")
    private Integer pnuCansini;
}