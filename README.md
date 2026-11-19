# contracts-api

Backend del sistema de contratación de servicios. Spring Boot 4.1.2, Java 21 o superior y MySQL.

## Requisitos

- Java 21 o superior (`java -version`).
- Docker, si vas a levantar MySQL local.
- Maven no hace falta: el proyecto incluye Maven Wrapper.

## Arrancar

```sh
docker compose up -d
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

En Git Bash sobre Windows, usa `./mvnw.cmd spring-boot:run`. La API escucha en `http://localhost:8080`; puedes cambiar el puerto con `PORT` (por ejemplo, `PORT=8090 ./mvnw spring-boot:run`; en PowerShell, `$env:PORT=8090; .\mvnw.cmd spring-boot:run`). La conexión a MySQL se configura en `src/main/resources/application.properties`. Para usar la base local de Docker, configura allí la URL, el usuario y la clave correspondientes al servicio de `docker-compose.yml`. Las tablas se actualizan al arrancar (`spring.jpa.hibernate.ddl-auto=update`).

## Probar

- Pruebas Java: `./mvnw test` (Windows PowerShell: `.\mvnw.cmd test`; Git Bash: `./mvnw.cmd test`).
- Colección Postman: importa `postman/contracts-api.postman_collection.json` y ejecuta *Run collection*. Contiene **72 solicitudes y 123 comprobaciones `pm.test`**; los casos rotulados explícitamente en la colección son CP-42 a CP-55. Cambia `baseUrl` si la API usa otro puerto. `angularOrigin` se usa para la prueba CORS; las demás variables de IDs y tokens se completan durante la ejecución.
- Desde la terminal: `postman collection run postman/contracts-api.postman_collection.json --env-var baseUrl=http://localhost:8080`.

## Endpoints

Las rutas no públicas requieren autenticación y responden `401` si no se presenta una credencial válida. Son públicas el login, el registro de usuarios, las consultas de planes y la consulta de plantillas. Las peticiones inválidas responden `400`; el cuerpo es Problem Detail y añade `errors` con los campos que fallaron. No hay endpoints `PATCH`: las modificaciones usan `PUT`.

| Método | Ruta | Query params | Respuestas |
|---|---|---|---|
| POST | `/api/v1/auth/login` | — | 200, 400, 401 |
| POST | `/api/v1/auth/logout` | — | 204 |
| POST | `/api/v1/users` | — | 201, 400, 409 |
| GET | `/api/v1/customers` | `search` opcional | 200 |
| GET | `/api/v1/customers/{id}` | — | 200, 404 |
| POST | `/api/v1/customers` | — | 201, 400, 409 |
| PUT | `/api/v1/customers/{id}` | — | 200, 400, 404, 409 |
| DELETE | `/api/v1/customers/{id}` | — | 204, 404, 409 |
| GET | `/api/v1/plans` | — | 200 |
| GET | `/api/v1/plans/{id}` | — | 200, 404 (incluye `services`) |
| POST | `/api/v1/plans` | — | 201, 400, 409 |
| PUT | `/api/v1/plans/{id}` | — | 200, 400, 404, 409 |
| DELETE | `/api/v1/plans/{id}` | — | 204, 404, 409 |
| GET | `/api/v1/plans/{planId}/services` | — | 200, 404 |
| POST | `/api/v1/plans/{planId}/services` | — | 201, 400, 404, 409 |
| PUT | `/api/v1/plans/{planId}/services/{serviceId}` | — | 200, 400, 404 |
| DELETE | `/api/v1/plans/{planId}/services/{serviceId}` | — | 204, 404 |
| GET | `/api/v1/service-types` | — | 200 |
| GET | `/api/v1/service-types/{id}` | — | 200, 404 |
| POST | `/api/v1/service-types` | — | 201, 400, 409 |
| PUT | `/api/v1/service-types/{id}` | — | 200, 400, 404, 409 |
| DELETE | `/api/v1/service-types/{id}` | — | 204, 404, 409 |
| GET | `/api/v1/support-services` | — | 200 |
| GET | `/api/v1/support-services/{id}` | — | 200, 404 |
| POST | `/api/v1/support-services` | — | 201, 400, 404, 409 |
| PUT | `/api/v1/support-services/{id}` | — | 200, 400, 404, 409 |
| DELETE | `/api/v1/support-services/{id}` | — | 204, 404, 409 |
| GET | `/api/v1/templates` | `planId` opcional | 200 |
| POST | `/api/v1/templates` | — | 201, 400, 404, 409 |
| PUT | `/api/v1/templates/{id}` | — | 200, 400, 404, 409 |
| DELETE | `/api/v1/templates/{id}` | — | 204, 404 |
| GET | `/api/v1/contracts` | `status`, `customerId`, `validity` opcionales | 200 |
| GET | `/api/v1/contracts/{id}` | — | 200, 404 |
| POST | `/api/v1/contracts` | — | 201, 400, 404 |
| PUT | `/api/v1/contracts/{id}` | — | 200, 400, 404, 409 |
| DELETE | `/api/v1/contracts/{id}` | — | 204, 404, 409 |
| GET | `/api/v1/contracts/{id}/pdf` | — | 200, 404 |
| GET | `/api/v1/contracts/{id}/signed-document` | — | 200, 404, 502 |
| POST | `/api/v1/contracts/{id}/upload-signed` | Multipart: `file` | 200, 400, 404, 409, 415, 502 |

