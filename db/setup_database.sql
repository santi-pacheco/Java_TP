-- Script para configurar la base de datos FatMovies

-- 1. Crear la base de datos (ejecutar como administrador)
CREATE DATABASE IF NOT EXISTS fatmovies 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. Usar la base de datos
USE fatmovies;

-- 3. Crear la tabla de géneros
CREATE TABLE IF NOT EXISTS genres (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Insertar algunos géneros de ejemplo (opcional)
INSERT IGNORE INTO genres (id, name) VALUES
(28, 'Action'),
(12, 'Adventure'),
(16, 'Animation'),
(35, 'Comedy'),
(80, 'Crime'),
(99, 'Documentary'),
(18, 'Drama'),
(10751, 'Family'),
(14, 'Fantasy'),
(36, 'History'),
(27, 'Horror'),
(10402, 'Music'),
(9648, 'Mystery'),
(10749, 'Romance'),
(878, 'Science Fiction'),
(10770, 'TV Movie'),
(53, 'Thriller'),
(10752, 'War'),
(37, 'Western');

-- 5. Verificar que la tabla se creó correctamente
DESCRIBE genres;

-- 6. Mostrar los datos insertados
SELECT * FROM genres ORDER BY name;