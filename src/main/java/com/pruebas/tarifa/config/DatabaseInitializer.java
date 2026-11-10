package com.pruebas.tarifa.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // 2. Ejecutar el Body (Cuerpo)
            ejecutarScript(statement, "db/migration/V2__crear_body_pck.sql");

            // 1. Ejecutar el Header (Cabecera)
            ejecutarScript(statement, "db/migration/V1__crear_header_pck.sql");

            // 3. Ejecutar Completo
            ejecutarScript(statement, "db/migration/V3__compilar_pck.sql");

            System.out.println(">>> [INICIALIZADOR] Paquete PCK_SEGUROS_VEHICULO creado correctamente.");

        } catch (Exception e) {
            System.err.println(">>> [INICIALIZADOR] Error al ejecutar el script SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ejecutarScript(Statement statement, String rutaArchivo) throws Exception {
        ClassPathResource resource = new ClassPathResource(rutaArchivo);
        try (InputStream inputStream = resource.getInputStream()) {
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            // Quitamos la barra "/" si quedó al final del archivo
            sql = sql.replaceAll("(?m)^/\\s*$", "").trim();

            // Ejecutamos el bloque completo
            statement.execute(sql);
        }
    }
}