-- 1. Crear Base de Datos (Ejecutar esto primero)
-- CREATE DATABASE almacen_db;

-- 2. Tabla de Usuarios (Para el Login)
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- En producción esto iría encriptado
    rol VARCHAR(20) NOT NULL, -- 'ADMIN' o 'USER'
    nombre_completo VARCHAR(100)
);

-- 3. Tabla de Categorías
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Tabla de Productos
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    fecha_registro DATE DEFAULT CURRENT_DATE,
    categoria_id INT NOT NULL,
    CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id)
);

-- 5. Tabla de Movimientos (Historial)
CREATE TABLE movimientos (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL, -- 'ENTRADA' o 'SALIDA'
    fecha DATE NOT NULL,
    cantidad INT NOT NULL,
    producto_id INT NOT NULL,
    CONSTRAINT fk_producto FOREIGN KEY (producto_id) REFERENCES productos (id)
);

-- DATOS SEMILLA (INICIALES)
INSERT INTO usuarios (username, password, rol, nombre_completo) VALUES
('admin', 'admin123', 'ADMIN', 'Administrador Principal'),
('usuario', 'user123', 'USER', 'Operador de Almacén');

INSERT INTO categorias (nombre) VALUES
('	1ra Categoria'), ('2da Categoria'), ('3ra Categoría');

INSERT INTO productos (nombre, descripcion, precio_unitario, cantidad, fecha_registro, categoria_id) VALUES
('Producto 1', 'Producto número 1', 1500.00, 10, '2025-12-07', 1),
('Producto 2', 'Producto número 2', 1100.00, 10, '2025-12-13', 2),
('Producto 3', 'Producto número 3', 1000.00, 9, '2025-12-14', 3),
('Producto 4', 'Producto número 4', 1700.00, 10, '2025-12-14', 4);