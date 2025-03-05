

DROP DATABASE IF EXISTS sistemamedico;
CREATE DATABASE sistemamedico CHARACTER SET utf8 COLLATE utf8_general_ci;
USE sistemamedico;

CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL
);
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE usuario_roles (
    usuario_id BIGINT,
    rol_id BIGINT,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);


INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_USER');


DROP USER IF EXISTS 'admin'@'localhost';
FLUSH PRIVILEGES;

CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON sistemamedico.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;


SELECT * FROM usuarios;

