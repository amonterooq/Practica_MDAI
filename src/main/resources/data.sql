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
VALUES (1, 'Camiseta blanca', 'Blanco', 'Uniqlo', 'M', NULL, 1, 'SUPERIOR'),
       (2, 'Camisa celeste', 'Azul claro', 'Massimo Dutti', 'M', NULL, 1, 'SUPERIOR'),
       (3, 'Suéter gris', 'Gris', 'H&M', 'M', NULL, 1, 'SUPERIOR'),
       (4, 'Vaquero slim', 'Azul', 'Levi''s', '36', NULL, 1, 'INFERIOR'),
       (5, 'Pantalón chino', 'Beige', 'Dockers', '36', NULL, 1, 'INFERIOR'),
       (6, 'Short running', 'Negro', 'Nike', '36', NULL, 1, 'INFERIOR'),
       (7, 'Zapatillas running', 'Negro', 'Nike', '40', NULL, 1, 'CALZADO'),
       (8, 'Zapatillas urban', 'Blanco', 'Adidas', '40', NULL, 1, 'CALZADO');

-- Usuario 2: bob
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (9, 'Chaqueta cuero', 'Negro', 'Zara', 'M', NULL, 2, 'SUPERIOR'),
       (10, 'Abrigo lana', 'Camel', 'Mango', 'M', NULL, 2, 'SUPERIOR'),
       (11, 'Camiseta negra', 'Negro', 'Uniqlo', 'M', NULL, 2, 'SUPERIOR'),
       (12, 'Jeans negros', 'Negro', 'Levi''s', '36', NULL, 2, 'INFERIOR'),
       (13, 'Pantalón vestir', 'Gris', 'Hugo Boss', '36', NULL, 2, 'INFERIOR'),
       (14, 'Short denim', 'Azul', 'Pull&Bear', '36', NULL, 2, 'INFERIOR'),
       (15, 'Botas montaña', 'Marrón', 'Salomon', '40', NULL, 2, 'CALZADO'),
       (16, 'Zapatos oxford', 'Negro', 'Clarks', '40', NULL, 2, 'CALZADO');

-- Usuario 3: carla
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (17, 'Blusa seda', 'Blanco', 'Mango', 'M', NULL, 3, 'SUPERIOR'),
       (18, 'Chaqueta vaquera', 'Azul', 'Levi''s', 'M', NULL, 3, 'SUPERIOR'),
       (19, 'Camiseta rayas', 'Azul marino', 'Petit Bateau', 'M', NULL, 3, 'SUPERIOR'),
       (20, 'Falda midi', 'Verde', 'Zara', '36', NULL, 3, 'INFERIOR'),
       (21, 'Pantalón culotte', 'Negro', 'COS', '36', NULL, 3, 'INFERIOR'),
       (22, 'Jeans recto', 'Azul', 'Lee', '36', NULL, 3, 'INFERIOR'),
       (23, 'Sandalias tira', 'Negro', 'Birkenstock', '40', NULL, 3, 'CALZADO'),
       (24, 'Zapatillas plataforma', 'Blanco', 'Victoria', '40', NULL, 3, 'CALZADO');

-- Usuario 4: diego
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (25, 'Camisa cuadros', 'Rojo', 'Barbour', 'M', NULL, 4, 'SUPERIOR'),
       (26, 'Suéter cuello alto', 'Gris', 'Uniqlo', 'M', NULL, 4, 'SUPERIOR'),
       (27, 'Chaqueta bomber', 'Verde', 'Alpha', 'M', NULL, 4, 'SUPERIOR'),
       (28, 'Pantalón cargo', 'Verde', 'Carhartt', '36', NULL, 4, 'INFERIOR'),
       (29, 'Jeans oscuro', 'Azul oscuro', 'Wrangler', '36', NULL, 4, 'INFERIOR'),
       (30, 'Short trekking', 'Gris', 'Quechua', '36', NULL, 4, 'INFERIOR'),
       (31, 'Botas senderismo', 'Gris', 'Merrell', '40', NULL, 4, 'CALZADO'),
       (32, 'Zapatillas court', 'Blanco', 'New Balance', '40', NULL, 4, 'CALZADO');

-- Usuario 5: eva
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (33, 'Blusa flores', 'Multicolor', 'Zara', 'M', NULL, 5, 'SUPERIOR'),
       (34, 'Abrigo largo', 'Negro', 'COS', 'M', NULL, 5, 'SUPERIOR'),
       (35, 'Camiseta básica', 'Blanco', 'H&M', 'M', NULL, 5, 'SUPERIOR'),
       (36, 'Falda lápiz', 'Negro', 'Mango', '36', NULL, 5, 'INFERIOR'),
       (37, 'Pantalón traje', 'Azul', 'Massimo Dutti', '36', NULL, 5, 'INFERIOR'),
       (38, 'Short lino', 'Beige', 'Oysho', '36', NULL, 5, 'INFERIOR'),
       (39, 'Zapato salón', 'Negro', 'Geox', '40', NULL, 5, 'CALZADO'),
       (40, 'Zapatillas running', 'Rosa', 'Asics', '40', NULL, 5, 'CALZADO');

