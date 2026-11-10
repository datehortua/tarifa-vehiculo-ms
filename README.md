# Tarifa Vehículo API

Backend para la aplicación de cálculo de tarifa de seguro de vehículo, desarrollado como API REST con Spring Boot y conexión directa a Oracle mediante procedimientos almacenados.

## 1. Objetivo del proyecto

La solución expone un endpoint que recibe los datos del vehículo y del cliente para calcular una tarifa de seguro. La lógica de negocio principal se ejecuta en un procedimiento almacenado de Oracle, y la API actúa como capa de integración, validación y exposición de resultados a la capa frontend o a otros consumidores.

## 2. Alcance funcional

- Recepción de datos de entrada mediante JSON.
- Validación de datos obligatorios y rangos de negocio.
- Llamada al procedimiento `PCK_SEGUROS_VEHICULO.SP_GET_TARIFA_VEHICULO`.
- Cálculo y respuesta con `tarifa`, `salerr`, `pvaSalerr` y `exitoso`.
- Manejo centralizado de errores y respuestas HTTP coherentes.
- Soporte de CORS para consumo desde una interfaz web local.

## 3. Arquitectura implementada

La aplicación sigue una arquitectura en capas, típica de servicios REST con Spring Boot:

- Capa de presentación: `controller`
  - `TarifaController`
  - Expone el endpoint REST para calcular la tarifa.
- Capa de servicio: `service`
  - `TarifaService`
  - Orquesta la lógica de la aplicación.
- Capa de acceso a datos: `repository`
  - `TarifaRepository`
  - Ejecuta el procedimiento almacenado en Oracle mediante `JdbcTemplate` / `CallableStatement`.
- Capa de transporte / DTOs: `dto`
  - `TarifaRequest`: estructura de entrada.
  - `TarifaResponse`: estructura de salida.
- Manejo de errores: `exception`
  - `GlobalExceptionHandler`
  - `TarifaException`
  - Centraliza validaciones y errores de negocio.
- Configuración: `config`
  - `CorsConfig`: configuración de CORS para consumo desde frontend local.
- Recursos de la aplicación: `resources`
  - `application.yaml`: configuración Spring Boot y datasource.
  - `db/migration/V1__crear_procedimientos.sql`: creación del paquete y procedimientos Oracle.

### Flujo de ejecución

1. El cliente envía una petición HTTP al endpoint `POST /api/v1/tarifas/calcular`.
2. `TarifaController` recibe el JSON y valida el modelo con Bean Validation.
3. `TarifaService` delega el cálculo al repositorio.
4. `TarifaRepository` llama al procedimiento Oracle `PCK_SEGUROS_VEHICULO.SP_GET_TARIFA_VEHICULO`.
5. El procedimiento valida reglas del negocio y devuelve la tarifa o errores de validación.
6. La API responde con un objeto JSON estructurado.

## 4. Stack tecnológico y versiones

A continuación la versión principal del stack utilizado en el backend para facilitar la documentación compartida con cliente y equipo de desarrollo.

| Componente | Versión / detalle | Observación |
|---|---|---|
| Java | 17 | JDK requerido por la aplicación |
| Spring Boot | 3.3.5 | Framework base del backend |
| Spring Web | Incluido en Spring Boot 3.3.5 | API REST |
| Spring Validation | Incluido en Spring Boot 3.3.5 | Validación de DTOs |
| Spring JDBC | Incluido en Spring Boot 3.3.5 | Acceso a base de datos |
| Maven | 3.x (gestión del proyecto) | Construcción y ejecución |
| Oracle JDBC Driver | `ojdbc11` 23.5.0.24.07 | Conector Oracle para Java |
| Oracle Database | compatible con la conexión `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` | Base de datos transaccional usada por la lógica de negocio |
| Flyway | gestionado por Spring Boot 3.3.5 | Migraciones y creación de objetos SQL |
| HikariCP | gestionado por Spring Boot 3.3.5 | Pool de conexiones |
| Lombok | incluido como dependencia opcional | Reduce boilerplate |
| JUnit | gestionado por Spring Boot 3.3.5 | Pruebas unitarias |
| Mockito | gestionado por Spring Boot 3.3.5 | Mocks para pruebas |
| TestNG | 7.x / versión definida por dependencia directa | Se usa en conjunto con pruebas de la aplicación |

