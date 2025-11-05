-- Script para insertar datos de prueba en la base de datos
-- Este script crea usuarios, cabañas, configuraciones del sistema y otros datos necesarios para testing
-- ========================================
-- 1. INSERTAR USUARIOS
-- ========================================
-- Usuario Administrador
INSERT INTO
    users (
        id,
        email,
        identification_number,
        name,
        phone,
        pin_hash,
        role,
        active,
        must_change_password,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        'admin@cooperativa.com',
        '12345678',
        'Administrador Sistema',
        '+57-300-123-4567',
        '$2b$10$9RH7z1ZGp1zArp/h.o3atOlwQQhZr06Wptekhu4t7F5Cp1Ty7dCju',
        'ADMIN',
        true,
        false,
        NOW (),
        NOW ()
    );

-- Usuarios Profesores
INSERT INTO
    users (
        id,
        email,
        identification_number,
        name,
        phone,
        pin_hash,
        role,
        active,
        must_change_password,
        created_at,
        updated_at
    )
VALUES
    (
        2,
        'profesor1@universidad.edu.co',
        '87654321',
        'María González',
        '+57-310-234-5678',
        '$2b$10$UIhhwyiC6zJHDG1Wyc/7oOBYyEMXNy8csIKNFyRxG.nckU6pJuiB2',
        'PROFESSOR',
        true,
        false,
        NOW (),
        NOW ()
    ),
    (
        3,
        'profesor2@universidad.edu.co',
        '11223344',
        'Carlos Rodríguez',
        '+57-315-345-6789',
        '$2b$10$UIhhwyiC6zJHDG1Wyc/7oOBYyEMXNy8csIKNFyRxG.nckU6pJuiB2',
        'PROFESSOR',
        true,
        false,
        NOW (),
        NOW ()
    ),
    (
        4,
        'profesor3@universidad.edu.co',
        '55667788',
        'Ana Martínez',
        '+57-320-456-7890',
        '$2b$10$UIhhwyiC6zJHDG1Wyc/7oOBYyEMXNy8csIKNFyRxG.nckU6pJuiB2',
        'PROFESSOR',
        true,
        false,
        NOW (),
        NOW ()
    );

-- Usuarios Pensionados
INSERT INTO
    users (
        id,
        email,
        identification_number,
        name,
        phone,
        pin_hash,
        role,
        active,
        must_change_password,
        created_at,
        updated_at
    )
VALUES
    (
        5,
        'pensionado1@email.com',
        '99887766',
        'Roberto Silva',
        '+57-325-567-8901',
        '$2b$10$UIhhwyiC6zJHDG1Wyc/7oOBYyEMXNy8csIKNFyRxG.nckU6pJuiB2',
        'RETIREE',
        true,
        false,
        NOW (),
        NOW ()
    ),
    (
        6,
        'pensionado2@email.com',
        '44332211',
        'Carmen López',
        '+57-330-678-9012',
        '$2b$10$UIhhwyiC6zJHDG1Wyc/7oOBYyEMXNy8csIKNFyRxG.nckU6pJuiB2',
        'RETIREE',
        true,
        false,
        NOW (),
        NOW ()
    );

-- ========================================
-- 2. INSERTAR NÚMEROS DE DOCUMENTO
-- ========================================
INSERT INTO
    document_numbers (
        id,
        document_number,
        status,
        created_at,
        updated_at
    )
VALUES
    (1, '12345678', 'ACTIVE', NOW (), NOW ()),
    (2, '87654321', 'ACTIVE', NOW (), NOW ()),
    (3, '11223344', 'ACTIVE', NOW (), NOW ()),
    (4, '55667788', 'ACTIVE', NOW (), NOW ()),
    (5, '99887766', 'ACTIVE', NOW (), NOW ()),
    (6, '44332211', 'ACTIVE', NOW (), NOW ()),
    (7, '99999999', 'ACTIVE', NOW (), NOW ());

