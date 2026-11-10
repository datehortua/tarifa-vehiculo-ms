package com.pruebas.tarifa.service;

import com.pruebas.tarifa.dto.TarifaRequest;
import com.pruebas.tarifa.dto.TarifaResponse;
import com.pruebas.tarifa.repository.TarifaRepository;
import org.springframework.stereotype.Service;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    public TarifaResponse calcularTarifa(TarifaRequest request) {
        return tarifaRepository.calcularTarifa(request);
    }
}