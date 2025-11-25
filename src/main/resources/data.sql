-- =========================================================
-- DATA INICIAL NADA (H2/MySQL) - Herencia JOINED; ENUM = ORDINAL
-- Tablas creadas previamente por Hibernate (ddl-auto)
-- =========================================================

-- ========== USUARIOS ==========
INSERT INTO usuarios (id, username, password, email)
VALUES (1, 'alice', 'alice123', 'alice@nada.test'),
       (2, 'bob', 'bob123', 'bob@nada.test'),
       (3, 'carla', 'carla123', 'carla@nada.test'),
       (4, 'diego', 'diego123', 'diego@nada.test'),
       (5, 'eva', 'eva123', 'eva@nada.test'),
       (6, 'fran', 'fran123', 'fran@nada.test');

-- =========================================================
-- PRENDAS BASE (prenda)
-- Campos: id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda
-- =========================================================


-- Usuario 1: alice
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (1, 'Camiseta blanca', 'blanco', 'Uniqlo', 'M', NULL, 1, 'SUPERIOR'),
       (2, 'Camisa celeste', 'celeste', 'Massimo Dutti', 'M', NULL, 1, 'SUPERIOR'),
       (3, 'Suéter gris', 'gris', 'H&M', 'M', NULL, 1, 'SUPERIOR'),
       (4, 'Vaquero slim', 'azul', 'Levi''s', '36', NULL, 1, 'INFERIOR'),
       (5, 'Pantalón chino', 'beige', 'Dockers', '36', NULL, 1, 'INFERIOR'),
       (6, 'Short running', 'negro', 'Nike', '36', NULL, 1, 'INFERIOR'),
       (7, 'Zapatillas running', 'negro', 'Nike', '40', NULL, 1, 'CALZADO'),
       (8, 'Zapatillas urban', 'blanco', 'Adidas', '40', NULL, 1, 'CALZADO');

-- Usuario 2: bob
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (9, 'Chaqueta cuero', 'negro', 'Zara', 'M', NULL, 2, 'SUPERIOR'),
       (10, 'Abrigo lana', 'camel', 'Mango', 'M', NULL, 2, 'SUPERIOR'),
       (11, 'Camiseta negra', 'negro', 'Uniqlo', 'M', NULL, 2, 'SUPERIOR'),
       (12, 'Jeans negros', 'negro', 'Levi''s', '36', NULL, 2, 'INFERIOR'),
       (13, 'Pantalón vestir', 'gris', 'Hugo Boss', '36', NULL, 2, 'INFERIOR'),
       (14, 'Short denim', 'azul', 'Pull&Bear', '36', NULL, 2, 'INFERIOR'),
       (15, 'Botas montaña', 'marrón', 'Salomon', '40', NULL, 2, 'CALZADO'),
       (16, 'Zapatos oxford', 'negro', 'Clarks', '40', NULL, 2, 'CALZADO');

-- Usuario 3: carla
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (17, 'Blusa seda', 'blanco', 'Mango', 'M', NULL, 3, 'SUPERIOR'),
       (18, 'Chaqueta vaquera', 'azul', 'Levi''s', 'M', NULL, 3, 'SUPERIOR'),
       (19, 'Camiseta rayas', 'marino', 'Petit Bateau', 'M', NULL, 3, 'SUPERIOR'),
       (20, 'Falda midi', 'verde', 'Zara', '36', NULL, 3, 'INFERIOR'),
       (21, 'Pantalón culotte', 'negro', 'COS', '36', NULL, 3, 'INFERIOR'),
       (22, 'Jeans recto', 'azul', 'Lee', '36', NULL, 3, 'INFERIOR'),
       (23, 'Sandalias tira', 'negro', 'Birkenstock', '40', NULL, 3, 'CALZADO'),
       (24, 'Zapatillas plataforma', 'blanco', 'Victoria', '40', NULL, 3, 'CALZADO');

-- Usuario 4: diego
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (25, 'Camisa cuadros', 'rojo', 'Barbour', 'M', NULL, 4, 'SUPERIOR'),
       (26, 'Suéter cuello alto', 'gris', 'Uniqlo', 'M', NULL, 4, 'SUPERIOR'),
       (27, 'Chaqueta bomber', 'verde', 'Alpha', 'M', NULL, 4, 'SUPERIOR'),
       (28, 'Pantalón cargo', 'verde', 'Carhartt', '36', NULL, 4, 'INFERIOR'),
       (29, 'Jeans oscuro', 'azul', 'Wrangler', '36', NULL, 4, 'INFERIOR'),
       (30, 'Short trekking', 'gris', 'Quechua', '36', NULL, 4, 'INFERIOR'),
       (31, 'Botas senderismo', 'gris', 'Merrell', '40', NULL, 4, 'CALZADO'),
       (32, 'Zapatillas court', 'blanco', 'New Balance', '40', NULL, 4, 'CALZADO');