-- ========================================
-- 3. INSERTAR CABINAS
-- ========================================
INSERT INTO
    cabins (
        id,
        name,
        description,
        capacity,
        bedrooms,
        bathrooms,
        base_price,
        max_guests,
        is_active,
        amenities,
        location,
        default_check_in_time,
        default_check_out_time,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        'Cabaña del Bosque',
        'Hermosa cabaña rodeada de árboles nativos, ideal para descanso y contacto con la naturaleza.',
        6,
        3,
        2,
        150000.00,
        6,
        true,
        '["WiFi", "Chimenea", "Cocina completa", "Terraza", "Parrilla", "Estacionamiento"]',
        '{"address": "Vía Principal Km 5", "coordinates": {"lat": 4.6097, "lng": -74.0817}, "zone": "Bosque"}',
        '15:00:00',
        '11:00:00',
        NOW (),
        NOW ()
    ),
    (
        2,
        'Cabaña del Lago',
        'Cabaña con vista al lago, perfecta para pescar y disfrutar del paisaje acuático.',
        4,
        2,
        1,
        120000.00,
        4,
        true,
        '["WiFi", "Vista al lago", "Cocina básica", "Balcón", "Embarcadero privado"]',
        '{"address": "Vía del Lago Km 3", "coordinates": {"lat": 4.6100, "lng": -74.0820}, "zone": "Lago"}',
        '15:00:00',
        '11:00:00',
        NOW (),
        NOW ()
    ),
    (
        3,
        'Cabaña Familiar',
        'Cabaña amplia y cómoda, diseñada para familias grandes con todas las comodidades.',
        8,
        4,
        3,
        200000.00,
        8,
        true,
        '["WiFi", "Aire acondicionado", "Cocina completa", "Sala de juegos", "Parrilla", "Jardín"]',
        '{"address": "Vía Familiar Km 2", "coordinates": {"lat": 4.6080, "lng": -74.0800}, "zone": "Familiar"}',
        '15:00:00',
        '11:00:00',
        NOW (),
        NOW ()
    ),
    (
        4,
        'Cabaña Romántica',
        'Pequeña y acogedora cabaña ideal para parejas, con ambiente íntimo y romántico.',
        2,
        1,
        1,
        80000.00,
        2,
        true,
        '["WiFi", "Chimenea", "Jacuzzi", "Desayuno incluido", "Terraza privada"]',
        '{"address": "Vía Romántica Km 1", "coordinates": {"lat": 4.6070, "lng": -74.0790}, "zone": "Romántica"}',
        '15:00:00',
        '11:00:00',
        NOW (),
        NOW ()
    );

-- ========================================
-- 4. INSERTAR CONFIGURACIONES DEL SISTEMA
-- ========================================
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
        'system.maintenance.mode',
        'false',
        'BOOLEAN',
        'Indica si el sistema está en modo mantenimiento',
        true,
        NOW (),
        NOW ()
    ),
    (
        8,
        'email.notifications.enabled',
        'true',
        'BOOLEAN',
        'Habilita las notificaciones por email',
        true,
        NOW (),
        NOW ()
    );

-- ========================================
-- 5. INSERTAR RANGOS DE PRECIOS ESPECIALES
-- ========================================
-- Temporada alta para Cabaña del Bosque (Diciembre - Enero)
INSERT INTO
    price_ranges (
        id,
        cabin_id,
        start_date,
        end_date,
        base_price,
        price_multiplier,
        reason,
        created_by,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        1,
        '2024-12-01',
        '2025-01-31',
        150000.00,
        1.5,
        'Temporada alta - Vacaciones de fin de año',
        1,
        NOW (),
        NOW ()
    );

-- Temporada alta para Cabaña del Lago (Junio - Agosto)
INSERT INTO
    price_ranges (
        id,
        cabin_id,
        start_date,
        end_date,
        base_price,
        price_multiplier,
        reason,
        created_by,
        created_at,
        updated_at
    )
VALUES
    (
        2,
        2,
        '2024-06-01',
        '2024-08-31',
        120000.00,
        1.3,
        'Temporada alta - Vacaciones de mitad de año',
        1,
        NOW (),
        NOW ()
    );

-- Fin de semana especial para Cabaña Romántica
INSERT INTO
    price_ranges (
        id,
        cabin_id,
        start_date,
        end_date,
        base_price,
        price_multiplier,
        reason,
        created_by,
        created_at,
        updated_at
    )
VALUES
    (
        3,
        4,
        '2024-02-10',
        '2024-02-17',
        80000.00,
        2.0,
        'Semana del amor y la amistad',
        1,
        NOW (),
        NOW ()
    );

-- ========================================
-- 6. INSERTAR BLOQUEOS DE DISPONIBILIDAD
-- ========================================
-- Bloqueo obligatorio mínimo para Cabaña del Bosque (fin de semana largo)
-- Los usuarios deben reservar mínimo todo el fin de semana, no pueden reservar solo el viernes o solo el sábado
INSERT INTO
    availability_blocks (
        id,
        cabin_id,
        start_date,
        end_date,
        reason,
        created_by,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        1,
        '2024-03-15',
        '2024-03-17',
        'Fin de semana largo - reserva mínima de 3 días',
        1,
        NOW (),
        NOW ()
    );

-- Bloqueo obligatorio mínimo para Cabaña del Lago (puente festivo)
-- Los usuarios deben reservar mínimo todo el puente, no pueden reservar días sueltos
INSERT INTO
    availability_blocks (
        id,
        cabin_id,
        start_date,
        end_date,
        reason,
        created_by,
        created_at,
        updated_at
    )
VALUES
    (
        2,
        2,
        '2024-04-01',
        '2024-04-03',
        'Puente festivo - reserva mínima de 3 días',
        1,
        NOW (),
        NOW ()
    );

-- Bloqueo obligatorio mínimo para Cabaña Familiar (temporada alta)
-- Los usuarios deben reservar mínimo una semana completa en temporada alta
INSERT INTO
    availability_blocks (
        id,
        cabin_id,
        start_date,
        end_date,
        reason,
        created_by,
        created_at,
        updated_at
    )
