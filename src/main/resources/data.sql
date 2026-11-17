-- Datos de desarrollo del alojamiento El Cam, Lloró, Chocó. Idempotentes.
ALTER TABLE Reserva ALTER COLUMN titular_usuario_id DROP NOT NULL;
ALTER TABLE Bloqueo ALTER COLUMN apartamento_id VARCHAR(36);
INSERT INTO politicas_cancelacion (version, vigente_desde, porcentaje_no_show)
SELECT 'el-cam-v1', DATE '2026-01-01', 1.0000
WHERE NOT EXISTS (SELECT 1 FROM politicas_cancelacion WHERE version = 'el-cam-v1');

INSERT INTO politica_tramos (version, dias_antelacion_minimos, porcentaje_retencion)
SELECT 'el-cam-v1', 0, 0.2500
WHERE NOT EXISTS (SELECT 1 FROM politica_tramos WHERE version = 'el-cam-v1' AND dias_antelacion_minimos = 0);
-- Migra los umbrales de la política de desarrollo de días a horas y corrige el anticipo (25%).
UPDATE politica_tramos SET dias_antelacion_minimos=24, porcentaje_retencion=0.2500
WHERE version='el-cam-v1' AND dias_antelacion_minimos=1 AND porcentaje_retencion=0.0625;
UPDATE politica_tramos SET dias_antelacion_minimos=72, porcentaje_retencion=0.1000
WHERE version='el-cam-v1' AND dias_antelacion_minimos=4 AND porcentaje_retencion=0.1000;
INSERT INTO politica_tramos (version, dias_antelacion_minimos, porcentaje_retencion)
SELECT 'el-cam-v1', 24, 0.2500
WHERE NOT EXISTS (SELECT 1 FROM politica_tramos WHERE version = 'el-cam-v1' AND dias_antelacion_minimos = 24);
INSERT INTO politica_tramos (version, dias_antelacion_minimos, porcentaje_retencion)
SELECT 'el-cam-v1', 72, 0.1000
WHERE NOT EXISTS (SELECT 1 FROM politica_tramos WHERE version = 'el-cam-v1' AND dias_antelacion_minimos = 72);
UPDATE reserva_tramos_cancelacion SET dias_antelacion_minimos=24, porcentaje_retencion=0.2500
WHERE dias_antelacion_minimos=1 AND porcentaje_retencion=0.0625;
UPDATE reserva_tramos_cancelacion SET dias_antelacion_minimos=72
WHERE dias_antelacion_minimos=4 AND porcentaje_retencion=0.1000;

INSERT INTO alojamientos (id, nombre, descripcion, ciudad, direccion, departamento, umbral_edad_facturable,
 hora_entrada, hora_salida, tiempo_preparacion_horas, plazo_confirmacion_horas, hora_limite_no_show,
 porcentaje_anticipo, politica_version, politica_vigente_desde)
SELECT '10000000-0000-0000-0000-000000000001', 'El Cam', 'Alojamiento en Lloró, Chocó',
 'Lloró', 'Lloró, Chocó, Colombia', 'Chocó', 5, TIME '15:00:00', TIME '15:00:00', 3, 4,
 TIME '18:00:00', 0.2500, 'el-cam-v1', DATE '2026-01-01'
WHERE NOT EXISTS (SELECT 1 FROM alojamientos WHERE id = '10000000-0000-0000-0000-000000000001');
UPDATE alojamientos SET nombre='El Cam', descripcion='Alojamiento en Lloró, Chocó', ciudad='Lloró',
 direccion='Lloró, Chocó, Colombia', departamento='Chocó', umbral_edad_facturable=5,
 hora_entrada=TIME '15:00:00', hora_salida=TIME '15:00:00', tiempo_preparacion_horas=3,
 plazo_confirmacion_horas=4, hora_limite_no_show=TIME '18:00:00', porcentaje_anticipo=0.2500
WHERE id='10000000-0000-0000-0000-000000000001';
UPDATE alojamientos SET politica_version='el-cam-v1', politica_vigente_desde=DATE '2026-01-01'
WHERE id='10000000-0000-0000-0000-000000000001' AND (politica_version IS NULL OR politica_version='triquinuelas-v1');

INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, categoria, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '101', 'Apartamento Standard', 3, 'STANDARD', 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id='20000000-0000-0000-0000-000000000001');
INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, categoria, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', '102', 'Apartamento Standard', 3, 'STANDARD', 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id='20000000-0000-0000-0000-000000000002');
INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, categoria, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000001', '201', 'Apartamento Premium', 6, 'PREMIUM', 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id='20000000-0000-0000-0000-000000000003');
INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, categoria, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000001', '202', 'Apartamento Premium', 6, 'PREMIUM', 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id='20000000-0000-0000-0000-000000000004');
UPDATE apartamentos SET identificador_unico='101', descripcion='Apartamento Standard', capacidad=3, categoria='STANDARD' WHERE id='20000000-0000-0000-0000-000000000001';
UPDATE apartamentos SET identificador_unico='102', descripcion='Apartamento Standard', capacidad=3, categoria='STANDARD' WHERE id='20000000-0000-0000-0000-000000000002';
UPDATE apartamentos SET identificador_unico='201', descripcion='Apartamento Premium', capacidad=6, categoria='PREMIUM' WHERE id='20000000-0000-0000-0000-000000000003';
UPDATE apartamentos SET identificador_unico='202', descripcion='Apartamento Premium', capacidad=6, categoria='PREMIUM' WHERE id='20000000-0000-0000-0000-000000000004';

DELETE FROM apartamento_dormitorios WHERE apartamento_id IN (
 '20000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000002',
 '20000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000004');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000001','Dormitorio 1',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000001','Dormitorio 2',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000002','Dormitorio 1',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000002','Dormitorio 2',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000003','Dormitorio 1',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000003','Dormitorio 2',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000003','Dormitorio 3',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000004','Dormitorio 1',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000004','Dormitorio 2',1,'Doble');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas) VALUES ('20000000-0000-0000-0000-000000000004','Dormitorio 3',1,'Doble');

INSERT INTO temporada (id, nombre, fecha_inicio, fecha_fin, multiplicador_tarifa, es_base, activa)
SELECT '30000000-0000-0000-0000-000000000001', 'Temporada Base', NULL, NULL, 1.00, TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM temporada WHERE id='30000000-0000-0000-0000-000000000001');
UPDATE temporada SET nombre='Temporada Base', fecha_inicio=NULL, fecha_fin=NULL, multiplicador_tarifa=1.00, es_base=TRUE, activa=TRUE WHERE id='30000000-0000-0000-0000-000000000001';
INSERT INTO temporada (id, nombre, fecha_inicio, fecha_fin, multiplicador_tarifa, es_base, activa)
SELECT '30000000-0000-0000-0000-000000000002', 'Fiestas Patronales', DATE '2027-07-10', DATE '2027-07-21', 1.00, FALSE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM temporada WHERE id='30000000-0000-0000-0000-000000000002');
UPDATE temporada SET nombre='Fiestas Patronales', fecha_inicio=DATE '2027-07-10', fecha_fin=DATE '2027-07-21', multiplicador_tarifa=1.00, es_base=FALSE, activa=TRUE WHERE id='30000000-0000-0000-0000-000000000002';
INSERT INTO temporada (id, nombre, fecha_inicio, fecha_fin, multiplicador_tarifa, es_base, activa)
SELECT '30000000-0000-0000-0000-000000000003', 'Festival de la Piña 2026-2027', DATE '2026-12-15', DATE '2027-01-16', 1.00, FALSE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM temporada WHERE id='30000000-0000-0000-0000-000000000003');
INSERT INTO temporada (id, nombre, fecha_inicio, fecha_fin, multiplicador_tarifa, es_base, activa)
SELECT '30000000-0000-0000-0000-000000000004', 'Festival de la Piña 2027-2028', DATE '2027-12-15', DATE '2028-01-16', 1.00, FALSE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM temporada WHERE id='30000000-0000-0000-0000-000000000004');

-- Los valores de tarifa son por apartamento (Standard) o por persona facturable (Premium).
DELETE FROM temporada_tarifas WHERE temporada_id IN (
 '30000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000002',
 '30000000-0000-0000-0000-000000000003','30000000-0000-0000-0000-000000000004');
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000001',80000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000002',80000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000003',150000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000004',150000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000001',120000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000002',120000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000003',220000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000004',220000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000001',140000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000002',140000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000003',250000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000004',250000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000001',140000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000002',140000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000003',250000);
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche) VALUES ('30000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000004',250000);
