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

-- Tabla de citas
CREATE TABLE citas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_paciente BIGINT NOT NULL,
    id_doctor BIGINT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    motivo TEXT,
    estado ENUM('pendiente', 'confirmada', 'cancelada') DEFAULT 'pendiente',
    FOREIGN KEY (id_paciente) REFERENCES usuarios(id),
    FOREIGN KEY (id_doctor) REFERENCES usuarios(id)
);
-- Tabla de consultas
CREATE TABLE consultas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_paciente BIGINT NOT NULL,
    id_doctor BIGINT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    sintomas TEXT NOT NULL,
    medicamentos TEXT,
    dosis TEXT,
    estado ENUM('pendiente', 'confirmada', 'cancelada') DEFAULT 'pendiente',
    fecha_proxima_cita DATE,
    FOREIGN KEY (id_paciente) REFERENCES usuarios(id),
    FOREIGN KEY (id_doctor) REFERENCES usuarios(id)
);

-- Tabla de historial médico
CREATE TABLE historial_medico (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_paciente BIGINT NOT NULL,
    id_doctor BIGINT NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    descripcion TEXT NOT NULL,
    prescripciones TEXT,
    FOREIGN KEY (id_paciente) REFERENCES usuarios(id),
    FOREIGN KEY (id_doctor) REFERENCES usuarios(id)
);

-- Tabla de mensajes para el chat
CREATE TABLE mensajes_chat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_emisor BIGINT NOT NULL,
    id_receptor BIGINT NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_emisor) REFERENCES usuarios(id),
    FOREIGN KEY (id_receptor) REFERENCES usuarios(id)
);

INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_USER'), ('ROLE_DOCTOR');

-- Trigger para asignar automáticamente roles según el dominio del email
DELIMITER $$
CREATE TRIGGER auto_assign_roles_by_email 
AFTER INSERT ON usuarios
FOR EACH ROW 
BEGIN
    -- Si el email termina en @admin.com, asignar rol ADMIN
    IF NEW.email REGEXP '.*@admin\\.com$' THEN
        INSERT IGNORE INTO usuario_roles (usuario_id, rol_id)
        SELECT NEW.id, r.id 
        FROM roles r 
        WHERE r.nombre = 'ROLE_ADMIN';
    END IF;
    
    -- Si el email termina en @doctor.com, asignar rol DOCTOR
    IF NEW.email REGEXP '.*@doctor\\.com$' THEN
        INSERT IGNORE INTO usuario_roles (usuario_id, rol_id)
        SELECT NEW.id, r.id 
        FROM roles r 
        WHERE r.nombre = 'ROLE_DOCTOR';
    END IF;
    
    -- Si no coincide con ningún patrón especial, asignar rol USER por defecto
    IF NOT (NEW.email REGEXP '.*@admin\\.com$' OR NEW.email REGEXP '.*@doctor\\.com$') THEN
        INSERT IGNORE INTO usuario_roles (usuario_id, rol_id)
        SELECT NEW.id, r.id 
        FROM roles r 
        WHERE r.nombre = 'ROLE_USER';
    END IF;
END$$
DELIMITER ;

-- Los usuarios se crearán automáticamente vía registro con triggers

-- Los roles se asignarán automáticamente vía triggers según el dominio del email

DROP USER IF EXISTS 'admin'@'localhost';
FLUSH PRIVILEGES;

CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON sistemamedico.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;

ALTER TABLE citas ADD COLUMN fecha_proxima_cita DATE;
  