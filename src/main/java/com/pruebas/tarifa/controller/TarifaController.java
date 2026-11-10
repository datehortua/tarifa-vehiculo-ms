package com.pruebas.tarifa.controller;

import com.pruebas.tarifa.dto.TarifaRequest;
import com.pruebas.tarifa.dto.TarifaResponse;
import com.pruebas.tarifa.service.TarifaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tarifas")
@Tag(name = "Tarifas", description = "Endpoints para el cálculo de primas de seguros")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    /**
     * Calcula la tarifa de seguro de vehículo invocando SP_GET_TARIFA_VEHICULO.
     *
     * POST /api/v1/tarifas/calcular
     */
    @PostMapping("/calcular")
    @Operation(summary = "Calcular tarifa de vehículo", description = "Invoca el procedimiento de Oracle SP_GET_TARIFA_VEHICULO para obtener la prima.")
    public ResponseEntity<TarifaResponse> calcular(@Valid @RequestBody TarifaRequest request) {
        return ResponseEntity.ok(tarifaService.calcularTarifa(request));
    }
}