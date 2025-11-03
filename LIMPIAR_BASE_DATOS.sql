-- SCRIPT PARA LIMPIAR Y RECREAR LA BASE DE DATOS

-- 1. Eliminar la base de datos (si existe)
DROP DATABASE IF EXISTS ecommerce_db;

-- 2. Crear la base de datos nuevamente
CREATE DATABASE ecommerce_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 3. Usar la base de datos
USE ecommerce_db;

-- Listo! Ahora Spring Boot creará las tablas automáticamente al iniciar