-- Usuario 5: eva
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (33, 'Blusa flores', 'multicolor', 'Zara', 'M', NULL, 5, 'SUPERIOR'),
       (34, 'Abrigo largo', 'negro', 'COS', 'M', NULL, 5, 'SUPERIOR'),
       (35, 'Camiseta básica', 'blanco', 'H&M', 'M', NULL, 5, 'SUPERIOR'),
       (36, 'Falda lápiz', 'negro', 'Mango', '36', NULL, 5, 'INFERIOR'),
       (37, 'Pantalón traje', 'azul', 'Massimo Dutti', '36', NULL, 5, 'INFERIOR'),
       (38, 'Short lino', 'beige', 'Oysho', '36', NULL, 5, 'INFERIOR'),
       (39, 'Zapato salón', 'negro', 'Geox', '40', NULL, 5, 'CALZADO'),
       (40, 'Zapatillas running', 'rosa', 'Asics', '40', NULL, 5, 'CALZADO');

-- Usuario 6: fran
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (41, 'Camisa blanca', 'blanco', 'Hugo Boss', 'M', NULL, 6, 'SUPERIOR'),
       (42, 'Chaqueta softshell', 'azul', 'The North Face', 'M', NULL, 6, 'SUPERIOR'),
       (43, 'Suéter azul', 'azul', 'GAP', 'M', NULL, 6, 'SUPERIOR'),
       (44, 'Pantalón jogger', 'gris', 'Nike', '36', NULL, 6, 'INFERIOR'),
       (45, 'Jeans slim', 'azul', 'Pepe Jeans', '36', NULL, 6, 'INFERIOR'),
       (46, 'Short deporte', 'negro', 'Adidas', '36', NULL, 6, 'INFERIOR'),
       (47, 'Zapatillas trail', 'naranja', 'Salomon', '40', NULL, 6, 'CALZADO'),
       (48, 'Zapatos derby', 'marrón', 'Clarks', '40', NULL, 6, 'CALZADO');

-- =========================================================
-- SUBTABLAS (JOINED)
--  SUPERIOR: (id, categoria, manga)
--  INFERIOR: (id, categoria_inferior)
--  CALZADO : (id, categoria)
-- =========================================================

-- SUPERIORES
INSERT INTO prenda_superior (id, categoria, manga)
VALUES (1, 0, 0),
       (2, 1, 1),
       (3, 2, 1),
       (9, 3, 1),
       (10, 4, 1),
       (11, 0, 0),
       (17, 5, 2),
       (18, 3, 1),
       (19, 0, 0),
       (25, 1, 1),
       (26, 2, 1),
       (27, 3, 1),
       (33, 5, 2),
       (34, 4, 1),
       (35, 0, 0),
       (41, 1, 1),
       (42, 3, 1),
       (43, 2, 1);

-- INFERIORES
INSERT INTO prenda_inferior (id, categoria_inferior)
VALUES (4, 3),
       (5, 0),
       (6, 1),
       (12, 3),
       (13, 0),
       (14, 1),
       (20, 2),
       (21, 0),
       (22, 3),
       (28, 0),
       (29, 3),
       (30, 1),
       (36, 2),
       (37, 0),
       (38, 1),
       (44, 0),
       (45, 3),
       (46, 1);

-- CALZADOS
INSERT INTO prenda_calzado (id, categoria)
VALUES (7, 0),
       (8, 4),
       (15, 2),
       (16, 1),
       (23, 3),
       (24, 4),
       (31, 2),
       (32, 4),
       (39, 1),
       (40, 0),
       (47, 0),
       (48, 1);

-- =========================================================
-- CONJUNTOS
-- Campos: id, nombre, descripcion, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id
-- =========================================================

INSERT INTO conjunto (id, nombre, descripcion, usuario_id, prenda_superior_id, prenda_inferior_id, prenda_calzado_id)
VALUES (1, 'Look running', 'nada', 1, 1, 6, 7),
       (2, 'Casual vaquero', 'nada', 1, 2, 4, 8),
       (3, 'Office light', 'nada', 1, 3, 5, 7),

       (4, 'Noche formal', 'nada', 2, 10, 13, 16),
       (5, 'Moto casual', 'nada', 2, 9, 12, 15),
       (6, 'Summer denim', 'nada', 2, 11, 14, 16),

       (7, 'Brisa verano', 'nada', 3, 17, 20, 23),
       (8, 'Weekend jean', 'nada', 3, 18, 22, 24),
       (9, 'Oficina chic', 'nada', 3, 19, 21, 24),

       (10, 'Bosque urbano', 'nada', 4, 27, 28, 31),
       (11, 'Casual oscuro', 'nada', 4, 25, 29, 32),
       (12, 'Trekking light', 'nada', 4, 26, 30, 31),

       (13, 'Reunión', 'nada', 5, 34, 37, 39),
       (14, 'Cena formal', 'nada', 5, 33, 36, 39),
       (15, 'Sport day', 'nada', 5, 35, 38, 40),

       (16, 'Negocios', 'nada', 6, 41, 45, 48),
       (17, 'Trail pack', 'nada', 6, 42, 44, 47),
       (18, 'Gym rápido', 'nada', 6, 43, 46, 47);