-- Usuario 6: fran
INSERT INTO prenda (id, nombre, color, marca, talla, dir_imagen, usuario_id, tipo_prenda)
VALUES (41, 'Camisa blanca', 'Blanco', 'Hugo Boss', 'M', '/images/6/prenda_1.jpg', 6, 'SUPERIOR'),
       (42, 'Chaqueta softshell', 'Azul', 'The North Face', 'M', '/images/6/prenda_2.jpg', 6, 'SUPERIOR'),
       (43, 'Suéter azul', 'Azul', 'GAP', 'M', '/images/6/prenda_3.jpg', 6, 'SUPERIOR'),
       (44, 'Pantalón jogger', 'Gris', 'Nike', '36', '/images/6/prenda_4.jpg', 6, 'INFERIOR'),
       (45, 'Jeans slim', 'Azul', 'Pepe Jeans', '36', '/images/6/prenda_5.jpg', 6, 'INFERIOR'),
       (46, 'Short deporte', 'Negro', 'Adidas', '36', '/images/6/prenda_6.jpg', 6, 'INFERIOR'),
       (47, 'Zapatillas trail', 'Naranja', 'Salomon', '40', '/images/6/prenda_7.jpg', 6, 'CALZADO'),
       (48, 'Zapatos derby', 'Marrón', 'Clarks', '40', '/images/6/prenda_8.jpg', 6, 'CALZADO');

-- =========================================================
-- SUBTABLAS (JOINED)
--  SUPERIOR: (id, categoria, manga)
--  INFERIOR: (id, categoria_inferior)
--  CALZADO : (id, categoria)
-- =========================================================

-- SUPERIORES
INSERT INTO prenda_superior (id, categoria, manga)
VALUES (1, 5, 0),   -- Camiseta blanca -> CAMISETA
       (2, 4, 1),   -- Camisa celeste -> CAMISA
       (3, 11, 1),  -- Suéter gris -> JERSEY
       (9, 9, 1),   -- Chaqueta cuero -> CHAQUETA
       (10, 0, 1),  -- Abrigo lana -> ABRIGO
       (11, 5, 0),  -- Camiseta negra -> CAMISETA
       (17, 2, 2),  -- Blusa seda -> BLUSA
       (18, 7, 1),  -- Chaqueta vaquera -> CAZADORA
       (19, 5, 0),  -- Camiseta rayas -> CAMISETA
       (25, 4, 1),  -- Camisa cuadros -> CAMISA
       (26, 11, 1), -- Suéter cuello alto -> JERSEY
       (27, 7, 1),  -- Chaqueta bomber -> CAZADORA
       (33, 2, 2),  -- Blusa flores -> BLUSA
       (34, 0, 1),  -- Abrigo largo -> ABRIGO
       (35, 5, 0),  -- Camiseta básica -> CAMISETA
       (41, 4, 1),  -- Camisa blanca -> CAMISA
       (42, 9, 1),  -- Chaqueta softshell -> CHAQUETA
       (43, 11, 1); -- Suéter azul -> JERSEY

-- INFERIORES
-- Orden de CategoriaInferior (ordinal):
-- 0 BERMUDA, 1 FALDA_CORTA, 2 FALDA_LARGA, 3 FALDA_MIDI,
-- 4 JEAN, 5 JOGGER, 6 LEGGINGS, 7 MONO_CORTO, 8 MONO_LARGO,
-- 9 PANTALON, 10 PANTALON_CHANDAL, 11 PANTALON_VAQUERO,
-- 12 PANTALON_VESTIR, 13 SHORT
INSERT INTO prenda_inferior (id, categoria_inferior)
VALUES (4, 11),   -- Vaquero slim -> PANTALON_VAQUERO
       (5, 9),    -- Pantalón chino -> PANTALON
       (6, 10),   -- Short running -> PANTALON_CHANDAL
       (12, 11),  -- Jeans negros -> PANTALON_VAQUERO
       (13, 12),  -- Pantalón vestir -> PANTALON_VESTIR
       (14, 13),  -- Short denim -> SHORT
       (20, 3),   -- Falda midi -> FALDA_MIDI
       (21, 9),   -- Pantalón culotte -> PANTALON (aprox)
       (22, 11),  -- Jeans recto -> PANTALON_VAQUERO
       (28, 9),   -- Pantalón cargo -> PANTALON
       (29, 11),  -- Jeans oscuro -> PANTALON_VAQUERO
       (30, 13),  -- Short trekking -> SHORT
       (36, 2),   -- Falda lápiz -> FALDA_LARGA (aprox)
       (37, 12),  -- Pantalón traje -> PANTALON_VESTIR
       (38, 13),  -- Short lino -> SHORT
       (44, 5),   -- Pantalón jogger -> JOGGER
       (45, 11),  -- Jeans slim -> PANTALON_VAQUERO
       (46, 13);  -- Short deporte -> SHORT

-- CALZADOS
INSERT INTO prenda_calzado (id, categoria)
VALUES (7, 6),    -- Zapatillas running -> DEPORTIVO
       (8, 12),   -- Zapatillas urban -> ZAPATILLA_CASUAL
       (15, 2),   -- Botas montaña -> BOTA
       (16, 13),  -- Zapatos oxford -> ZAPATO_FORMAL
       (23, 9),   -- Sandalias tira -> SANDALIA
       (24, 12),  -- Zapatillas plataforma -> ZAPATILLA_CASUAL
       (31, 2),   -- Botas senderismo -> BOTA
       (32, 6),   -- Zapatillas court -> DEPORTIVO
       (39, 11),  -- Zapato salón -> TACON
       (40, 6),   -- Zapatillas running -> DEPORTIVO
       (47, 6),   -- Zapatillas trail -> DEPORTIVO
       (48, 13);  -- Zapatos derby -> ZAPATO_FORMAL

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
