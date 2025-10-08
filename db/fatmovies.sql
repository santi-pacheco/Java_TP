-- 1. ELIMINAR Y CREAR BASE DE DATOS
--------------------------------------------------------------------------------
-- Elimina la base de datos si existe para asegurar una instalación limpia.
DROP DATABASE IF EXISTS fatmovies;

-- Crea la base de datos.
CREATE DATABASE fatmovies CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usa la base de datos creada para que las siguientes sentencias la afecten.
USE fatmovies;
-- Creación de la tabla 'Estreno' (MODIFICADA)
-- Ahora tiene un ID (PK) y un ANIO como atributo separado.
CREATE TABLE estrenos (
    id_estreno INT PRIMARY KEY AUTO_INCREMENT, -- Nuevo ID como clave primaria
    anio INT NOT NULL UNIQUE                  -- El año ahora es un atributo, único y no nulo
);

----------------------------------------------------------------------------------------------------

-- Creación de la tabla 'Genero' (MODIFICADA)
-- Ahora incluye un 'id_api'.
CREATE TABLE generos (
    id_genero INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    id_api INT                                -- Nuevo campo para el ID de una API externa
);

----------------------------------------------------------------------------------------------------

-- Creación de la tabla 'Peliculas' (MODIFICADA)
-- La clave foránea apunta al ID de 'Estreno' (id_estreno) en lugar del año.
CREATE TABLE peliculas (
    id_pelicula INT PRIMARY KEY AUTO_INCREMENT,
    id_api INT,                                 -- ID externo de la película (API)
    name VARCHAR(255) NOT NULL,
    puntuacionProm DOUBLE NULL,
    sinopsis TEXT NULL,
    duracion TIME NULL,
    adulto BOOLEAN NULL,
    titulo_original VARCHAR(255) NULL,
    puntuacion_api DOUBLE NULL,
    idioma_original VARCHAR(10) NULL,
    poster_path VARCHAR(255) NULL,
    
    -- Columna para la relación con 'Estreno' (Ahora es el ID de la tabla Estreno)
    id_estreno INT NOT NULL,
    
    -- Clave foránea que referencia al ID de la tabla 'Estreno'
    FOREIGN KEY (id_estreno) REFERENCES estrenos(id_estreno)
);

----------------------------------------------------------------------------------------------------

-- Creación de la tabla intermedia 'genero_pelicula' (SIN CAMBIOS)
-- Para la relación N:M entre 'Genero' y 'Peliculas'.
CREATE TABLE generos_speliculas (
    id_pelicula INT NOT NULL,
    id_genero INT NOT NULL,
    
    PRIMARY KEY (id_pelicula, id_genero),
    
    FOREIGN KEY (id_pelicula) REFERENCES peliculas(id_pelicula) ON DELETE CASCADE,
    FOREIGN KEY (id_genero) REFERENCES generos(id_genero) ON DELETE CASCADE
);

CREATE TABLE personas (
    id_persona INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    birthdate DATE NOT NULL
);

CREATE TABLE usuarios (
    id_user INT PRIMARY KEY AUTO_INCREMENT, -- Clave Primaria, entero y se auto-incrementa.
    username VARCHAR(50) NOT NULL UNIQUE,  -- Nombre de usuario, cadena no nula y único.
    password VARCHAR(255) NOT NULL,            -- Contraseña (usar CHAR(60) o VARCHAR(255) para almacenar hashes seguros).
    role VARCHAR(20) NOT NULL,             -- Rol del usuario (e.g., 'ADMIN', 'USER').
    email VARCHAR(100) NOT NULL UNIQUE,    -- Correo electrónico, cadena no nula y único.
    birthDate DATE                         -- Fecha de nacimiento (solo fecha, no hora).
);