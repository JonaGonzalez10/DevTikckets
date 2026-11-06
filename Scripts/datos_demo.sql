-- Datos demo generados desde DatosDemo/eventos_demo.csv
-- Ejecutar en una sola sesion despues de migracion_001_asientos.sql.
-- Cada funcion tiene VIP (filas A-B, 20 asientos, precio_max) y GENERAL (filas C-F, 40 asientos, precio_min).
-- Las fechas del CSV son hora de Mexico (UTC-6) y se guardan en UTC.

INSERT INTO categorias (nombre) VALUES
  ('Concierto'),
  ('Exposición'),
  ('Teatro'),
  ('Danza'),
  ('Comedia'),
  ('Conferencia'),
  ('Deportes'),
  ('Festival'),
  ('Cultura Pop');

INSERT INTO recintos (nombre, direccion, ciudad, region, codigo_pais, capacidad) VALUES
  ('Estadio GNP Seguros', 'Estadio GNP Seguros', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Palacio de los Deportes', 'Palacio de los Deportes', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Auditorio Nacional', 'Auditorio Nacional', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Arena CDMX', 'Arena CDMX', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Pepsi Center', 'Pepsi Center', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Auditorio Telmex', 'Auditorio Telmex', 'Zapopan', 'Jalisco', 'MX', 60),
  ('Arena VFG', 'Arena VFG', 'Tlajomulco de Zúñiga', 'Jalisco', 'MX', 60),
  ('Arena Monterrey', 'Arena Monterrey', 'Monterrey', 'Nuevo León', 'MX', 60),
  ('Estadio BBVA', 'Estadio BBVA', 'Guadalupe', 'Nuevo León', 'MX', 60),
  ('Auditorio GNP Seguros', 'Auditorio GNP Seguros', 'Puebla', 'Puebla', 'MX', 60),
  ('Museo del Desierto', 'Museo del Desierto', 'Saltillo', 'Coahuila', 'MX', 60),
  ('Museo Franz Mayer', 'Museo Franz Mayer', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Teatro de la Ciudad Esperanza Iris', 'Teatro de la Ciudad Esperanza Iris', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Palacio de Bellas Artes', 'Palacio de Bellas Artes', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Auditorio Citibanamex', 'Auditorio Citibanamex', 'Monterrey', 'Nuevo León', 'MX', 60),
  ('Centro Citibanamex', 'Centro Citibanamex', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Expo Guadalajara', 'Expo Guadalajara', 'Guadalajara', 'Jalisco', 'MX', 60),
  ('Arena México', 'Arena México', 'Ciudad de México', 'Ciudad de México', 'MX', 60),
  ('Parque Fundidora', 'Parque Fundidora', 'Monterrey', 'Nuevo León', 'MX', 60),
  ('WTC Ciudad de México', 'WTC Ciudad de México', 'Ciudad de México', 'Ciudad de México', 'MX', 60);

INSERT INTO eventos (id_categoria, nombre, descripcion, estado) VALUES
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Ecos de Neón', 'Lía Vértice | Pop Electrónico | Estadio GNP Seguros, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Horizonte Cero', 'Nómada Solar | Rock Alternativo | Palacio de los Deportes, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Noches de Mercurio', 'Bruno Lumen | Indie Pop | Auditorio Nacional, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Frecuencia 404', 'Kira Byte | Electrónica | Arena CDMX, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Ciudad de Cristal', 'Prisma Norte | Synth-pop | Pepsi Center, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Ritmos del Fuego', 'Alma Ríos | Pop Latino | Auditorio Telmex, Zapopan', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Volver al Sol', 'Atlas del Sur | Rock en Español | Arena VFG, Tlajomulco de Zúñiga', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Código Norte', 'Los Circuitos | Rock / Pop | Arena Monterrey, Monterrey', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Horizonte Regio', 'Mateo Fénix | Música Urbana | Estadio BBVA, Guadalupe', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Concierto'), 'Luz de Invierno', 'Valeria Nox | Balada / Pop | Auditorio GNP Seguros, Puebla', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Exposición'), 'Dinosaurios de México', 'Museo del Desierto | Ciencia | Museo del Desierto, Saltillo', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Exposición'), 'Luz Eterna', 'Experiencia Inmersiva | Arte | Museo Franz Mayer, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Teatro'), 'La Última Función', 'Compañía Luna Roja | Teatro de la Ciudad Esperanza Iris, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Danza'), 'El Lago Etéreo', 'Ballet Contemporáneo | Ballet | Palacio de Bellas Artes, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Comedia'), 'Risas de Media Noche', 'Diego Torres | Stand Up | Auditorio Citibanamex, Monterrey', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Conferencia'), 'Futuro Real', 'Conferencia Internacional | Tecnología | Centro Citibanamex, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Deportes'), 'México Esports Cup', 'Organización MX | Esports | Expo Guadalajara, Guadalajara', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Deportes'), 'Noche de Leyendas', 'Liga Mexicana de Lucha | Lucha Libre | Arena México, Ciudad de México', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Festival'), 'Sabores de México', 'Festival Gastronómico | Gastronomía | Parque Fundidora, Monterrey', 'PUBLICADO'),
  ((SELECT id_categoria FROM categorias WHERE nombre = 'Cultura Pop'), 'Animeverse México', 'Convención Anime | Anime / Cómic | WTC Ciudad de México, Ciudad de México', 'PUBLICADO');

