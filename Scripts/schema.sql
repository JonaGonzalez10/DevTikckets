

CREATE TABLE usuarios (
  id_usuario BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  correo_electronico VARCHAR(254) NOT NULL,
  hash_contrasena VARCHAR(255) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NOT NULL,
  telefono VARCHAR(30) NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY uq_usuarios_correo (correo_electronico)
);

CREATE TABLE roles (
  id_rol BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  PRIMARY KEY (id_rol),
  UNIQUE KEY uq_roles_nombre (nombre)
);

CREATE TABLE usuarios_roles (
  id_usuario BIGINT UNSIGNED NOT NULL,
  id_rol BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id_usuario, id_rol),
  KEY idx_usuarios_roles_rol (id_rol),
  CONSTRAINT fk_usuarios_roles_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
    ON DELETE CASCADE,
  CONSTRAINT fk_usuarios_roles_rol
    FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
    ON DELETE CASCADE
);

CREATE TABLE categorias (
  id_categoria BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  PRIMARY KEY (id_categoria),
  UNIQUE KEY uq_categorias_nombre (nombre)
);

CREATE TABLE recintos (
  id_recinto BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(150) NOT NULL,
  direccion VARCHAR(255) NULL,
  ciudad VARCHAR(100) NOT NULL,
  region VARCHAR(100) NULL,
  codigo_pais CHAR(2) NOT NULL,
  capacidad INT UNSIGNED NULL,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_recinto),
  KEY idx_recintos_ciudad (ciudad)
);

CREATE TABLE eventos (
  id_evento BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_categoria BIGINT UNSIGNED NULL,
  nombre VARCHAR(200) NOT NULL,
  descripcion TEXT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_evento),
  KEY idx_eventos_categoria_estado (id_categoria, estado),
  CONSTRAINT fk_eventos_categoria
    FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
    ON DELETE SET NULL
);

CREATE TABLE imagenes_evento (
  id_imagen BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_evento BIGINT UNSIGNED NOT NULL,
  url_imagen VARCHAR(2048) NOT NULL,
  texto_alternativo VARCHAR(255) NULL,
  orden_presentacion SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  es_principal TINYINT(1) NOT NULL DEFAULT 0,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_imagen),
  KEY idx_imagenes_evento_orden (id_evento, orden_presentacion),
  CONSTRAINT fk_imagenes_evento_evento
    FOREIGN KEY (id_evento) REFERENCES eventos (id_evento)
    ON DELETE CASCADE
);

CREATE TABLE funciones_evento (
  id_funcion BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_evento BIGINT UNSIGNED NOT NULL,
  id_recinto BIGINT UNSIGNED NOT NULL,
  fecha_inicio DATETIME NOT NULL,
  fecha_fin DATETIME NULL,
  inicio_venta DATETIME NULL,
  fin_venta DATETIME NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADO',
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_funcion),
  KEY idx_funciones_estado_fecha (estado, fecha_inicio),
  KEY idx_funciones_evento_fecha (id_evento, fecha_inicio),
  KEY idx_funciones_recinto_fecha (id_recinto, fecha_inicio),
  CONSTRAINT fk_funciones_evento
    FOREIGN KEY (id_evento) REFERENCES eventos (id_evento)
    ON DELETE RESTRICT,
  CONSTRAINT fk_funciones_recinto
    FOREIGN KEY (id_recinto) REFERENCES recintos (id_recinto)
    ON DELETE RESTRICT
);

CREATE TABLE tipos_entrada (
  id_tipo_entrada BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_funcion BIGINT UNSIGNED NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255) NULL,
  precio DECIMAL(10,2) NOT NULL,
  moneda CHAR(3) NOT NULL,
  cantidad_total INT UNSIGNED NOT NULL,
  cantidad_vendida INT UNSIGNED NOT NULL DEFAULT 0,
  inicio_venta DATETIME NULL,
  fin_venta DATETIME NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_tipo_entrada),
  UNIQUE KEY uq_tipos_entrada_funcion_nombre (id_funcion, nombre),
  KEY idx_tipos_entrada_funcion_estado (id_funcion, estado),
  CONSTRAINT fk_tipos_entrada_funcion
    FOREIGN KEY (id_funcion) REFERENCES funciones_evento (id_funcion)
    ON DELETE RESTRICT
);

CREATE TABLE ordenes_compra (
  id_orden BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_usuario BIGINT UNSIGNED NOT NULL,
  numero_orden VARCHAR(40) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  moneda CHAR(3) NOT NULL,
  monto_total DECIMAL(10,2) NOT NULL,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  pagado_en DATETIME NULL,
  PRIMARY KEY (id_orden),
  UNIQUE KEY uq_ordenes_numero (numero_orden),
  KEY idx_ordenes_usuario_fecha (id_usuario, creado_en),
  KEY idx_ordenes_estado_fecha (estado, creado_en),
  CONSTRAINT fk_ordenes_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
    ON DELETE RESTRICT
);

CREATE TABLE detalles_orden (
  id_detalle_orden BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_orden BIGINT UNSIGNED NOT NULL,
  id_tipo_entrada BIGINT UNSIGNED NOT NULL,
  cantidad SMALLINT UNSIGNED NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  total_linea DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id_detalle_orden),
  KEY idx_detalles_orden_orden (id_orden),
  KEY idx_detalles_orden_tipo_entrada (id_tipo_entrada),
  CONSTRAINT fk_detalles_orden_orden
    FOREIGN KEY (id_orden) REFERENCES ordenes_compra (id_orden)
    ON DELETE RESTRICT,
  CONSTRAINT fk_detalles_orden_tipo_entrada
    FOREIGN KEY (id_tipo_entrada) REFERENCES tipos_entrada (id_tipo_entrada)
    ON DELETE RESTRICT
);

CREATE TABLE entradas (
  id_entrada BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_detalle_orden BIGINT UNSIGNED NOT NULL,
  codigo_entrada VARCHAR(80) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'VALIDA',
  emitida_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usada_en DATETIME NULL,
  PRIMARY KEY (id_entrada),
  UNIQUE KEY uq_entradas_codigo (codigo_entrada),
  KEY idx_entradas_detalle_orden (id_detalle_orden),
  CONSTRAINT fk_entradas_detalle_orden
    FOREIGN KEY (id_detalle_orden) REFERENCES detalles_orden (id_detalle_orden)
    ON DELETE RESTRICT
);

CREATE TABLE pagos (
  id_pago BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_orden BIGINT UNSIGNED NOT NULL,
  proveedor VARCHAR(50) NOT NULL,
  referencia_proveedor VARCHAR(150) NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  monto DECIMAL(10,2) NOT NULL,
  moneda CHAR(3) NOT NULL,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  pagado_en DATETIME NULL,
  PRIMARY KEY (id_pago),
  UNIQUE KEY uq_pagos_proveedor_referencia (proveedor, referencia_proveedor),
  KEY idx_pagos_orden_fecha (id_orden, creado_en),
  CONSTRAINT fk_pagos_orden
    FOREIGN KEY (id_orden) REFERENCES ordenes_compra (id_orden)
    ON DELETE RESTRICT
);