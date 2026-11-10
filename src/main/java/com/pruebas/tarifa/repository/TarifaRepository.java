package com.pruebas.tarifa.repository;

import com.pruebas.tarifa.dto.TarifaRequest;
import com.pruebas.tarifa.dto.TarifaResponse;
import com.pruebas.tarifa.exception.TarifaException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class TarifaRepository {

    private final JdbcTemplate jdbcTemplate;

    public TarifaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TarifaResponse calcularTarifa(TarifaRequest req) {
        try {
            // Llamada directa al procedimiento
            String sql = "{call PCK_SEGUROS_VEHICULO.SP_GET_TARIFA_VEHICULO(?, ?, ?, ?, ?, ?, ?)}";

            Map<String, Object> out = jdbcTemplate.call(connection -> {
                CallableStatement cs = connection.prepareCall(sql);
                cs.setBigDecimal(1, req.getPnuValcomVeh());
                cs.setInt(2, req.getPnuAntgVeh());
                cs.setInt(3, req.getPnuEdacli());
                cs.setInt(4, req.getPnuCansini());
                cs.registerOutParameter(5, Types.NUMERIC);
                cs.registerOutParameter(6, Types.NUMERIC);
                cs.registerOutParameter(7, Types.VARCHAR);
                return cs;
            }, List.of(
                    new SqlParameter(Types.NUMERIC), // 1
                    new SqlParameter(Types.NUMERIC), // 2
                    new SqlParameter(Types.NUMERIC), // 3
                    new SqlParameter(Types.NUMERIC), // 4
                    new SqlOutParameter("PNU_TARIFA", Types.NUMERIC), // 5
                    new SqlOutParameter("PNU_SALERR", Types.NUMERIC), // 6
                    new SqlOutParameter("PVA_SALERR", Types.VARCHAR)  // 7
            ));

            // Extraemos los valores devueltos
            BigDecimal tarifa = toBigDecimal(out.get("PNU_TARIFA"));
            Integer salerr = toInteger(out.get("PNU_SALERR"));
            String pvaSalerr = (String) out.get("PVA_SALERR");

            boolean exitoso = (salerr == null || salerr == 0)
                    && (pvaSalerr == null || pvaSalerr.isBlank());

            return TarifaResponse.builder()
                    .tarifa(tarifa)
                    .salerr(salerr)
                    .pvaSalerr(pvaSalerr)
                    .exitoso(exitoso)
                    .build();

        } catch (Exception e) {
            throw new TarifaException("Error al invocar SP_GET_TARIFA_VEHICULO: " + e.getMessage(), e);
        }
    }

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) return null;
        if (o instanceof BigDecimal bd) return bd;
        if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(o.toString());
    }

    private Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        return Integer.valueOf(o.toString());
    }
}