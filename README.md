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
| Springdoc OpenAPI / Swagger UI | 2.5.0 | Documentación interactiva y pruebas manuales de endpoints |
| Flyway | gestionado por Spring Boot 3.3.5 | Migraciones y creación de objetos SQL |
| HikariCP | gestionado por Spring Boot 3.3.5 | Pool de conexiones |
| Lombok | incluido como dependencia opcional | Reduce boilerplate |
| JUnit | gestionado por Spring Boot 3.3.5 | Pruebas unitarias |
| Mockito | gestionado por Spring Boot 3.3.5 | Mocks para pruebas |
| TestNG | `RELEASE` (sin versión fijada) | Dependencia declarada en alcance `compile`; conviene fijar versión y revisar el alcance |

## 5. Dependencias principales

El proyecto utiliza las siguientes librerías clave en `pom.xml`:

- `spring-boot-starter-web`
- `spring-boot-starter-jdbc`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`
- `org.springdoc:springdoc-openapi-starter-webmvc-ui` 2.5.0
- `com.oracle.database.jdbc:ojdbc11`
- `org.flywaydb:flyway-core`
- `org.flywaydb:flyway-database-oracle`
- `org.projectlombok:lombok`

## 6. Base de datos y migraciones

La aplicación se conecta a una base de datos Oracle local mediante JDBC. La configuración se encuentra en `src/main/resources/application.yaml`.

Puntos clave:

- Puerto del servicio: `8080`
- URL JDBC: `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`
- Usuario y password: configurados actualmente en `application.yaml`; usar credenciales protegidas y específicas por entorno.
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

## 8. Pruebas del backend con Swagger UI

El proyecto incluye Springdoc OpenAPI y Swagger UI para consultar la documentación interactiva y ejecutar pruebas manuales contra la API. Con la aplicación iniciada, abre en el navegador:

```text
http://localhost:8080/swagger-ui/index.html
```

El documento OpenAPI en formato JSON también está disponible en:

```text
http://localhost:8080/v3/api-docs
```

Para probar el cálculo de tarifa:

1. En Swagger UI, localiza `POST /api/v1/tarifas/calcular`.
2. Selecciona **Try it out**.
3. Ingresa el JSON de ejemplo de la sección anterior y selecciona **Execute**.
4. Revisa el código HTTP y el cuerpo de respuesta. Una solicitud válida devuelve la tarifa y los campos de resultado; los datos que no cumplan las validaciones del request devuelven HTTP 400.

Las pruebas que invoquen el cálculo requieren que Oracle esté disponible y que el paquete `PCK_SEGUROS_VEHICULO` exista en la base de datos configurada. Swagger UI facilita las pruebas manuales; no sustituye las pruebas automatizadas del proyecto.

## 9. Reglas de negocio principales

La lógica de negocio valida las siguientes condiciones:

- El valor comercial del vehículo debe ser mayor o igual a `20000000`.
- La antigüedad del vehículo debe cumplir criterios mínimos.
- La edad del cliente debe ser mayor o igual a `18` y menor o igual a `99`.
- La cantidad de siniestros debe respetar las reglas definidas por la lógica de cálculo.

Si alguna validación falla, el procedimiento devuelve error con `salerr` y `pvaSalerr` o la API responde con error de validación HTTP 400.

## 10. Ejecución del proyecto

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

## 11. Estado del desarrollo

El backend se encuentra en una etapa funcional orientada a servicio REST para cálculo de tarifa. La arquitectura actual permite:

- Escalar por nuevas reglas de negocio.
- Integrarse con frontend o APIs externas.
- Mantener la lógica crítica en base de datos Oracle.
- Facilitar mantenimiento mediante migraciones y capas bien definidas.

## 12. Riesgos y limitaciones

Los siguientes puntos deben revisarse antes de desplegar el servicio en un entorno compartido o productivo:

| Riesgo o limitación | Impacto | Recomendación |
|---|---|---|
| Credenciales de base de datos declaradas en `application.yaml` | Exposición o uso indebido de la base de datos si se distribuyen los archivos tal como están. | Externalizar usuario y contraseña mediante variables de entorno o un gestor de secretos; utilizar una cuenta de mínimo privilegio y rotar cualquier credencial que ya haya sido compartida. |
| Flyway permite `clean` (`clean-disabled: false`) y tiene `clean-on-validation-error: true` | Una inconsistencia de validación puede provocar el borrado del esquema administrado por Flyway. | Deshabilitar `clean` en entornos persistentes y no limpiar automáticamente ante errores de validación. Verificar los cambios en una base no productiva y contar con respaldos. |
| Configuración de esquema con el usuario `SYSTEM` | La aplicación y sus migraciones operan con una cuenta administrativa y el esquema queda acoplado a esa configuración. | Crear un esquema y usuario de aplicación con permisos mínimos, separado de las cuentas administrativas. |
| Reglas de tarifa implementadas en PL/SQL | La lógica depende de Oracle y su despliegue requiere que el paquete esté correctamente instalado y versionado. | Coordinar las migraciones de aplicación y base de datos; mantener pruebas de regresión de las reglas y respaldar los objetos de base de datos. |
| No se observa configuración de autenticación/autorización en el servicio | Los endpoints podrían quedar accesibles para cualquier cliente que alcance la red donde esté desplegada la API. | Definir el mecanismo de identidad y autorización antes de exponerla fuera de una red controlada; usar HTTPS en los entornos desplegados. |
| CORS permite únicamente `http://localhost:4200` | El frontend desplegado en otro dominio no podrá invocar la API desde el navegador hasta configurar su origen. | Definir una lista explícita de orígenes por entorno; evitar comodines en producción. |
| Las respuestas de error incluyen mensajes de excepción | Detalles internos o de la base de datos podrían llegar al consumidor de la API. | Devolver mensajes públicos genéricos y registrar el detalle técnico en logs protegidos, asociado a un identificador de seguimiento. |
| Oracle es requisito para ejecutar la migración y probar el flujo completo | Sin una instancia accesible y el paquete instalado, la prueba del endpoint de cálculo no es end-to-end. | Documentar y aprovisionar una instancia por entorno; usar pruebas aisladas para validar las capas que no requieren conexión real. |
| TestNG usa la versión dinámica `RELEASE` y está en alcance `compile` | La resolución de dependencias puede cambiar entre builds y la librería puede incluirse innecesariamente en ejecución. | Fijar una versión estable y moverla a alcance de pruebas si solo se utiliza en tests. |

