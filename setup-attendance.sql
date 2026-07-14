-- ============================================================
-- SETUP ATTENDANCE - Libreria Machy SVM v4.0
-- Inserta registros de asistencia de ejemplo (última semana)
-- Ejecutar en NeonDB SQL Editor
-- ============================================================

-- Registros de asistencia para los usuarios existentes
-- Usa subqueries para obtener los IDs de auth_schema

-- Últimos 5 días hábiles de asistencia

-- Admin: entrada puntual, salida normal
INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'admin' LIMIT 1),
 'Administrador Machy',
 CURRENT_DATE - INTERVAL '4 days',
 '07:55:00', '17:05:00', 'completo', 9.17, 0, true, 'puntual', NOW() - INTERVAL '4 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'admin' LIMIT 1),
 'Administrador Machy',
 CURRENT_DATE - INTERVAL '3 days',
 '08:02:00', '17:10:00', 'completo', 9.13, 2, false, 'tardanza', NOW() - INTERVAL '3 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'admin' LIMIT 1),
 'Administrador Machy',
 CURRENT_DATE - INTERVAL '2 days',
 '07:50:00', '17:00:00', 'completo', 9.17, 0, true, 'puntual', NOW() - INTERVAL '2 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'admin' LIMIT 1),
 'Administrador Machy',
 CURRENT_DATE - INTERVAL '1 day',
 '07:58:00', '17:02:00', 'completo', 9.07, 0, true, 'puntual', NOW() - INTERVAL '1 day');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'admin' LIMIT 1),
 'Administrador Machy',
 CURRENT_DATE,
 '08:10:00', NULL, 'completo', 0, 10, false, 'tardanza', NOW());

-- Ana: vendedora, turno completo
INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'ana' LIMIT 1),
 'Ana Vendedora',
 CURRENT_DATE - INTERVAL '4 days',
 '08:00:00', '17:00:00', 'completo', 9.00, 0, true, 'puntual', NOW() - INTERVAL '4 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'ana' LIMIT 1),
 'Ana Vendedora',
 CURRENT_DATE - INTERVAL '3 days',
 '08:15:00', '17:10:00', 'completo', 8.92, 15, false, 'tardanza', NOW() - INTERVAL '3 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'ana' LIMIT 1),
 'Ana Vendedora',
 CURRENT_DATE - INTERVAL '2 days',
 '07:55:00', '16:55:00', 'completo', 9.00, 0, true, 'puntual', NOW() - INTERVAL '2 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'ana' LIMIT 1),
 'Ana Vendedora',
 CURRENT_DATE - INTERVAL '1 day',
 '07:58:00', '17:05:00', 'completo', 9.12, 0, true, 'puntual', NOW() - INTERVAL '1 day');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'ana' LIMIT 1),
 'Ana Vendedora',
 CURRENT_DATE,
 '08:05:00', NULL, 'completo', 0, 5, false, 'tardanza', NOW());

-- Miguel: vendedor, turno completo
INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'miguel' LIMIT 1),
 'Miguel Vendedor',
 CURRENT_DATE - INTERVAL '4 days',
 '08:30:00', '17:30:00', 'completo', 9.00, 30, false, 'tardanza', NOW() - INTERVAL '4 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'miguel' LIMIT 1),
 'Miguel Vendedor',
 CURRENT_DATE - INTERVAL '3 days',
 '07:50:00', '16:50:00', 'completo', 9.00, 0, true, 'puntual', NOW() - INTERVAL '3 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'miguel' LIMIT 1),
 'Miguel Vendedor',
 CURRENT_DATE - INTERVAL '2 days',
 '08:20:00', '17:15:00', 'completo', 8.92, 20, false, 'tardanza', NOW() - INTERVAL '2 days');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'miguel' LIMIT 1),
 'Miguel Vendedor',
 CURRENT_DATE - INTERVAL '1 day',
 '07:45:00', '17:00:00', 'completo', 9.25, 0, true, 'puntual', NOW() - INTERVAL '1 day');

INSERT INTO sale_schema.asistencia (id, usuario_id, nombre, fecha, hora_entrada, hora_salida, turno, horas, tardanza_min, cumple_turno, estado_asistencia, created_at)
VALUES
(gen_random_uuid(),
 (SELECT id FROM auth_schema.usuarios WHERE username = 'miguel' LIMIT 1),
 'Miguel Vendedor',
 CURRENT_DATE,
 '07:55:00', NULL, 'completo', 0, 0, false, 'puntual', NOW());
