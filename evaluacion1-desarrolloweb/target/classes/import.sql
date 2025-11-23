-- Cargar datos iniciales para la tabla 'usuarios'
-- Nota: columnas alineadas con la entidad Usuario (sin 'edad')
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0001','Juan','Perez','password1','juan.perez@example.com','ADMIN');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0002','Maria','Gomez','password2','maria.gomez@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0003','Luis','Rodriguez','password3','luis.rodriguez@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0004','Ana','Martinez','password4','ana.martinez@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0005','Carlos','Sanchez','password5','carlos.sanchez@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0006','Laura','Fernandez','password6','laura.fernandez@example.com','ADMIN');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0007','Pedro','Lopez','password7','pedro.lopez@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0008','Sofia','Diaz','password8','sofia.diaz@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0009','Diego','Torres','password9','diego.torres@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0010','Lucia','Ramirez','password10','lucia.ramirez@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0011','Andres','Herrera','password11','andres.herrera@example.com','ADMIN');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0012','Valentina','Morales','password12','valentina.morales@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0013','Miguel','Castro','password13','miguel.castro@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0014','Camila','Navarro','password14','camila.navarro@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0015','Santiago','Vega','password15','santiago.vega@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0016','Daniela','Rojas','password16','daniela.rojas@example.com','ADMIN');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0017','Mateo','Fuentes','password17','mateo.fuentes@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0018','Martina','Silva','password18','martina.silva@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0019','Nicolas','Cabrera','password19','nicolas.cabrera@example.com','USER');
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol) VALUES('USR0020','Paula','Vargas','password20','paula.vargas@example.com','USER');

-- Cargar datos iniciales para la tabla 'juegos'
INSERT INTO juegos (titulo, genero, plataforma, stock, precio) VALUES('Horizon Forbidden West', 'Aventura', 'PS5', 12, 49990);
INSERT INTO juegos (titulo, genero, plataforma, stock, precio) VALUES('Legend of Zelda: Tears', 'Accion', 'Switch', 4, 59990);
INSERT INTO juegos (titulo, genero, plataforma, stock, precio) VALUES('Forza Horizon 5', 'Carreras', 'Xbox', 9, 44990);