Estas observaciones describen la configuración inspeccionada en este repositorio; no sustituyen una revisión de seguridad, operaciones ni cumplimiento para el entorno de despliegue.

## 13. Alternativas arquitectónicas consideradas

Las alternativas siguientes son opciones relevantes para evolución del sistema. La arquitectura implementada es la descrita en la sección 3; estas opciones no forman parte del alcance actual.

| Decisión | Implementación actual | Alternativa | Consideración |
|---|---|---|---|
| Ubicación de las reglas tarifarias | Procedimientos almacenados PL/SQL en Oracle | Implementar las reglas en la capa de servicio Java | Java facilita pruebas y portabilidad; PL/SQL mantiene la lógica cerca de los datos y reutilizable desde otros clientes Oracle. La decisión debe considerar gobierno de reglas, auditoría y propiedad del equipo sobre cada capa. |
| Acceso a datos | JDBC con `JdbcTemplate` y llamada a procedimiento | JPA/Hibernate o `SimpleJdbcCall` | JDBC ofrece control explícito sobre la llamada y los parámetros del procedimiento; JPA es más adecuado para operaciones orientadas a entidades, mientras que `SimpleJdbcCall` puede hacer más declarativa la invocación. |
| Persistencia y motor de base de datos | Oracle Database | Otro motor relacional o reglas en un servicio dedicado | Cambiar de motor reduce el acoplamiento a Oracle, pero implica migrar PL/SQL, validar resultados y operar otra plataforma. |
| Interfaz de integración | API REST síncrona | Mensajería asíncrona | REST es directo para una solicitud interactiva de cotización; mensajería puede ser útil si se requieren procesamiento desacoplado, reintentos o alto volumen, a cambio de mayor complejidad y respuestas diferidas. |
| Configuración por entorno | Archivo `application.yaml` con parámetros locales | Variables de entorno, perfiles y gestor de secretos | La configuración externa mejora la separación entre código, credenciales y entornos, y es la opción recomendada para despliegues compartidos o productivos. |

La selección final de alternativas debe validarse con requisitos de disponibilidad, seguridad, volumen, costos de operación y responsabilidades del equipo cliente.

## 14. Consideraciones para cliente y equipo técnico

- Este repositorio corresponde al backend, no al frontend.
- La API está preparada para consumir desde una interfaz web o app cliente.
- La lógica tarifaria central está encapsulada en procedimientos Oracle para evitar duplicación de reglas y facilitar auditoría.
- El uso de Flyway permite versionar la base de datos y mantener consistencia entre entornos.

## 15. Estructura del proyecto

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

## 16. Resumen ejecutivo

El backend de `tarifa-vehiculo` es una API REST en Java con Spring Boot que calcula tarifas de seguro para vehículos usando Oracle como motor de reglas de negocio. La solución está organizada por capas, usa validación de entrada, maneja errores centralizados y ejecuta la lógica principal en procedimientos almacenados para un alto nivel de trazabilidad y consistencia.
