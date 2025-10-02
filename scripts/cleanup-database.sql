-- Script para limpiar todas las tablas de la base de datos
-- ⚠️  ADVERTENCIA: Este script eliminará TODOS los datos de la base de datos
-- Ejecutar solo en entornos de desarrollo/testing
-- Deshabilitar temporalmente las restricciones de clave foránea
SET
    session_replication_role = replica;

-- Eliminar datos de todas las tablas (en orden inverso de dependencias)
TRUNCATE TABLE price_change_history CASCADE;

TRUNCATE TABLE waiting_list CASCADE;

TRUNCATE TABLE password_reset_tokens CASCADE;

TRUNCATE TABLE audit_logs CASCADE;

TRUNCATE TABLE system_configurations CASCADE;

TRUNCATE TABLE availability_blocks CASCADE;

TRUNCATE TABLE price_ranges CASCADE;

TRUNCATE TABLE reservations CASCADE;

TRUNCATE TABLE document_numbers CASCADE;

TRUNCATE TABLE cabins CASCADE;

TRUNCATE TABLE users CASCADE;

-- Reiniciar las secuencias de auto-incremento
ALTER SEQUENCE IF EXISTS users_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS cabins_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS document_numbers_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS reservations_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS price_ranges_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS availability_blocks_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS system_configurations_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS audit_logs_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS password_reset_tokens_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS waiting_list_id_seq
RESTART WITH 1;

ALTER SEQUENCE IF EXISTS price_change_history_id_seq
RESTART WITH 1;

-- Rehabilitar las restricciones de clave foránea
SET
    session_replication_role = DEFAULT;

-- Mensaje de confirmación
SELECT
    'Base de datos limpiada exitosamente. Todas las tablas están vacías.' as status;