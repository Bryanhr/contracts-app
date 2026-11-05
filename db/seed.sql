-- amount es el costo mensual del plan, no el total del periodo.

SET NAMES utf8mb4;

INSERT INTO `plan` (`id`, `duration_months`, `monthly_cost`, `name`, `response_time_hours`) VALUES
(1, 6,  450.00,  'Básico',            48),
(2, 12, 900.00,  'Estándar',          24),
(3, 12, 1500.00, 'Premium',           8),
(4, 24, 2800.00, 'Empresarial',       4),
(5, 36, 4500.00, 'Corporativo 24x7',  1),
(6, 3,  250.00,  'Starter',           72);

INSERT INTO `customer` (`id`, `address`, `business_name`, `email`, `phone`, `representative`, `ruc`) VALUES
(1, 'Av. Javier Prado Este 1234, San Isidro, Lima',       'Inversiones Andinas S.A.C.',        'contacto@inversionesandinas.pe', '987654321', 'Carlos Mendoza Rivas',   '20512345678'),
(2, 'Jr. de la Unión 456, Cercado de Lima',               'Comercial Pacífico E.I.R.L.',       'ventas@comercialpacifico.pe',    '956123478', 'Lucía Fernández Soto',   '20601234567'),
(3, 'Av. Larco 789, Miraflores, Lima',                    'Tecnologías del Sur S.A.',          'info@tecnosur.com.pe',           '945612378', 'Jorge Ramírez Quispe',   '20456789123'),
(4, 'Calle Los Cedros 321, Surco, Lima',                  'Constructora Horizonte S.A.C.',     'admin@horizonte.com.pe',         '934567812', 'María Torres Villanueva','20567891234'),
(5, 'Av. Arequipa 2020, Lince, Lima',                     'Distribuidora Nova Perú S.A.C.',    'gerencia@novaperu.com',          '923456781', 'Ricardo Salazar Núñez',  '20678912345'),
(6, 'Av. Grau 150, Barranco, Lima',                       'Clínica Santa Elena S.A.',          'sistemas@santaelena.pe',         '912345678', 'Patricia Gómez Aliaga',  '20123456789'),
(7, NULL,                                                 'Logística Express del Perú S.R.L.', 'operaciones@logexpress.pe',      NULL,        'Andrés Paredes Cano',    '20389123456'),
(8, 'Av. Brasil 3050, Magdalena del Mar, Lima',           'Educativa Futuro S.A.C.',           NULL,                             '901234567', NULL,                     '20745612389');

INSERT INTO `service_type` (`id`, `name`) VALUES
(1, 'Soporte técnico'),
(2, 'Mantenimiento'),
(3, 'Consultoría'),
(4, 'Capacitación'),
(5, 'Monitoreo');

INSERT INTO `support_service` (`id`, `name`, `unit`, `service_type_id`) VALUES
(1,  'Soporte remoto',                      'tickets',        1),
(2,  'Soporte presencial',                  'visitas',        1),
(3,  'Mantenimiento preventivo de equipos', 'visitas',        2),
(4,  'Mantenimiento correctivo',            'intervenciones', 2),
(5,  'Consultoría de infraestructura TI',   'horas',          3),
(6,  'Auditoría de seguridad informática',  'auditorías',     3),
(7,  'Capacitación a usuarios finales',     'sesiones',       4),
(8,  'Monitoreo de servidores',             'servidores',     5),
(9,  'Monitoreo de red',                    'dispositivos',   5),
(10, 'Copias de seguridad gestionadas',     'GB',             5);

INSERT INTO `plan_support_service` (`id`, `extra_cost`, `monthly_limit`, `plan_id`, `support_service_id`) VALUES
(1,  15.00,  10,   1, 1),
(2,  80.00,  1,    1, 3),
(3,  12.00,  25,   2, 1),
(4,  70.00,  2,    2, 3),
(5,  NULL,   NULL, 2, 9),
(6,  90.00,  4,    3, 2),
(7,  120.00, 3,    3, 4),
(8,  NULL,   NULL, 3, 8),
(9,  150.00, 10,   4, 5),
(10, 300.00, 2,    5, 6);

SET @head = '# CONTRATO DE PRESTACIÓN DE SERVICIOS DE SOPORTE

N.º {{numero}}

SERVIT y {{cliente}} firman este contrato el {{fecha_emision}}. {{cliente}}, RUC {{ruc}}{{representante_frase}}.{{contacto}}

SERVIT prestará el plan "{{plan}}" con los servicios, plazos y tarifas que se indican a continuación.

## 1) Servicios del plan.
{{servicios}}
';
SET @tail = '
## 2) Vigencia y tiempo de atención.
El servicio va del {{fecha_inicio}} al {{fecha_fin}}, por {{duracion}}. El tiempo máximo de atención del plan es de {{tiempo_atencion}} desde que se reporta el incidente.

