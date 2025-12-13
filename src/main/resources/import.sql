-- =========================================
-- DATOS INICIALES PARA SPRING SECURITY
-- =========================================

-- 1. Crear Roles (deben tener prefijo ROLE_)
INSERT INTO roles (nombre, descripcion) VALUES('ROLE_ADMIN', 'Administrador del sistema');
INSERT INTO roles (nombre, descripcion) VALUES('ROLE_USER', 'Usuario estándar');

-- 2. Crear Usuarios (con contraseña encriptada con BCrypt)
-- Contraseña para todos: "password" (sin el 1)
-- Hash BCrypt de "password": $2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0001', 'Admin', 'Sistema', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'admin@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0002', 'Juan', 'Pérez', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'juan.perez@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0003', 'María', 'Gómez', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'maria.gomez@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0004', 'Luis', 'Ramírez', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'luis.ramirez@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0005', 'Sofía', 'López', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'sofia.lopez@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0006', 'Carlos', 'Silva', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'carlos.silva@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0007', 'Ana', 'Torres', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'ana.torres@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0008', 'Diego', 'Castro', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'diego.castro@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0009', 'Lucía', 'Ortega', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'lucia.ortega@example.com', true);
INSERT INTO usuarios (rut, nombre, apellido, password, email, activo) VALUES('USR0010', 'Pedro', 'Núñez', '$2a$10$PpeUxRrpQTF9qzmccqcZyu9BCEJJVUkoAeRJtwG1LlAbt1QnbH/la', 'pedro.nunez@example.com', true);
-- 3. ASIGNAR ROLES A USUARIOS (tabla intermedia)
-- Usuario 1 (Admin) = ADMIN
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (1, 1);
-- Usuario 2 (Juan) = ADMIN
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (2, 1);
-- Usuario 3 (María) = USER
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (3, 2);
-- Usuario 4 (Luis) = USER
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (4, 2);
-- Usuario 5 (Sofía) = USER
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (5, 2);
-- Usuario 6 (Carlos) = ADMIN
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (6, 1);
-- Usuario 7 (Ana) = USER
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (7, 2);
-- Usuario 8 (Diego) = USER
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (8, 2);
-- Usuario 9 (Lucía) = USER
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (9, 2);
-- Usuario 10 (Pedro) = USER
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (10, 2);

-- Cargar datos iniciales para la tabla 'juegos'
INSERT INTO juegos (titulo, genero, plataforma, stock, precio) VALUES('Horizon Forbidden West', 'Aventura', 'PS5', 12, 49990);
INSERT INTO juegos (titulo, genero, plataforma, stock, precio) VALUES('Legend of Zelda: Tears', 'Accion', 'Switch', 4, 59990);
INSERT INTO juegos (titulo, genero, plataforma, stock, precio) VALUES('Forza Horizon 5', 'Carreras', 'Xbox', 9, 44990);