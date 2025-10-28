-- =========================================================
-- DATA INICIAL NADA (válido para H2 y MySQL)
-- Herencia JOINED; enums almacenados como ORDINAL
-- Tablas creadas previamente por Hibernate (ddl-auto)
-- =========================================================

-- ========== USUARIOS ==========
INSERT INTO usuarios (id, username, password, email) VALUES
(1,'alice','alice123','alice@nada.test'),
(2,'bob','bob123','bob@nada.test'),
(3,'carla','carla123','carla@nada.test'),
(4,'diego','diego123','diego@nada.test'),
(5,'eva','eva123','eva@nada.test'),
(6,'fran','fran123','fran@nada.test');

-- =========================================================
-- PRENDAS BASE (prenda)  - usar tipo_prenda: SUPERIOR | INFERIOR | CALZADO
-- Campos: id, nombre, color, marca, url_imagen, usuario_id, tipo_prenda
-- =========================================================

-- Usuario 1: alice
INSERT INTO prenda (id, nombre, color, marca, url_imagen, usuario_id, tipo_prenda) VALUES
(1,'Camiseta blanca','blanco','Uniqlo',NULL,1,'SUPERIOR'),
(2,'Camisa celeste','celeste','Massimo Dutti',NULL,1,'SUPERIOR'),
(3,'Suéter gris','gris','H&M',NULL,1,'SUPERIOR'),
(4,'Vaquero slim','azul','Levi''s',NULL,1,'INFERIOR'),
(5,'Pantalón chino','beige','Dockers',NULL,1,'INFERIOR'),
(6,'Short running','negro','Nike',NULL,1,'INFERIOR'),
(7,'Zapatillas running','negro','Nike',NULL,1,'CALZADO'),
(8,'Zapatillas urban','blanco','Adidas',NULL,1,'CALZADO');

-- Usuario 2: bob
INSERT INTO prenda (id, nombre, color, marca, url_imagen, usuario_id, tipo_prenda) VALUES
(9,'Chaqueta cuero','negro','Zara',NULL,2,'SUPERIOR'),
(10,'Abrigo lana','camel','Mango',NULL,2,'SUPERIOR'),
(11,'Camiseta negra','negro','Uniqlo',NULL,2,'SUPERIOR'),
(12,'Jeans negros','negro','Levi''s',NULL,2,'INFERIOR'),
(13,'Pantalón vestir','gris','Hugo Boss',NULL,2,'INFERIOR'),
(14,'Short denim','azul','Pull&Bear',NULL,2,'INFERIOR'),
(15,'Botas montaña','marrón','Salomon',NULL,2,'CALZADO'),
(16,'Zapatos oxford','negro','Clarks',NULL,2,'CALZADO');

-- Usuario 3: carla
INSERT INTO prenda (id, nombre, color, marca, url_imagen, usuario_id, tipo_prenda) VALUES
(17,'Blusa seda','blanco','Mango',NULL,3,'SUPERIOR'),
(18,'Chaqueta vaquera','azul','Levi''s',NULL,3,'SUPERIOR'),
(19,'Camiseta rayas','marino','Petit Bateau',NULL,3,'SUPERIOR'),
(20,'Falda midi','verde','Zara',NULL,3,'INFERIOR'),
(21,'Pantalón culotte','negro','COS',NULL,3,'INFERIOR'),
(22,'Jeans recto','azul','Lee',NULL,3,'INFERIOR'),
(23,'Sandalias tira','negro','Birkenstock',NULL,3,'CALZADO'),
(24,'Zapatillas plataforma','blanco','Victoria',NULL,3,'CALZADO');

-- Usuario 4: diego
INSERT INTO prenda (id, nombre, color, marca, url_imagen, usuario_id, tipo_prenda) VALUES
(25,'Camisa cuadros','rojo','Barbour',NULL,4,'SUPERIOR'),
(26,'Suéter cuello alto','gris','Uniqlo',NULL,4,'SUPERIOR'),
(27,'Chaqueta bomber','verde','Alpha',NULL,4,'SUPERIOR'),
(28,'Pantalón cargo','verde','Carhartt',NULL,4,'INFERIOR'),
(29,'Jeans oscuro','azul','Wrangler',NULL,4,'INFERIOR'),
(30,'Short trekking','gris','Quechua',NULL,4,'INFERIOR'),
(31,'Botas senderismo','gris','Merrell',NULL,4,'CALZADO'),
(32,'Zapatillas court','blanco','New Balance',NULL,4,'CALZADO');

-- Usuario 5: eva
INSERT INTO prenda (id, nombre, color, marca, url_imagen, usuario_id, tipo_prenda) VALUES
(33,'Blusa flores','multicolor','Zara',NULL,5,'SUPERIOR'),
(34,'Abrigo largo','negro','COS',NULL,5,'SUPERIOR'),
(35,'Camiseta básica','blanco','H&M',NULL,5,'SUPERIOR'),
(36,'Falda lápiz','negro','Mango',NULL,5,'INFERIOR'),
(37,'Pantalón traje','azul','Massimo Dutti',NULL,5,'INFERIOR'),
(38,'Short lino','beige','Oysho',NULL,5,'INFERIOR'),
(39,'Zapato salón','negro','Geox',NULL,5,'CALZADO'),
(40,'Zapatillas running','rosa','Asics',NULL,5,'CALZADO');

