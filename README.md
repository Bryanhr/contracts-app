# contracts-api

Backend del sistema de contratación de servicios (semana 2). Spring Boot 4.1, Java 21 o superior, MySQL 8.4.

## Requisitos

- Java 21 o superior (`java -version`).
- Docker, para levantar MySQL. Maven no hace falta: el proyecto trae `./mvnw`, que lo descarga solo.

## Arrancar

```sh
docker compose up -d        # MySQL en el puerto 3306 (base contracts_db, usuario y clave: contracts)
./mvnw spring-boot:run      # API en http://localhost:8080
```

Si el puerto 8080 está ocupado, usa otro: `PORT=8090 ./mvnw spring-boot:run`.
Para conectarte a otra base de datos, define las variables `DB_HOST`, `DB_PORT`, `DB_USER` y `DB_PASSWORD`.

Las tablas se crean solas al arrancar (`spring.jpa.hibernate.ddl-auto=update`). El esquema exportado está en `db/schema.sql`.

## Probar

- Prueba automática de la regla de vigencia: `./mvnw test`.
- Prueba de la API: importa `postman/contracts-api.postman_collection.json` en Postman, cambia la variable `baseUrl` si usas otro puerto y ejecuta *Run collection*. Desde la terminal también puedes usar:
  `postman collection run postman/contracts-api.postman_collection.json --env-var baseUrl=http://localhost:8080`

## Endpoints

| Método | Ruta | Respuestas |
|---|---|---|
| GET | `/api/v1/customers` | 200 |
| GET | `/api/v1/customers/{id}` | 200, 404 |
| POST | `/api/v1/customers` | 201, 400 (faltan datos o el RUC no tiene 11 dígitos), 409 (RUC repetido) |
| GET | `/api/v1/plans` | 200 |
| GET | `/api/v1/plans/{id}` | 200, 404 |
| POST | `/api/v1/plans` | 201, 400, 409 (nombre repetido) |
| GET | `/api/v1/templates?planId=` | 200 (`planId` es opcional) |
| POST | `/api/v1/templates` | 201, 400, 404 (el plan no existe), 409 (nombre repetido) |
| GET | `/api/v1/contracts?status=&customerId=` | 200 (los dos filtros son opcionales) |
| GET | `/api/v1/contracts/{id}` | 200, 404 |
| POST | `/api/v1/contracts` | 201, 400, 404 (el cliente o el plan no existen) |

Para registrar un contrato solo se envía `{ "customerId", "planId", "startDate" }`. El monto se copia del plan, `endDate` es `startDate + durationMonths`, el estado inicial es `GENERATED` y `validity` se calcula en cada respuesta.

Los errores se devuelven como JSON con `status`, `title` y `detail`. Los 400 incluyen además `errors` con el campo que falló.

## Estructura

```
controller/  recibe las peticiones y devuelve los códigos HTTP
service/     reglas de negocio
repository/  acceso a la base de datos (Spring Data JPA)
entity/      tablas: Customer, Plan, Template, Contract, ServiceType, SupportService, PlanSupportService, User
dto/         lo que entra y sale de la API
exception/   manejo central de errores
```