VALUES
    (
        3,
        3,
        '2024-06-15',
        '2024-06-22',
        'Temporada alta - reserva mínima de 7 días',
        1,
        NOW (),
        NOW ()
    );

-- ========================================
-- 7. INSERTAR RESERVAS DE EJEMPLO
-- ========================================
-- Reserva confirmada próxima
INSERT INTO
    reservations (
        id,
        user_id,
        cabin_id,
        start_date,
        end_date,
        check_in_time,
        check_out_time,
        number_of_guests,
        status,
        base_price,
        final_price,
        created_at,
        confirmed_at
    )
VALUES
    (
        1,
        2,
        1,
        '2024-02-15',
        '2024-02-18',
        '15:00:00',
        '11:00:00',
        4,
        'CONFIRMED',
        150000.00,
        150000.00,
        NOW (),
        NOW ()
    );

-- Reserva en uso
INSERT INTO
    reservations (
        id,
        user_id,
        cabin_id,
        start_date,
        end_date,
        check_in_time,
        check_out_time,
        number_of_guests,
        status,
        base_price,
        final_price,
        created_at,
        confirmed_at
    )
VALUES
    (
        2,
        3,
        2,
        '2024-01-20',
        '2024-01-23',
        '15:00:00',
        '11:00:00',
        2,
        'IN_USE',
        120000.00,
        120000.00,
        NOW (),
        NOW ()
    );

-- Reserva completada
INSERT INTO
    reservations (
        id,
        user_id,
        cabin_id,
        start_date,
        end_date,
        check_in_time,
        check_out_time,
        number_of_guests,
        status,
        base_price,
        final_price,
        created_at,
        confirmed_at
    )
VALUES
    (
        3,
        4,
        3,
        '2023-12-25',
        '2023-12-30',
        '15:00:00',
        '11:00:00',
        6,
        'COMPLETED',
        200000.00,
        300000.00,
        NOW (),
        NOW ()
    );

-- ========================================
-- 8. INSERTAR LISTA DE ESPERA
-- ========================================
-- Usuarios en lista de espera para fechas específicas
INSERT INTO
    waiting_list (
        id,
        user_id,
        cabin_id,
        requested_start_date,
        requested_end_date,
        number_of_guests,
        position,
        status,
        created_at
    )
VALUES
    (
        1,
        5,
        1,
        '2024-02-20',
        '2024-02-25',
        3,
        1,
        'PENDING',
        NOW ()
    ),
    (
        2,
        6,
        1,
        '2024-02-20',
        '2024-02-25',
        2,
        2,
        'PENDING',
        NOW ()
    ),
    (
        3,
        2,
        3,
        '2024-03-10',
        '2024-03-15',
        5,
        1,
        'NOTIFIED',
        NOW ()
    );

-- ========================================
-- 9. INSERTAR LOGS DE AUDITORÍA DE EJEMPLO
-- ========================================
INSERT INTO
    audit_logs (
        id,
        action,
        entity_type,
        entity_id,
        old_values,
        new_values,
        ip_address,
        user_id,
        created_at,
        updated_at
    )
VALUES
    (
        1,
        'CREATE',
        'User',
        2,
        null,
        '{"name":"María González","email":"profesor1@universidad.edu.co"}',
        '192.168.1.100',
        1,
        NOW (),
        NOW ()
    ),
    (
        2,
        'UPDATE',
        'SystemConfiguration',
        1,
        '{"value":"30"}',
        '{"value":"60"}',
        '192.168.1.101',
        1,
        NOW (),
        NOW ()
    ),
    (
        3,
        'CREATE',
        'Reservation',
        1,
        null,
        '{"startDate":"2024-02-15","endDate":"2024-02-18"}',
        '192.168.1.102',
        2,
        NOW (),
        NOW ()
    );

-- ========================================
-- ACTUALIZAR SECUENCIAS
-- ========================================
-- Actualizar las secuencias para que los próximos IDs sean correctos
SELECT
    setval (
        'users_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                users
        )
    );

SELECT
    setval (
        'document_numbers_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                document_numbers
        )
    );

SELECT
    setval (
        'cabins_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                cabins
        )
    );

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

SELECT
    setval (
        'price_ranges_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                price_ranges
        )
    );

SELECT
    setval (
        'availability_blocks_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                availability_blocks
        )
    );

SELECT
    setval (
        'reservations_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                reservations
        )
    );

SELECT
    setval (
        'waiting_list_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                waiting_list
        )
    );

SELECT
    setval (
        'audit_logs_id_seq',
        (
            SELECT
                MAX(id)
            FROM
                audit_logs
        )
    );

-- ========================================
-- MENSAJE DE CONFIRMACIÓN
-- ========================================
SELECT
    'Datos de prueba insertados exitosamente!' as status,
    (
        SELECT
            COUNT(*)
        FROM
            users
    ) as total_users,
    (
        SELECT
            COUNT(*)
        FROM
            cabins
    ) as total_cabins,
    (
        SELECT
            COUNT(*)
        FROM
            reservations
    ) as total_reservations,
    (
        SELECT
            COUNT(*)
        FROM
            waiting_list
    ) as total_waiting_list;