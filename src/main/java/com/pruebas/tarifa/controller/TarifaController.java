package com.pruebas.tarifa.controller;

import com.pruebas.tarifa.dto.TarifaRequest;
import com.pruebas.tarifa.dto.TarifaResponse;
import com.pruebas.tarifa.service.TarifaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tarifas")
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
    public ResponseEntity<TarifaResponse> calcular(@Valid @RequestBody TarifaRequest request) {
        TarifaResponse response = tarifaService.calcularTarifa(request);
        return ResponseEntity.ok(response);
    }
}