INSERT INTO imagenes_evento (id_evento, url_imagen, texto_alternativo, orden_presentacion, es_principal) VALUES
  ((SELECT id_evento FROM eventos WHERE nombre = 'Ecos de Neón'), 'assets/eventos/01_ecos_de_neón.png', 'Ecos de Neón', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Horizonte Cero'), 'assets/eventos/02_horizonte_cero.png', 'Horizonte Cero', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Noches de Mercurio'), 'assets/eventos/03_noches_de_mercurio.png', 'Noches de Mercurio', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Frecuencia 404'), 'assets/eventos/04_frecuencia_404.png', 'Frecuencia 404', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Ciudad de Cristal'), 'assets/eventos/05_ciudad_de_cristal.png', 'Ciudad de Cristal', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Ritmos del Fuego'), 'assets/eventos/06_ritmos_del_fuego.png', 'Ritmos del Fuego', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Volver al Sol'), 'assets/eventos/07_volver_al_sol.png', 'Volver al Sol', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Código Norte'), 'assets/eventos/08_código_norte.png', 'Código Norte', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Horizonte Regio'), 'assets/eventos/09_horizonte_regio.png', 'Horizonte Regio', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Luz de Invierno'), 'assets/eventos/10_luz_de_invierno.png', 'Luz de Invierno', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Dinosaurios de México'), 'assets/eventos/11_dinosaurios_de_méxico.png', 'Dinosaurios de México', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Luz Eterna'), 'assets/eventos/12_luz_eterna.png', 'Luz Eterna', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'La Última Función'), 'assets/eventos/13_la_última_función.png', 'La Última Función', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'El Lago Etéreo'), 'assets/eventos/14_el_lago_etéreo.png', 'El Lago Etéreo', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Risas de Media Noche'), 'assets/eventos/15_risas_de_media_noche.png', 'Risas de Media Noche', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Futuro Real'), 'assets/eventos/16_futuro_real.png', 'Futuro Real', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'México Esports Cup'), 'assets/eventos/17_méxico_esports_cup.png', 'México Esports Cup', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Noche de Leyendas'), 'assets/eventos/18_noche_de_leyendas.png', 'Noche de Leyendas', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Sabores de México'), 'assets/eventos/19_sabores_de_méxico.png', 'Sabores de México', 0, 1),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Animeverse México'), 'assets/eventos/20_animeverse_méxico.png', 'Animeverse México', 0, 1);

