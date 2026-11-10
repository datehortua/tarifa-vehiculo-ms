package com.pruebas.tarifa.repository;

import com.pruebas.tarifa.dto.TarifaRequest;
import com.pruebas.tarifa.dto.TarifaResponse;
import com.pruebas.tarifa.exception.TarifaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.CallableStatementCreator; // <-- Importación correcta
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class TarifaRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private TarifaRepository tarifaRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tarifaRepository = new TarifaRepository(jdbcTemplate);
    }

    @Test
    public void testCalcularTarifa_Exitoso() {
        // 1. Arrange: Preparar la respuesta simulada de la BD (Mapa de salida)
        Map<String, Object> mockDbOut = new HashMap<>();
        mockDbOut.put("PNU_TARIFA", new BigDecimal("24000000.00"));
        mockDbOut.put("PNU_SALERR", null); // Sin errores
        mockDbOut.put("PVA_SALERR", null);

        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(mockDbOut);

        TarifaRequest request = TarifaRequest.builder()
                .pnuValcomVeh(new BigDecimal("50000000"))
                .pnuAntgVeh(10)
                .pnuEdacli(20)
                .pnuCansini(2)
                .build();

        // 2. Act
        TarifaResponse response = tarifaRepository.calcularTarifa(request);

        // 3. Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("24000000.00"), response.getTarifa());
        assertTrue(response.isExitoso());
        assertNull(response.getSalerr());
    }

    @Test
    public void testCalcularTarifa_ErrorLogicaNegocio() {
        // Simulamos que el SP devuelve error de negocio (SALERR no nulo)
        Map<String, Object> mockDbOut = new HashMap<>();
        mockDbOut.put("PNU_TARIFA", null);
        mockDbOut.put("PNU_SALERR", 1);
        mockDbOut.put("PVA_SALERR", "El vehículo no cumple las reglas");

        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(mockDbOut);

        TarifaRequest request = TarifaRequest.builder()
                .pnuValcomVeh(new BigDecimal("1000")) // Datos que provocarían error
                .pnuAntgVeh(1)
                .pnuEdacli(18)
                .pnuCansini(0)
                .build();

        // 2. Act
        TarifaResponse response = tarifaRepository.calcularTarifa(request);

        // 3. Assert
        assertFalse(response.isExitoso());
        assertEquals(1, response.getSalerr());
        assertEquals("El vehículo no cumple las reglas", response.getPvaSalerr());
    }

    @Test
    public void testCalcularTarifa_ExcepcionTecnica() {
        // Simulamos una caída de conexión o error de SQL
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenThrow(new RuntimeException("ORA-12541: TNS:no listener"));

        TarifaRequest request = TarifaRequest.builder()
                .pnuValcomVeh(BigDecimal.TEN)
                .pnuAntgVeh(1).pnuEdacli(1).pnuCansini(1).build();

        // Act & Assert
        TarifaException exception = assertThrows(TarifaException.class, () -> {
            tarifaRepository.calcularTarifa(request);
        });

        assertTrue(exception.getMessage().contains("ORA-12541"));
    }
}