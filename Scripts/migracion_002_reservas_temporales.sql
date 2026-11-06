-- Migracion 002: reservas temporales de asientos.
-- Ejecutar sobre una base que ya tenga las tablas ordenes_compra y asientos_funcion.
-- Cada paso va en su propia sentencia; si uno ya se aplico, omitelo y continua con el siguiente.

-- Paso 1
ALTER TABLE ordenes_compra
  ADD COLUMN expira_en DATETIME NULL AFTER pagado_en;

-- Paso 2
ALTER TABLE asientos_funcion
  ADD COLUMN id_orden_reserva BIGINT UNSIGNED NULL AFTER id_tipo_entrada;

-- Paso 3
ALTER TABLE asientos_funcion
  ADD KEY idx_asientos_orden_reserva (id_orden_reserva);

-- Paso 4
ALTER TABLE asientos_funcion
  ADD CONSTRAINT fk_asientos_orden_reserva
    FOREIGN KEY (id_orden_reserva) REFERENCES ordenes_compra (id_orden)
    ON DELETE RESTRICT;