INSERT INTO funciones_evento (id_evento, id_recinto, fecha_inicio, fecha_fin, estado) VALUES
  ((SELECT id_evento FROM eventos WHERE nombre = 'Ecos de Neón'), (SELECT id_recinto FROM recintos WHERE nombre = 'Estadio GNP Seguros'), '2026-10-18 02:00:00', '2026-10-18 05:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Horizonte Cero'), (SELECT id_recinto FROM recintos WHERE nombre = 'Palacio de los Deportes'), '2026-11-01 03:00:00', '2026-11-01 06:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Noches de Mercurio'), (SELECT id_recinto FROM recintos WHERE nombre = 'Auditorio Nacional'), '2026-11-08 02:00:00', '2026-11-08 05:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Frecuencia 404'), (SELECT id_recinto FROM recintos WHERE nombre = 'Arena CDMX'), '2026-11-08 02:00:00', '2026-11-08 05:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Ciudad de Cristal'), (SELECT id_recinto FROM recintos WHERE nombre = 'Pepsi Center'), '2026-11-15 03:00:00', '2026-11-15 06:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Ritmos del Fuego'), (SELECT id_recinto FROM recintos WHERE nombre = 'Auditorio Telmex'), '2026-11-22 02:00:00', '2026-11-22 05:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Volver al Sol'), (SELECT id_recinto FROM recintos WHERE nombre = 'Arena VFG'), '2026-12-06 02:00:00', '2026-12-06 05:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Código Norte'), (SELECT id_recinto FROM recintos WHERE nombre = 'Arena Monterrey'), '2026-12-13 03:00:00', '2026-12-13 06:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Horizonte Regio'), (SELECT id_recinto FROM recintos WHERE nombre = 'Estadio BBVA'), '2026-12-20 02:00:00', '2026-12-20 05:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Luz de Invierno'), (SELECT id_recinto FROM recintos WHERE nombre = 'Auditorio GNP Seguros'), '2027-01-10 02:00:00', '2027-01-10 05:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Dinosaurios de México'), (SELECT id_recinto FROM recintos WHERE nombre = 'Museo del Desierto'), '2026-10-15 16:00:00', '2026-10-15 19:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Luz Eterna'), (SELECT id_recinto FROM recintos WHERE nombre = 'Museo Franz Mayer'), '2026-11-08 16:00:00', '2026-11-08 19:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'La Última Función'), (SELECT id_recinto FROM recintos WHERE nombre = 'Teatro de la Ciudad Esperanza Iris'), '2026-11-23 01:00:00', '2026-11-23 04:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'El Lago Etéreo'), (SELECT id_recinto FROM recintos WHERE nombre = 'Palacio de Bellas Artes'), '2026-11-30 00:00:00', '2026-11-30 03:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Risas de Media Noche'), (SELECT id_recinto FROM recintos WHERE nombre = 'Auditorio Citibanamex'), '2026-12-07 03:00:00', '2026-12-07 06:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Futuro Real'), (SELECT id_recinto FROM recintos WHERE nombre = 'Centro Citibanamex'), '2026-12-10 15:00:00', '2026-12-10 18:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'México Esports Cup'), (SELECT id_recinto FROM recintos WHERE nombre = 'Expo Guadalajara'), '2026-12-13 16:00:00', '2026-12-13 19:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Noche de Leyendas'), (SELECT id_recinto FROM recintos WHERE nombre = 'Arena México'), '2026-12-21 02:30:00', '2026-12-21 05:30:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Sabores de México'), (SELECT id_recinto FROM recintos WHERE nombre = 'Parque Fundidora'), '2027-01-16 17:00:00', '2027-01-16 20:00:00', 'PROGRAMADO'),
  ((SELECT id_evento FROM eventos WHERE nombre = 'Animeverse México'), (SELECT id_recinto FROM recintos WHERE nombre = 'WTC Ciudad de México'), '2027-01-23 16:00:00', '2027-01-23 19:00:00', 'PROGRAMADO');