## 5. Dependencias principales

El proyecto utiliza las siguientes librerías clave en `pom.xml`:

- `spring-boot-starter-web`
- `spring-boot-starter-jdbc`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`
- `com.oracle.database.jdbc:ojdbc11`
- `org.flywaydb:flyway-core`
- `org.flywaydb:flyway-database-oracle`
- `org.projectlombok:lombok`

## 6. Base de datos y migraciones

La aplicación se conecta a una base de datos Oracle local mediante JDBC. La configuración se encuentra en `src/main/resources/application.yaml`.

Puntos clave:

- Puerto del servicio: `8080`
- URL JDBC: `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`
- Usuario: `SYSTEM`
- Password: `Dbasql`
- Pool de conexiones: HikariCP

El proyecto incluye una migración de Flyway:

- `src/main/resources/db/migration/V1__crear_procedimientos.sql`

Esta migración crea el paquete `PCK_SEGUROS_VEHICULO`, con la lógica principal de negocio en `SP_GET_TARIFA_VEHICULO` y `FN_REGNEG_SEGVEH`.

## 7. Endpoint disponible

### POST /api/v1/tarifas/calcular

#### Request body

```json
{
  "pnuValcomVeh": 25000000,
  "pnuAntgVeh": 4,
  "pnuEdacli": 30,
  "pnuCansini": 2
}
```

#### Campos esperados

- `pnuValcomVeh`: valor comercial del vehículo
- `pnuAntgVeh`: antigüedad del vehículo
- `pnuEdacli`: edad del cliente
- `pnuCansini`: cantidad de siniestros

#### Respuesta esperada

```json
{
  "tarifa": 3250000,
  "salerr": 0,
  "pvaSalerr": null,
  "exitoso": true
}
```

## 8. Reglas de negocio principales

La lógica de negocio valida las siguientes condiciones:

- El valor comercial del vehículo debe ser mayor o igual a `20000000`.
- La antigüedad del vehículo debe cumplir criterios mínimos.
- La edad del cliente debe ser mayor o igual a `18` y menor o igual a `99`.
- La cantidad de siniestros debe respetar las reglas definidas por la lógica de cálculo.

Si alguna validación falla, el procedimiento devuelve error con `salerr` y `pvaSalerr` o la API responde con error de validación HTTP 400.

## 9. Ejecución del proyecto

Requisitos:

- Java 17+
- Maven 3.9+
- Oracle Database disponible en `localhost:1521/FREEPDB1`

Pasos:

```bash
mvn clean install
mvn spring-boot:run
```

La API quedará levantada en:

```text
http://localhost:8080
```

## 10. Estado del desarrollo

El backend se encuentra en una etapa funcional orientada a servicio REST para cálculo de tarifa. La arquitectura actual permite:

- Escalar por nuevas reglas de negocio.
- Integrarse con frontend o APIs externas.
- Mantener la lógica crítica en base de datos Oracle.
- Facilitar mantenimiento mediante migraciones y capas bien definidas.

## 11. Consideraciones para cliente y equipo técnico

- Este repositorio corresponde al backend, no al frontend.
- La API está preparada para consumir desde una interfaz web o app cliente.
- La lógica tarifaria central está encapsulada en procedimientos Oracle para evitar duplicación de reglas y facilitar auditoría.
- El uso de Flyway permite versionar la base de datos y mantener consistencia entre entornos.

## 12. Estructura del proyecto

```text
src/
├── main/
│   ├── java/com/pruebas/tarifa/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── service/
│   │   └── TarifaVehiculoApplication.java
│   └── resources/
│       ├── application.yaml
│       └── db/migration/V1__crear_procedimientos.sql
└── test/
    └── java/com/pruebas/tarifa/
```

## 13. Resumen ejecutivo

El backend de `tarifa-vehiculo` es una API REST en Java con Spring Boot que calcula tarifas de seguro para vehículos usando Oracle como motor de reglas de negocio. La solución está organizada por capas, usa validación de entrada, maneja errores centralizados y ejecuta la lógica principal en procedimientos almacenados para un alto nivel de trazabilidad y consistencia.