-- Usuario 6: fran
INSERT INTO prenda (id, nombre, color, marca, url_imagen, usuario_id, tipo_prenda) VALUES
(41,'Camisa blanca','blanco','Hugo Boss',NULL,6,'SUPERIOR'),
(42,'Chaqueta softshell','azul','The North Face',NULL,6,'SUPERIOR'),
(43,'Suéter azul','azul','GAP',NULL,6,'SUPERIOR'),
(44,'Pantalón jogger','gris','Nike',NULL,6,'INFERIOR'),
(45,'Jeans slim','azul','Pepe Jeans',NULL,6,'INFERIOR'),
(46,'Short deporte','negro','Adidas',NULL,6,'INFERIOR'),
(47,'Zapatillas trail','naranja','Salomon',NULL,6,'CALZADO'),
(48,'Zapatos derby','marrón','Clarks',NULL,6,'CALZADO');

-- =========================================================
-- SUBTABLAS (JOINED)
--  SUPERIOR: (id, categoria, manga)
--  INFERIOR: (id, categoria_inferior)
--  CALZADO : (id, categoria)
--  * Enums como ORDINAL según mapeo de arriba
-- =========================================================

-- SUPERIORES
INSERT INTO prenda_superior (id, categoria, manga) VALUES
(1, 0, 0),  -- Camiseta / CORTA
(2, 1, 1),  -- Camisa  / LARGA
(3, 2, 1),  -- Suéter  / LARGA
(9, 3, 1),  -- Chaqueta/ LARGA
(10, 4, 1), -- Abrigo  / LARGA
(11, 0, 0), -- Camiseta/ CORTA
(17, 5, 2), -- Blusa   / SIN_MANGA
(18, 3, 1), -- Chaqueta/ LARGA
(19, 0, 0), -- Camiseta/ CORTA
(25, 1, 1), -- Camisa  / LARGA
(26, 2, 1), -- Suéter  / LARGA
(27, 3, 1), -- Chaqueta/ LARGA
(33, 5, 2), -- Blusa   / SIN_MANGA
(34, 4, 1), -- Abrigo  / LARGA
(35, 0, 0), -- Camiseta/ CORTA
(41, 1, 1), -- Camisa  / LARGA
(42, 3, 1), -- Chaqueta/ LARGA
(43, 2, 1); -- Suéter  / LARGA

-- INFERIORES
INSERT INTO prenda_inferior (id, categoria_inferior) VALUES
(4, 3),  -- JEAN
(5, 0),  -- PANTALON
(6, 1),  -- SHORT
(12, 3), -- JEAN
(13, 0), -- PANTALON
(14, 1), -- SHORT
(20, 2), -- FALDA
(21, 0), -- PANTALON
(22, 3), -- JEAN
(28, 0), -- PANTALON
(29, 3), -- JEAN
(30, 1), -- SHORT
(36, 2), -- FALDA
(37, 0), -- PANTALON
(38, 1), -- SHORT
(44, 0), -- PANTALON (jogger)
(45, 3), -- JEAN
(46, 1); -- SHORT

-- CALZADOS
INSERT INTO prenda_calzado (id, categoria) VALUES
(7, 0),   -- DEPORTIVO
(8, 4),   -- ZAPATILLAS
(15, 2),  -- BOTAS
(16, 1),  -- FORMAL
(23, 3),  -- SANDALIAS
(24, 4),  -- ZAPATILLAS
(31, 2),  -- BOTAS
(32, 4),  -- ZAPATILLAS
(39, 1),  -- FORMAL (salón)
(40, 0),  -- DEPORTIVO
(47, 0),  -- DEPORTIVO (trail)
(48, 1);  -- FORMAL (derby)

-- =========================================================
-- CONJUNTOS (todas las prendas pertenecen al mismo usuario)
-- Campos: id, nombre, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id
-- =========================================================

-- alice (1): usa 1-2-7 / 2-4-8 / 3-5-7
INSERT INTO conjunto (id, nombre, descripcion,  usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id) VALUES
(1, 'Look running', nada, 1, 1, 6, 7),
(2, 'Casual vaquero', nada, 1, 2, 4, 8),
(3, 'Office light', nada, 1, 3, 5, 7);

-- bob (2): usa 9/12/16 y 10/13/16 y 11/14/15
INSERT INTO conjunto (id, nombre, descripcion, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id) VALUES
(4, 'Noche formal', nada, 2, 10, 13, 16),
(5, 'Moto casual', nada, 2, 9, 12, 15),
(6, 'Summer denim', nada, 2, 11, 14, 16);

-- carla (3): 17/20/23, 18/22/24, 19/21/24
INSERT INTO conjunto (id, nombre, descripcion, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id) VALUES
(7, 'Brisa verano', nada, 3, 17, 20, 23),
(8, 'Weekend jean', nada, 3, 18, 22, 24),
(9, 'Oficina chic', nada, 3, 19, 21, 24);

-- diego (4): 25/29/32, 26/28/31, 27/30/32
INSERT INTO conjunto (id, nombre, descripcion, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id) VALUES
(10, 'Bosque urbano', nada, 4, 27, 28, 31),
(11, 'Casual oscuro', nada, 4, 25, 29, 32),
(12, 'Trekking light', nada, 4, 26, 30, 31);

-- eva (5): 33/36/39, 34/37/39, 35/38/40
INSERT INTO conjunto (id, nombre, descripcion, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id) VALUES
(13, 'Reunión', nada, 5, 34, 37, 39),
(14, 'Cena formal', nada, 5, 33, 36, 39),
(15, 'Sport day', nada, 5, 35, 38, 40);

-- fran (6): 41/45/48, 42/44/47, 43/46/47
INSERT INTO conjunto (id, nombre, descripcion, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id) VALUES
(16, 'Negocios', nada, 6, 41, 45, 48),
(17, 'Trail pack', nada, 6, 42, 44, 47),
(18, 'Gym rápido', nada, 6, 43, 46, 47);