INSERT INTO tipos_entrada (id_funcion, nombre, descripcion, precio, moneda, cantidad_total, estado) VALUES
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Ecos de Neón'), 'VIP', 'Primeras filas', 2200.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Ecos de Neón'), 'GENERAL', 'Admision general numerada', 450.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Horizonte Cero'), 'VIP', 'Primeras filas', 1800.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Horizonte Cero'), 'GENERAL', 'Admision general numerada', 400.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Noches de Mercurio'), 'VIP', 'Primeras filas', 2000.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Noches de Mercurio'), 'GENERAL', 'Admision general numerada', 500.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Frecuencia 404'), 'VIP', 'Primeras filas', 2300.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Frecuencia 404'), 'GENERAL', 'Admision general numerada', 500.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Ciudad de Cristal'), 'VIP', 'Primeras filas', 1500.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Ciudad de Cristal'), 'GENERAL', 'Admision general numerada', 350.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Ritmos del Fuego'), 'VIP', 'Primeras filas', 1700.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Ritmos del Fuego'), 'GENERAL', 'Admision general numerada', 400.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Volver al Sol'), 'VIP', 'Primeras filas', 1600.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Volver al Sol'), 'GENERAL', 'Admision general numerada', 350.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Código Norte'), 'VIP', 'Primeras filas', 1800.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Código Norte'), 'GENERAL', 'Admision general numerada', 400.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Horizonte Regio'), 'VIP', 'Primeras filas', 2100.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Horizonte Regio'), 'GENERAL', 'Admision general numerada', 450.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Luz de Invierno'), 'VIP', 'Primeras filas', 1400.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Luz de Invierno'), 'GENERAL', 'Admision general numerada', 350.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Dinosaurios de México'), 'VIP', 'Primeras filas', 320.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Dinosaurios de México'), 'GENERAL', 'Admision general numerada', 180.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Luz Eterna'), 'VIP', 'Primeras filas', 350.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Luz Eterna'), 'GENERAL', 'Admision general numerada', 200.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'La Última Función'), 'VIP', 'Primeras filas', 600.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'La Última Función'), 'GENERAL', 'Admision general numerada', 250.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'El Lago Etéreo'), 'VIP', 'Primeras filas', 900.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'El Lago Etéreo'), 'GENERAL', 'Admision general numerada', 300.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Risas de Media Noche'), 'VIP', 'Primeras filas', 900.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Risas de Media Noche'), 'GENERAL', 'Admision general numerada', 350.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Futuro Real'), 'VIP', 'Primeras filas', 1500.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Futuro Real'), 'GENERAL', 'Admision general numerada', 500.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'México Esports Cup'), 'VIP', 'Primeras filas', 800.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'México Esports Cup'), 'GENERAL', 'Admision general numerada', 250.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Noche de Leyendas'), 'VIP', 'Primeras filas', 1200.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Noche de Leyendas'), 'GENERAL', 'Admision general numerada', 300.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Sabores de México'), 'VIP', 'Primeras filas', 400.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Sabores de México'), 'GENERAL', 'Admision general numerada', 150.00, 'MXN', 40, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Animeverse México'), 'VIP', 'Primeras filas', 600.00, 'MXN', 20, 'ACTIVO'),
  ((SELECT fe.id_funcion FROM funciones_evento fe JOIN eventos e ON e.id_evento = fe.id_evento WHERE e.nombre = 'Animeverse México'), 'GENERAL', 'Admision general numerada', 200.00, 'MXN', 40, 'ACTIVO');

INSERT INTO asientos_funcion (id_funcion, id_tipo_entrada, seccion, fila, numero, estado)
SELECT t.id_funcion, t.id_tipo_entrada, t.nombre, f.fila, n.numero, 'DISPONIBLE'
FROM tipos_entrada t
JOIN (SELECT 'VIP' AS seccion, 'A' AS fila UNION ALL SELECT 'VIP', 'B'
      UNION ALL SELECT 'GENERAL', 'C' UNION ALL SELECT 'GENERAL', 'D'
      UNION ALL SELECT 'GENERAL', 'E' UNION ALL SELECT 'GENERAL', 'F') f ON f.seccion = t.nombre
JOIN (SELECT 1 AS numero UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
      UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) n;