## 3) Condiciones económicas y forma de pago.
{{cliente}} pagará a SERVIT {{costo_mensual}} al mes por el plan "{{plan}}". En {{duration_months}} meses, el total del periodo es {{total}}. El pago es por mes vencido, dentro de los 15 días de recibido el comprobante.{{monto_registrado}}

Las partes firman este contrato en la fecha indicada arriba.{{firmado}}';

INSERT INTO `template` (`id`, `content`, `name`, `updated_at`, `plan_id`) VALUES
(1, CONCAT(@head, 'Se acuerda la prestación de soporte técnico por {{duracion}} con un costo mensual de {{costo_mensual}} y un tiempo de respuesta máximo de {{tiempo_atencion}}.', @tail), 'Plantilla Plan Básico', '2026-01-10 09:15:00.000000', 1),
(2, CONCAT(@head, 'Se acuerda la prestación de soporte técnico por {{duracion}} con un costo mensual de {{costo_mensual}} y un tiempo de respuesta máximo de {{tiempo_atencion}}.', @tail), 'Plantilla Plan Estándar', '2026-01-10 09:30:00.000000', 2),
(3, CONCAT(@head, 'Se acuerda la prestación de soporte técnico prioritario por {{duracion}} con un costo mensual de {{costo_mensual}} y un tiempo de respuesta máximo de {{tiempo_atencion}}.', @tail), 'Plantilla Plan Premium', '2026-02-05 14:00:00.000000', 3),
(4, CONCAT(@head, 'Se acuerda la prestación de soporte integral por {{duracion}} con un costo mensual de {{costo_mensual}}, incluyendo monitoreo y consultoría.', @tail), 'Plantilla Plan Empresarial', '2026-02-20 11:45:00.000000', 4),
(5, CONCAT(@head, 'Se acuerda la prestación de soporte 24x7 por {{duracion}} con un costo mensual de {{costo_mensual}} y respuesta máxima de {{response_time_hours}} hora.', @tail), 'Plantilla Plan Corporativo 24x7', '2026-03-12 16:20:00.000000', 5),
(6, CONCAT(@head, 'Se acuerda un periodo de prueba de {{duracion}} con un costo mensual de {{costo_mensual}}.', @tail), 'Plantilla Plan Starter', '2026-04-01 08:00:00.000000', 6);

-- Estos hash no son la contraseña del ingreso. El ingreso no consulta esta tabla.
INSERT INTO `users` (`id`, `email`, `name`, `password_hash`, `role`) VALUES
(1, 'admin@empresa.pe',     'Administrador General', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa8mXq1vYk3LrTz0sQnP5bUcD2eHgW1i', 'ADMIN'),
(2, 'gerente@empresa.pe',   'Sofía Castillo Rojas',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER'),
(3, 'ventas1@empresa.pe',   'Diego Herrera Luna',    '$2a$10$Wk3pQz8Vb1nXc5Rt7YhLdeUo9mAs2FgJ4iKq6TvBw0ZxCyN1PjEHa', 'SALES'),
(4, 'ventas2@empresa.pe',   'Valeria Ochoa Prado',   '$2a$10$Hb5Tn2Qw9LxEr4MzUc7KsOeY1pVj8AfG3dNi6BtXk0RwCuZyS2mDq', 'SALES'),
(5, 'soporte@empresa.pe',   'Marco Vega Ibáñez',     '$2a$10$Pz7Lc3Ra5YmWe9NqTg1HuOk4XsBj2VdF8iAo6CtJn0KwEyUh3QxMb', 'SUPPORT'),
(6, 'legal@empresa.pe',     'Elena Barrios Tapia',   '$2a$10$Ju4Xv1Ck8TnRb6MqWz3LeOd9YsHg5PfA2iNo7BtKm0QwEyUr1ZxCa', 'LEGAL');

INSERT INTO `contract` (`id`, `amount`, `end_date`, `issue_date`, `signed_date`, `start_date`, `status`, `customer_id`, `plan_id`) VALUES
(1, 450.00,  '2026-07-01', '2025-12-20', '2025-12-28', '2026-01-01', 'SIGNED',    1, 1),
(2, 900.00,  '2027-02-01', '2026-01-15', '2026-01-25', '2026-02-01', 'SIGNED',    2, 2),
(3, 1500.00, '2027-03-01', '2026-02-10', '2026-02-20', '2026-03-01', 'SIGNED',    3, 3),
(4, 2800.00, '2028-04-01', '2026-03-05', '2026-03-18', '2026-04-01', 'SIGNED',    4, 4),
(5, 4500.00, '2029-06-01', '2026-05-10', NULL,         '2026-06-01', 'GENERATED', 5, 5),
(6, 250.00,  '2026-12-01', '2026-08-20', '2026-08-28', '2026-09-01', 'SIGNED',    6, 6),
(7, 900.00,  '2027-11-01', '2026-10-15', NULL,         '2026-11-01', 'GENERATED', 7, 2),
(8, 1500.00, '2027-12-01', '2026-10-28', NULL,         '2026-12-01', 'GENERATED', 8, 3);
