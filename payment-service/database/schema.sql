-- ============================================================================
-- RETAILSPORTS - PAYMENT SERVICE DATABASE SCHEMA
-- Database: retailsports_payment
-- Gestione Ordini e Pagamenti
-- ============================================================================

CREATE DATABASE IF NOT EXISTS retailsports_payment;
USE retailsports_payment;

-- ============================================================================
-- TABELLA ORDERS - Ordini
-- ============================================================================
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,           -- ES: ORD-2025-000001
    user_id BIGINT NOT NULL,
    
    -- Prezzi (in centesimi)
    subtotal_cents BIGINT NOT NULL,                     -- Totale prodotti
    discount_cents BIGINT DEFAULT 0,                    -- Sconto totale
    shipping_cents BIGINT DEFAULT 0,                    -- Costi spedizione
    tax_cents BIGINT DEFAULT 0,                         -- IVA
    total_cents BIGINT NOT NULL,                        -- Totale finale
    
    -- Stati
    status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') 
        DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') 
        DEFAULT 'PENDING',
    
    -- Metodo pagamento
    payment_method ENUM('CREDIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH_ON_DELIVERY'),
    
    -- Indirizzi (snapshot al momento dell'ordine)
    shipping_address_line1 VARCHAR(255) NOT NULL,
    shipping_address_line2 VARCHAR(255),
    shipping_city VARCHAR(100) NOT NULL,
    shipping_state VARCHAR(100),
    shipping_postal_code VARCHAR(20) NOT NULL,
    shipping_country VARCHAR(100) NOT NULL,
    
    billing_address_line1 VARCHAR(255) NOT NULL,
    billing_address_line2 VARCHAR(255),
    billing_city VARCHAR(100) NOT NULL,
    billing_state VARCHAR(100),
    billing_postal_code VARCHAR(20) NOT NULL,
    billing_country VARCHAR(100) NOT NULL,
    
    -- Note
    customer_notes TEXT,                                 -- Note cliente
    admin_notes TEXT,                                    -- Note interne
    
    -- Tracking
    tracking_number VARCHAR(100),                        -- Numero tracking spedizione
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    shipped_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    
    INDEX idx_order_number (order_number),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABELLA ORDER_ITEMS - Prodotti nell'ordine
-- ============================================================================
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    
    -- Prodotto (snapshot al momento dell'ordine)
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(100),
    product_image VARCHAR(500),
    
    -- Quantità e prezzi (in centesimi)
    quantity INT NOT NULL,
    unit_price_cents BIGINT NOT NULL,                   -- Prezzo unitario
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    discount_cents BIGINT DEFAULT 0,                    -- Sconto per item
    subtotal_cents BIGINT NOT NULL,                     -- unit_price * quantity
    total_cents BIGINT NOT NULL,                        -- subtotal - discount
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABELLA PAYMENTS - Pagamenti
-- ============================================================================
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    
    -- Dettagli pagamento
    payment_method ENUM('CREDIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH_ON_DELIVERY') NOT NULL,
    amount_cents BIGINT NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    
    -- Stato
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    
    -- Gateway esterno (per future integrazioni Stripe/PayPal)
    transaction_id VARCHAR(255),                         -- ID transazione gateway
    payment_gateway VARCHAR(50),                         -- stripe, paypal, etc.
    
    -- Dettagli carta (se carta di credito - salvare tokenizzati!)
    card_last4 VARCHAR(4),                              -- Ultime 4 cifre
    card_brand VARCHAR(50),                             -- Visa, Mastercard, etc.
    
    -- Note e errori
    notes TEXT,
    error_message TEXT,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    failed_at TIMESTAMP NULL,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABELLA ORDER_STATUS_HISTORY - Storico stati ordine
-- ============================================================================
CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    
    -- Stati
    old_status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED'),
    new_status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') NOT NULL,
    
    -- Chi ha effettuato il cambio
    changed_by_user_id BIGINT,                          -- NULL = sistema
    changed_by_admin BOOLEAN DEFAULT FALSE,
    
    -- Note
    notes TEXT,
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- DATI INIZIALI DI TEST (OPZIONALE)
-- ============================================================================

-- Nota: Gli ordini verranno creati dinamicamente tramite l'applicazione
-- dopo che l'utente completa il checkout dal carrello

-- ============================================================================
-- FINE SCHEMA
-- ============================================================================