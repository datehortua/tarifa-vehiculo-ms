package com.pruebas.tarifa.controller; // Verifica que este sea tu paquete real

import com.pruebas.tarifa.dto.TarifaRequest;
import com.pruebas.tarifa.dto.TarifaResponse;
import com.pruebas.tarifa.service.TarifaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//Imports correctos para simular peticiones en MockMvc
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TarifaController.class)
public class TarifaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarifaService tarifaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCalcularTarifa_Exitoso() throws Exception {
        // 1. Preparar datos (Arrange)
        TarifaRequest request = TarifaRequest.builder()
                .pnuValcomVeh(new BigDecimal("50000000"))
                .pnuAntgVeh(10)
                .pnuEdacli(20)
                .pnuCansini(2)
                .build();

        TarifaResponse response = TarifaResponse.builder()
                .tarifa(new BigDecimal("24000000.00"))
                .exitoso(true)
                .build();

        // Usamos 'any()' directamente importado de ArgumentMatchers
        when(tarifaService.calcularTarifa(any(TarifaRequest.class))).thenReturn(response);

        // 2. Ejecutar y Verificar (Act & Assert)
        // Usamos post() de MockMvcRequestBuilders
        mockMvc.perform(post("/api/v1/tarifas/calcular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarifa").value(24000000.00))
                .andExpect(jsonPath("$.exitoso").value(true));
    }

    @Test
    public void testCalcularTarifa_ValidacionFallida() throws Exception {
        // Petición con campos inválidos (ej. valores nulos)
        TarifaRequest request = TarifaRequest.builder().build();

        mockMvc.perform(post("/api/v1/tarifas/calcular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error de validación"));
    }
}