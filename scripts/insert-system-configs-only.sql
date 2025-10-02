-- Script para insertar solo las configuraciones del sistema
-- Útil cuando solo necesitas resetear las configuraciones sin afectar otros datos
-- Limpiar configuraciones existentes
DELETE FROM system_configurations;

-- Reiniciar secuencia
ALTER SEQUENCE system_configurations_id_seq
RESTART WITH 1;

-- Insertar configuraciones del sistema
INSERT INTO
    system_configurations (
        id,
        config_key,
        config_value,
        data_type,
        description,
        is_active,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        'reservation.timeout.minutes',
        '60',
        'INT',
        'Tiempo en minutos para confirmar una reserva antes de que expire',
        true,
        NOW (),
        NOW ()
    ),
    (
        2,
        'reservation.penalty.days',
        '30',
        'INT',
        'Días de penalización por cancelación tardía',
        true,
        NOW (),
        NOW ()
    ),
    (
        3,
        'jwt.access.minutes',
        '30',
        'INT',
        'Duración del token de acceso en minutos',
        true,
        NOW (),
        NOW ()
    ),
    (
        4,
        'jwt.refresh.days',
        '7',
        'INT',
        'Duración del token de refresco en días',
        true,
        NOW (),
        NOW ()
    ),
    (
        5,
        'reservation.max.per.year',
        '3',
        'INT',
        'Máximo número de reservas por usuario por año',
        true,
        NOW (),
        NOW ()
    ),
    (
        6,
        'waitinglist.notification.hours',
        '24',
        'INT',
        'Horas de notificación para reclamar lugar en lista de espera',
        true,
        NOW (),
        NOW ()
    ),
    (
        7,
        'waitinglist.expire.delay-ms',
        '300000',
        'INT',
        'Intervalo en milisegundos para expirar notificaciones de lista de espera (5 minutos)',
        true,
        NOW (),
        NOW ()
    ),
    (
        8,
        'system.maintenance.mode',
        'false',
        'BOOLEAN',
        'Indica si el sistema está en modo mantenimiento',
        true,
        NOW (),
        NOW ()
    ),
    (
        9,
        'email.notifications.enabled',
        'true',
        'BOOLEAN',
        'Habilita las notificaciones por email',
        true,
        NOW (),
        NOW ()
    ),
    (
        10,
        'sms.notifications.enabled',
        'false',
        'BOOLEAN',
        'Habilita las notificaciones por SMS',
        true,
        NOW (),
        NOW ()
    );

-- Actualizar secuencia
SELECT
    setval (
        'system_configurations_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                system_configurations
        )
    );

-- Mostrar configuraciones insertadas
SELECT
    config_key,
    config_value,
    description
FROM
    system_configurations
ORDER BY
    id;