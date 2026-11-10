package com.pruebas.tarifa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaResponse {

    private BigDecimal tarifa;
    private Integer salerr;
    private String pvaSalerr;
    private boolean exitoso;
}