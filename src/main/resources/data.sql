-- Datos de desarrollo para Triquiñuelas. Cada inserción es idempotente.
INSERT INTO politicas_cancelacion (version, vigente_desde, porcentaje_no_show)
SELECT 'triquinuelas-v1', DATE '2026-01-01', 1.0000
WHERE NOT EXISTS (SELECT 1 FROM politicas_cancelacion WHERE version = 'triquinuelas-v1');

INSERT INTO politica_tramos (version, dias_antelacion_minimos, porcentaje_retencion)
SELECT 'triquinuelas-v1', 0, 0.5000
WHERE NOT EXISTS (SELECT 1 FROM politica_tramos WHERE version = 'triquinuelas-v1' AND dias_antelacion_minimos = 0);
INSERT INTO politica_tramos (version, dias_antelacion_minimos, porcentaje_retencion)
SELECT 'triquinuelas-v1', 3, 0.2500
WHERE NOT EXISTS (SELECT 1 FROM politica_tramos WHERE version = 'triquinuelas-v1' AND dias_antelacion_minimos = 3);
INSERT INTO politica_tramos (version, dias_antelacion_minimos, porcentaje_retencion)
SELECT 'triquinuelas-v1', 7, 0.1000
WHERE NOT EXISTS (SELECT 1 FROM politica_tramos WHERE version = 'triquinuelas-v1' AND dias_antelacion_minimos = 7);

INSERT INTO alojamientos (id, nombre, descripcion, ciudad, direccion, departamento, umbral_edad_facturable,
 hora_entrada, hora_salida, tiempo_preparacion_horas, plazo_confirmacion_horas, hora_limite_no_show,
 porcentaje_anticipo, politica_version, politica_vigente_desde)
SELECT '10000000-0000-0000-0000-000000000001', 'Triquiñuelas', 'Alojamiento rural del equipo SGA',
 'Salento', 'Vía al Valle de Cocora, km 2', 'Quindío', 5, TIME '15:00:00', TIME '11:00:00', 4, 24,
 TIME '18:00:00', 0.2500, 'triquinuelas-v1', DATE '2026-01-01'
WHERE NOT EXISTS (SELECT 1 FROM alojamientos WHERE id = '10000000-0000-0000-0000-000000000001');

INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'COLIBRI', 'Cabaña acogedora para parejas', 2, 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id = '20000000-0000-0000-0000-000000000001');
INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'BARRANQUERO', 'Cabaña familiar con balcón', 4, 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id = '20000000-0000-0000-0000-000000000002');
INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000001', 'TANGARA', 'Suite con vista a la montaña', 3, 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id = '20000000-0000-0000-0000-000000000003');
INSERT INTO apartamentos (id, alojamiento_id, identificador_unico, descripcion, capacidad, estado_operativo, disponible, activo)
SELECT '20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000001', 'TOROROI', 'Cabaña amplia para grupos', 6, 'PREPARADO', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM apartamentos WHERE id = '20000000-0000-0000-0000-000000000004');

INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas)
SELECT '20000000-0000-0000-0000-000000000001', 'Principal', 1, 'Doble'
WHERE NOT EXISTS (SELECT 1 FROM apartamento_dormitorios WHERE apartamento_id = '20000000-0000-0000-0000-000000000001');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas)
SELECT '20000000-0000-0000-0000-000000000002', 'Familiar', 2, 'Doble'
WHERE NOT EXISTS (SELECT 1 FROM apartamento_dormitorios WHERE apartamento_id = '20000000-0000-0000-0000-000000000002');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas)
SELECT '20000000-0000-0000-0000-000000000003', 'Suite', 1, 'Doble'
WHERE NOT EXISTS (SELECT 1 FROM apartamento_dormitorios WHERE apartamento_id = '20000000-0000-0000-0000-000000000003');
INSERT INTO apartamento_dormitorios (apartamento_id, nombre, cantidad_camas, tipo_camas)
SELECT '20000000-0000-0000-0000-000000000004', 'Grupo', 3, 'Doble'
WHERE NOT EXISTS (SELECT 1 FROM apartamento_dormitorios WHERE apartamento_id = '20000000-0000-0000-0000-000000000004');

INSERT INTO temporada (id, nombre, fecha_inicio, fecha_fin, multiplicador_tarifa, es_base, activa)
SELECT '30000000-0000-0000-0000-000000000001', 'Base', NULL, NULL, 1.00, TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM temporada WHERE id = '30000000-0000-0000-0000-000000000001');
INSERT INTO temporada (id, nombre, fecha_inicio, fecha_fin, multiplicador_tarifa, es_base, activa)
SELECT '30000000-0000-0000-0000-000000000002', 'Festival de la Piña', DATE '2027-06-01', DATE '2027-06-08', 1.20, FALSE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM temporada WHERE id = '30000000-0000-0000-0000-000000000002');

INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche)
SELECT '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 120000
WHERE NOT EXISTS (SELECT 1 FROM temporada_tarifas WHERE temporada_id = '30000000-0000-0000-0000-000000000001' AND apartamento_id = '20000000-0000-0000-0000-000000000001');
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche)
SELECT '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000002', 180000
WHERE NOT EXISTS (SELECT 1 FROM temporada_tarifas WHERE temporada_id = '30000000-0000-0000-0000-000000000001' AND apartamento_id = '20000000-0000-0000-0000-000000000002');
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche)
SELECT '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000003', 155000
WHERE NOT EXISTS (SELECT 1 FROM temporada_tarifas WHERE temporada_id = '30000000-0000-0000-0000-000000000001' AND apartamento_id = '20000000-0000-0000-0000-000000000003');
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche)
SELECT '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000004', 240000
WHERE NOT EXISTS (SELECT 1 FROM temporada_tarifas WHERE temporada_id = '30000000-0000-0000-0000-000000000001' AND apartamento_id = '20000000-0000-0000-0000-000000000004');
INSERT INTO temporada_tarifas (temporada_id, apartamento_id, precio_por_noche)
SELECT '30000000-0000-0000-0000-000000000002', apartamento_id, precio_por_noche * 1.20 FROM temporada_tarifas
WHERE temporada_id = '30000000-0000-0000-0000-000000000001'
AND NOT EXISTS (SELECT 1 FROM temporada_tarifas t WHERE t.temporada_id = '30000000-0000-0000-0000-000000000002' AND t.apartamento_id = temporada_tarifas.apartamento_id);
