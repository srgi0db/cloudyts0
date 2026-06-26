USE db_appbiblioteca;

ALTER TABLE ventas
    MODIFY COLUMN estado VARCHAR(30) NOT NULL DEFAULT 'COMPLETADA';

UPDATE ventas
SET estado = 'COMPLETADA'
WHERE id > 0
  AND (
    estado IS NULL
    OR estado <> 'COMPLETADA'
  );

-- Agregar columnas solo si no existen, compatible con MySQL Workbench.

SET @db_name = DATABASE();

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'ventas'
              AND COLUMN_NAME = 'codigo_verificacion'
        ),
        'SELECT ''codigo_verificacion ya existe''',
        'ALTER TABLE ventas ADD COLUMN codigo_verificacion VARCHAR(40) NULL'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'ventas'
              AND COLUMN_NAME = 'codigo_verificacion'
              AND NON_UNIQUE = 0
        ),
        'SELECT ''codigo_verificacion ya tiene índice UNIQUE''',
        'ALTER TABLE ventas ADD UNIQUE KEY uk_ventas_codigo_verificacion (codigo_verificacion)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'ventas'
              AND COLUMN_NAME = 'punto_recojo_id'
        ),
        'SELECT ''punto_recojo_id ya existe''',
        'ALTER TABLE ventas ADD COLUMN punto_recojo_id VARCHAR(40) NULL'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'ventas'
              AND COLUMN_NAME = 'punto_recojo_nombre'
        ),
        'SELECT ''punto_recojo_nombre ya existe''',
        'ALTER TABLE ventas ADD COLUMN punto_recojo_nombre VARCHAR(160) NULL'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'ventas'
              AND COLUMN_NAME = 'metodo_pago'
        ),
        'SELECT ''metodo_pago ya existe''',
        'ALTER TABLE ventas ADD COLUMN metodo_pago VARCHAR(30) NULL'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;