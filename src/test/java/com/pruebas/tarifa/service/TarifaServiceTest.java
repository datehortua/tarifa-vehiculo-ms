package com.pruebas.tarifa.service;

import com.pruebas.tarifa.dto.TarifaRequest;
import com.pruebas.tarifa.dto.TarifaResponse;
import com.pruebas.tarifa.repository.TarifaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TarifaServiceTest {

    @Mock
    private TarifaRepository tarifaRepository;

    @InjectMocks
    private TarifaService tarifaService;

    @Test
    public void testCalcularTarifa_DelegaAlRepositorio() {
        // Arrange
        TarifaRequest request = TarifaRequest.builder()
                .pnuValcomVeh(new BigDecimal("50000000"))
                .pnuAntgVeh(10)
                .pnuEdacli(20)
                .pnuCansini(2)
                .build();

        TarifaResponse responseEsperada = TarifaResponse.builder()
                .tarifa(new BigDecimal("24000000.00"))
                .exitoso(true)
                .build();

        when(tarifaRepository.calcularTarifa(request)).thenReturn(responseEsperada);

        // Act
        TarifaResponse responseReal = tarifaService.calcularTarifa(request);

        // Assert
        assertEquals(responseEsperada.getTarifa(), responseReal.getTarifa());
        assertEquals(responseEsperada.isExitoso(), responseReal.isExitoso());
        verify(tarifaRepository).calcularTarifa(request);
    }
}