-- Migracion 001: asientos por funcion.
-- Ejecutar una sola vez sobre la base de datos existente.

CREATE TABLE asientos_funcion (
  id_asiento BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_funcion BIGINT UNSIGNED NOT NULL,
  id_tipo_entrada BIGINT UNSIGNED NOT NULL,
  seccion VARCHAR(50) NOT NULL,
  fila VARCHAR(10) NOT NULL,
  numero SMALLINT UNSIGNED NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
  PRIMARY KEY (id_asiento),
  UNIQUE KEY uq_asientos_funcion_lugar (id_funcion, seccion, fila, numero),
  KEY idx_asientos_funcion_estado (id_funcion, estado),
  KEY idx_asientos_tipo_entrada (id_tipo_entrada),
  CONSTRAINT fk_asientos_funcion
    FOREIGN KEY (id_funcion) REFERENCES funciones_evento (id_funcion)
    ON DELETE RESTRICT,
  CONSTRAINT fk_asientos_tipo_entrada
    FOREIGN KEY (id_tipo_entrada) REFERENCES tipos_entrada (id_tipo_entrada)
    ON DELETE RESTRICT
);

ALTER TABLE entradas
  ADD COLUMN id_asiento BIGINT UNSIGNED NULL AFTER id_detalle_orden;

ALTER TABLE entradas
  ADD UNIQUE KEY uq_entradas_asiento (id_asiento);

ALTER TABLE entradas
  ADD CONSTRAINT fk_entradas_asiento
    FOREIGN KEY (id_asiento) REFERENCES asientos_funcion (id_asiento)
    ON DELETE RESTRICT;