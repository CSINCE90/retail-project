CREATE DATABASE IF NOT EXISTS retailsports_stock;
USE retailsports_stock;

-- Giacenze prodotti
CREATE TABLE stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    available_quantity INT NOT NULL DEFAULT 0,      -- Disponibile per vendita
    reserved_quantity INT NOT NULL DEFAULT 0,       -- Prenotato (ordini pending)
    physical_quantity INT NOT NULL DEFAULT 0,       -- Quantità fisica = available + reserved
    minimum_quantity INT DEFAULT 10,                -- Soglia alert scorte basse
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_available_quantity (available_quantity),
    CHECK (physical_quantity = available_quantity + reserved_quantity),
    CHECK (available_quantity >= 0),
    CHECK (reserved_quantity >= 0)
);

-- Movimenti stock (storico completo)
CREATE TABLE stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    movement_type ENUM('IN', 'OUT', 'RESERVE', 'RELEASE', 'TRANSFER', 'ADJUSTMENT', 'RETURN') NOT NULL,
    quantity INT NOT NULL,                          -- Sempre positivo
    previous_quantity INT NOT NULL,                 -- Quantità prima del movimento
    new_quantity INT NOT NULL,                      -- Quantità dopo il movimento
    reference_type ENUM('ORDER', 'PURCHASE', 'MANUAL', 'TRANSFER', 'RETURN'),
    reference_id BIGINT,                            -- ID ordine/acquisto/etc
    notes TEXT,
    created_by_user_id BIGINT,                      -- Chi ha fatto il movimento
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_movement_type (movement_type),
    INDEX idx_created_at (created_at),
    INDEX idx_reference (reference_type, reference_id)
);

-- Prenotazioni stock (per ordini)
CREATE TABLE stock_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status ENUM('ACTIVE', 'CONFIRMED', 'RELEASED', 'EXPIRED') DEFAULT 'ACTIVE',
    expires_at TIMESTAMP NULL,                      -- Prenotazione scade dopo X minuti
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    released_at TIMESTAMP NULL,
    INDEX idx_product_id (product_id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_expires_at (expires_at)
);

-- Alert scorte basse (log)
CREATE TABLE low_stock_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    available_quantity INT NOT NULL,
    minimum_quantity INT NOT NULL,
    alert_status ENUM('ACTIVE', 'RESOLVED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    INDEX idx_product_id (product_id),
    INDEX idx_alert_status (alert_status)
);