`409` indica un dato duplicado o una relación que impide eliminar el recurso; un contrato firmado tampoco se puede editar ni eliminar. `404` indica que no existe el recurso relacionado. La carga de documento firmado acepta archivos con tipo de contenido `application/pdf`; los fallos del almacenamiento externo responden `502`.

Los filtros `status` aceptan `GENERATED` o `SIGNED`; `validity` acepta `PENDING`, `ACTIVE`, `EXPIRING` o `EXPIRED`. `search` busca coincidencias parciales, sin distinguir mayúsculas, en el nombre del cliente.

### Validación de solicitudes

- **Customers:** `businessName` no puede estar vacío; `ruc` debe tener 11 dígitos; `email`, si se envía, debe tener formato válido. El RUC no se puede repetir.
- **Plans:** `name` obligatorio; `responseTimeHours` positivo; `monthlyCost` positivo, hasta 8 dígitos enteros y 2 decimales; `durationMonths` desde 1. El nombre no se puede repetir.
- **Servicios de plan:** `supportServiceId`, `monthlyLimit` y `extraCost` obligatorios; límite y costo extra no negativos; costo extra hasta 8 dígitos enteros y 2 decimales. No se repite el mismo servicio en un plan.
- **Service types:** `name` obligatorio y único.
- **Support services:** `name` y `serviceTypeId` obligatorios; el tipo debe existir y el nombre no se puede repetir. `unit` es opcional.
- **Templates:** `planId`, `name` y `content` obligatorios y no vacíos; el plan debe existir y el nombre no se puede repetir.
- **Contracts:** `customerId`, `planId` y `startDate` obligatorios; cliente y plan deben existir. El monto se copia del plan y `endDate` se calcula con su duración.
- **Users:** `name`, `email`, `password` y `role` obligatorios; email válido; nombre y email hasta 255 caracteres, contraseña hasta 72 y rol hasta 20.
- **Login:** email y contraseña obligatorios.

## Plantilla desde cero

Si el `content` de `POST /api/v1/templates` no incluye el texto `# CONTRATO`, no reemplaza la plantilla. Se guarda como una cláusula extra, al final del punto 1 de la plantilla por defecto.

Para armar una plantilla propia, el `content` tiene que incluir `# CONTRATO`. Ese texto se guarda tal cual. Cada línea se interpreta así:

- `# ` y el título: título centrado.
- `## ` y el nombre de la sección: subtítulo.
- `N.º ` o `N.° `: número del contrato, centrado.
- Cualquier otra línea: párrafo. Una línea en blanco no se imprime.

Las marcas `{{ }}` se reemplazan al generar el PDF del contrato. Un `content` de plantilla completa:

```text
# CONTRATO DE PRESTACIÓN DE SERVICIOS DE SOPORTE

N.º {{numero}}

SERVIT y {{cliente}}, RUC {{ruc}}, firman este contrato el {{fecha_emision}}.

## 1) Servicios del plan.
{{servicios}}

## 2) Vigencia.
El servicio va del {{fecha_inicio}} al {{fecha_fin}}, por {{duracion}}.

## 3) Pago.
{{cliente}} pagará {{costo_mensual}} al mes. El total del periodo es {{total}}.
```

Otras marcas útiles: `{{plan}}`, `{{tiempo_atencion}}`, `{{representante}}`, `{{direccion}}`, `{{correo}}`, `{{telefono}}` y `{{firmado}}`.

## Estructura

Paquetes bajo `src/main/java/pe/edu/solweb/contracts/`:

```text
config/       seguridad HTTP, CORS y Cloudinary
controller/   controladores REST
dto/          solicitudes y respuestas de la API
entity/       entidades JPA y enums de dominio
exception/    excepciones y manejo central de errores
mappers/      conversión entre entidades y DTOs
repository/   acceso a datos con Spring Data JPA
security/     autenticación JWT y revocación de tokens
service/      reglas de negocio e integraciones
  impl/       implementaciones de servicios
```
