-- ============================================
-- RetailSports - Product Service Database Schema
-- ============================================
-- Versione: 1.0
-- Database: MySQL 8.0+
-- Charset: utf8mb4 (supporto emoji e caratteri speciali)
-- ============================================

CREATE DATABASE IF NOT EXISTS retailsports_products 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE retailsports_products;

-- ============================================
-- TABELLA CATEGORIE
-- ============================================
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Dati categoria
    name VARCHAR(100) UNIQUE NOT NULL COMMENT 'Nome categoria (es. Calcio, Tennis)',
    slug VARCHAR(100) UNIQUE NOT NULL COMMENT 'URL-friendly name',
    description TEXT COMMENT 'Descrizione categoria',
    
    -- Gerarchia categorie (self-referencing)
    parent_id BIGINT NULL COMMENT 'Categoria padre (NULL = categoria root)',
    
    -- Ordinamento e visualizzazione
    display_order INT DEFAULT 0 COMMENT 'Ordine di visualizzazione',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Categoria attiva/disattiva',
    
    -- SEO
    meta_title VARCHAR(200),
    meta_description VARCHAR(500),
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    
    INDEX idx_slug (slug),
    INDEX idx_parent (parent_id),
    INDEX idx_active (is_active),
    INDEX idx_order (display_order)
) ENGINE=InnoDB COMMENT='Categorie prodotti (gerarchiche)';

-- ============================================
-- TABELLA BRAND/MARCHE
-- ============================================
CREATE TABLE brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Dati brand
    name VARCHAR(100) UNIQUE NOT NULL COMMENT 'Nome brand (es. Nike, Adidas)',
    slug VARCHAR(100) UNIQUE NOT NULL COMMENT 'URL-friendly name',
    description TEXT,
    logo_url VARCHAR(500) COMMENT 'URL logo brand',
    website_url VARCHAR(255) COMMENT 'Sito web ufficiale',
    
    -- Stato
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Brand attivo/disattivo',
    
    -- SEO
    meta_title VARCHAR(200),
    meta_description VARCHAR(500),
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_slug (slug),
    INDEX idx_active (is_active),
    INDEX idx_name (name)
) ENGINE=InnoDB COMMENT='Brand/Marche prodotti';

-- ============================================
-- TABELLA PRODOTTI (con soft delete)
-- ============================================
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Codici identificativi
    sku VARCHAR(50) UNIQUE NOT NULL COMMENT 'Stock Keeping Unit - codice univoco',
    barcode VARCHAR(50) UNIQUE COMMENT 'Codice a barre (EAN-13, UPC, etc.)',
    
    -- Dati base
    name VARCHAR(255) NOT NULL COMMENT 'Nome prodotto',
    slug VARCHAR(255) UNIQUE NOT NULL COMMENT 'URL-friendly name',
    description TEXT COMMENT 'Descrizione breve',
    long_description LONGTEXT COMMENT 'Descrizione dettagliata (HTML)',
    
    -- Relazioni
    category_id BIGINT NOT NULL,
    brand_id BIGINT,
    
    -- Prezzi (in centesimi per evitare problemi float)
    price_cents INT NOT NULL COMMENT 'Prezzo in centesimi (es. 9999 = 99.99€)',
    compare_at_price_cents INT COMMENT 'Prezzo di listino (per sconti)',
    cost_price_cents INT COMMENT 'Costo di acquisto',
    
    -- Caratteristiche fisiche
    weight_grams INT COMMENT 'Peso in grammi',
    length_cm DECIMAL(10,2) COMMENT 'Lunghezza in cm',
    width_cm DECIMAL(10,2) COMMENT 'Larghezza in cm',
    height_cm DECIMAL(10,2) COMMENT 'Altezza in cm',
    
    -- Inventario (snapshot - il dettaglio è in Stock Service)
    stock_quantity INT DEFAULT 0 COMMENT 'Quantità disponibile (cache)',
    low_stock_threshold INT DEFAULT 10 COMMENT 'Soglia scorte basse',
    track_inventory BOOLEAN DEFAULT TRUE COMMENT 'Traccia inventario',
    
    -- Stato prodotto
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Prodotto attivo/disattivo',
    is_featured BOOLEAN DEFAULT FALSE COMMENT 'Prodotto in evidenza',
    is_new BOOLEAN DEFAULT FALSE COMMENT 'Nuovo arrivo',
    is_on_sale BOOLEAN DEFAULT FALSE COMMENT 'In promozione',
    
    -- SEO
    meta_title VARCHAR(200),
    meta_description VARCHAR(500),
    meta_keywords VARCHAR(500),
    
    -- Statistiche (denormalized per performance)
    views_count INT DEFAULT 0 COMMENT 'Visualizzazioni prodotto',
    sales_count INT DEFAULT 0 COMMENT 'Numero vendite',
    rating_average DECIMAL(3,2) DEFAULT 0.00 COMMENT 'Media recensioni (0-5)',
    rating_count INT DEFAULT 0 COMMENT 'Numero recensioni',
    
    -- Soft delete
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp',
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL,
    
    INDEX idx_sku (sku),
    INDEX idx_slug (slug),
    INDEX idx_category (category_id),
    INDEX idx_brand (brand_id),
    INDEX idx_active (is_active),
    INDEX idx_featured (is_featured),
    INDEX idx_deleted (deleted_at),
    INDEX idx_price (price_cents),
    INDEX idx_name (name),
    FULLTEXT INDEX idx_search (name, description)
) ENGINE=InnoDB COMMENT='Prodotti catalogo';

-- ============================================
-- TABELLA IMMAGINI PRODOTTO
-- ============================================
CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    
    -- URL immagine
    image_url VARCHAR(500) NOT NULL COMMENT 'URL immagine',
    alt_text VARCHAR(255) COMMENT 'Testo alternativo',
    
    -- Ordinamento
    display_order INT DEFAULT 0 COMMENT 'Ordine visualizzazione',
    is_primary BOOLEAN DEFAULT FALSE COMMENT 'Immagine principale',
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    
    INDEX idx_product (product_id),
    INDEX idx_primary (is_primary),
    INDEX idx_order (display_order)
) ENGINE=InnoDB COMMENT='Immagini prodotti';

-- ============================================
-- TABELLA ATTRIBUTI PRODOTTO (es. colore, taglia)
-- ============================================
CREATE TABLE product_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Nome attributo
    name VARCHAR(50) UNIQUE NOT NULL COMMENT 'Nome attributo (es. Colore, Taglia)',
    display_name VARCHAR(100) NOT NULL COMMENT 'Nome visualizzato',
    
    -- Tipo attributo
    type ENUM('COLOR', 'SIZE', 'MATERIAL', 'CUSTOM') DEFAULT 'CUSTOM',
    
    -- Ordinamento
    display_order INT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Definizione attributi prodotto';

-- ============================================
-- TABELLA VALORI ATTRIBUTI
-- ============================================
CREATE TABLE attribute_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attribute_id BIGINT NOT NULL,
    
    -- Valore
    value VARCHAR(100) NOT NULL COMMENT 'Valore attributo (es. Rosso, XL)',
    display_value VARCHAR(100) NOT NULL COMMENT 'Valore visualizzato',
    
    -- Per colori: hex code
    color_hex VARCHAR(7) COMMENT 'Codice colore HEX (es. #FF0000)',
    
    -- Ordinamento
    display_order INT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (attribute_id) REFERENCES product_attributes(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_attr_value (attribute_id, value),
    INDEX idx_attribute (attribute_id)
) ENGINE=InnoDB COMMENT='Valori possibili per ogni attributo';

-- ============================================
-- TABELLA ASSOCIAZIONE PRODOTTI-ATTRIBUTI (Many-to-Many)
-- ============================================
CREATE TABLE product_attribute_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    attribute_value_id BIGINT NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (attribute_value_id) REFERENCES attribute_values(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_product_attr (product_id, attribute_value_id),
    INDEX idx_product (product_id),
    INDEX idx_attr_value (attribute_value_id)
) ENGINE=InnoDB COMMENT='Attributi assegnati ai prodotti';

-- ============================================
-- TABELLA TAGS PRODOTTO
-- ============================================
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    name VARCHAR(50) UNIQUE NOT NULL COMMENT 'Nome tag',
    slug VARCHAR(50) UNIQUE NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_slug (slug),
    INDEX idx_name (name)
) ENGINE=InnoDB COMMENT='Tags per categorizzazione libera';

-- ============================================
-- TABELLA ASSOCIAZIONE PRODOTTI-TAGS (Many-to-Many)
-- ============================================
CREATE TABLE product_tags (
    product_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (product_id, tag_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    
    INDEX idx_product (product_id),
    INDEX idx_tag (tag_id)
) ENGINE=InnoDB COMMENT='Associazione prodotti e tags';

-- ============================================
-- TABELLA SCONTI/PROMOZIONI
-- ============================================
CREATE TABLE discounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Dati sconto
    name VARCHAR(100) NOT NULL COMMENT 'Nome promozione',
    code VARCHAR(50) UNIQUE COMMENT 'Codice sconto (opzionale)',
    description TEXT,
    
    -- Tipo sconto
    type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL DEFAULT 'PERCENTAGE',
    value INT NOT NULL COMMENT 'Valore sconto (percentuale o centesimi)',
    
    -- Validità temporale
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    
    -- Limiti utilizzo
    max_uses INT COMMENT 'Utilizzi massimi totali',
    max_uses_per_user INT DEFAULT 1 COMMENT 'Utilizzi massimi per utente',
    current_uses INT DEFAULT 0 COMMENT 'Utilizzi attuali',
    
    -- Requisiti minimi
    min_purchase_amount_cents INT COMMENT 'Importo minimo acquisto',
    
    -- Stato
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_code (code),
    INDEX idx_active (is_active),
    INDEX idx_dates (starts_at, ends_at)
) ENGINE=InnoDB COMMENT='Sconti e promozioni';

-- ============================================
-- TABELLA ASSOCIAZIONE PRODOTTI-SCONTI
-- ============================================
CREATE TABLE product_discounts (
    product_id BIGINT NOT NULL,
    discount_id BIGINT NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (product_id, discount_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Sconti applicati ai prodotti';

-- ============================================
-- INSERIMENTO DATI INIZIALI
-- ============================================

-- Categorie principali
INSERT INTO categories (name, slug, description, parent_id, display_order) VALUES
('Calcio', 'calcio', 'Articoli per il calcio', NULL, 1),
('Tennis', 'tennis', 'Articoli per il tennis', NULL, 2),
('Running', 'running', 'Articoli per la corsa', NULL, 3),
('Palestra', 'palestra', 'Articoli per la palestra', NULL, 4),
('Basket', 'basket', 'Articoli per il basket', NULL, 5),
('Nuoto', 'nuoto', 'Articoli per il nuoto', NULL, 6);

-- Sottocategorie Calcio
INSERT INTO categories (name, slug, description, parent_id, display_order) VALUES
('Scarpe da calcio', 'scarpe-calcio', 'Scarpe e scarpini da calcio', 1, 1),
('Palloni', 'palloni-calcio', 'Palloni da calcio', 1, 2),
('Abbigliamento calcio', 'abbigliamento-calcio', 'Maglie, pantaloncini, calzettoni', 1, 3),
('Parastinchi', 'parastinchi', 'Protezioni per le gambe', 1, 4);

-- Brand principali
INSERT INTO brands (name, slug, description, logo_url, website_url) VALUES
('Nike', 'nike', 'Just Do It', 'https://example.com/logos/nike.png', 'https://www.nike.com'),
('Adidas', 'adidas', 'Impossible is Nothing', 'https://example.com/logos/adidas.png', 'https://www.adidas.com'),
('Puma', 'puma', 'Forever Faster', 'https://example.com/logos/puma.png', 'https://www.puma.com'),
('Under Armour', 'under-armour', 'I Will', 'https://example.com/logos/ua.png', 'https://www.underarmour.com'),
('New Balance', 'new-balance', 'Fearlessly Independent', 'https://example.com/logos/nb.png', 'https://www.newbalance.com'),
('Asics', 'asics', 'Sound Mind, Sound Body', 'https://example.com/logos/asics.png', 'https://www.asics.com');

-- Attributi standard
INSERT INTO product_attributes (name, display_name, type, display_order) VALUES
('color', 'Colore', 'COLOR', 1),
('size', 'Taglia', 'SIZE', 2),
('material', 'Materiale', 'MATERIAL', 3),
('gender', 'Genere', 'CUSTOM', 4);

-- Valori per Colore
INSERT INTO attribute_values (attribute_id, value, display_value, color_hex, display_order) VALUES
(1, 'black', 'Nero', '#000000', 1),
(1, 'white', 'Bianco', '#FFFFFF', 2),
(1, 'red', 'Rosso', '#FF0000', 3),
(1, 'blue', 'Blu', '#0000FF', 4),
(1, 'green', 'Verde', '#00FF00', 5),
(1, 'yellow', 'Giallo', '#FFFF00', 6);

-- Valori per Taglia
INSERT INTO attribute_values (attribute_id, value, display_value, display_order) VALUES
(2, 'xs', 'XS', 1),
(2, 's', 'S', 2),
(2, 'm', 'M', 3),
(2, 'l', 'L', 4),
(2, 'xl', 'XL', 5),
(2, 'xxl', 'XXL', 6);

-- Valori per Genere
INSERT INTO attribute_values (attribute_id, value, display_value, display_order) VALUES
(4, 'male', 'Uomo', 1),
(4, 'female', 'Donna', 2),
(4, 'unisex', 'Unisex', 3),
(4, 'kids', 'Bambino', 4);

-- Prodotti di esempio
INSERT INTO products (sku, name, slug, description, category_id, brand_id, price_cents, compare_at_price_cents, stock_quantity, is_active, is_featured) VALUES
('NK-AIR-001', 'Nike Air Zoom Mercurial', 'nike-air-zoom-mercurial', 'Scarpe da calcio professionali con tecnologia Air Zoom', 7, 1, 15999, 19999, 50, TRUE, TRUE),
('AD-PRED-002', 'Adidas Predator Elite', 'adidas-predator-elite', 'Scarpe da calcio con controllo di palla superiore', 7, 2, 18999, 22999, 30, TRUE, TRUE),
('NK-BALL-003', 'Nike Flight Official', 'nike-flight-official', 'Pallone da calcio ufficiale Serie A', 8, 1, 12999, NULL, 100, TRUE, FALSE);

-- Immagini prodotti di esempio
INSERT INTO product_images (product_id, image_url, alt_text, display_order, is_primary) VALUES
(1, 'https://example.com/products/nike-mercurial-1.jpg', 'Nike Mercurial - Vista frontale', 0, TRUE),
(1, 'https://example.com/products/nike-mercurial-2.jpg', 'Nike Mercurial - Vista laterale', 1, FALSE),
(2, 'https://example.com/products/adidas-predator-1.jpg', 'Adidas Predator - Vista frontale', 0, TRUE),
(3, 'https://example.com/products/nike-ball-1.jpg', 'Nike Flight - Pallone', 0, TRUE);

-- Tags di esempio
INSERT INTO tags (name, slug) VALUES
('novità', 'novita'),
('best-seller', 'best-seller'),
('eco-friendly', 'eco-friendly'),
('limited-edition', 'limited-edition');

-- ============================================
-- VIEW: Prodotti attivi (esclude soft deleted)
-- ============================================
CREATE OR REPLACE VIEW active_products AS
SELECT 
    p.id, p.sku, p.name, p.slug, p.description,
    p.category_id, c.name AS category_name,
    p.brand_id, b.name AS brand_name,
    p.price_cents, p.compare_at_price_cents,
    p.stock_quantity, p.is_featured, p.is_new, p.is_on_sale,
    p.rating_average, p.rating_count,
    p.created_at, p.updated_at
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN brands b ON p.brand_id = b.id
WHERE p.deleted_at IS NULL AND p.is_active = TRUE;

-- ============================================
-- VIEW: Prodotti con categoria completa
-- ============================================
CREATE OR REPLACE VIEW products_with_full_category AS
SELECT 
    p.id, p.sku, p.name, p.price_cents,
    c1.name AS category_name,
    c2.name AS parent_category_name,
    b.name AS brand_name
FROM products p
LEFT JOIN categories c1 ON p.category_id = c1.id
LEFT JOIN categories c2 ON c1.parent_id = c2.id
LEFT JOIN brands b ON p.brand_id = b.id
WHERE p.deleted_at IS NULL;

-- ============================================
-- VIEW: Prodotti in sconto
-- ============================================
CREATE OR REPLACE VIEW products_on_sale AS
SELECT 
    p.id, p.sku, p.name, p.price_cents, p.compare_at_price_cents,
    ROUND((p.compare_at_price_cents - p.price_cents) * 100 / p.compare_at_price_cents) AS discount_percentage
FROM products p
WHERE p.deleted_at IS NULL 
  AND p.is_active = TRUE 
  AND p.is_on_sale = TRUE
  AND p.compare_at_price_cents IS NOT NULL
  AND p.compare_at_price_cents > p.price_cents;

-- ============================================
-- STORED PROCEDURE: Soft Delete Product
-- ============================================
DELIMITER //

CREATE PROCEDURE soft_delete_product(IN p_product_id BIGINT)
BEGIN
    UPDATE products 
    SET deleted_at = NOW(), 
        is_active = FALSE 
    WHERE id = p_product_id 
      AND deleted_at IS NULL;
      
    SELECT ROW_COUNT() AS affected_rows;
END //

-- ============================================
-- STORED PROCEDURE: Restore Product
-- ============================================
CREATE PROCEDURE restore_product(IN p_product_id BIGINT)
BEGIN
    UPDATE products 
    SET deleted_at = NULL, 
        is_active = TRUE 
    WHERE id = p_product_id 
      AND deleted_at IS NOT NULL;
      
    SELECT ROW_COUNT() AS affected_rows;
END //

-- ============================================
-- STORED PROCEDURE: Update Product Stock
-- ============================================
CREATE PROCEDURE update_product_stock(
    IN p_product_id BIGINT,
    IN p_quantity_change INT
)
BEGIN
    UPDATE products 
    SET stock_quantity = stock_quantity + p_quantity_change,
        updated_at = NOW()
    WHERE id = p_product_id;
    
    SELECT id, sku, name, stock_quantity 
    FROM products 
    WHERE id = p_product_id;
END //

-- ============================================
-- STORED PROCEDURE: Increment Product Views
-- ============================================
CREATE PROCEDURE increment_product_views(IN p_product_id BIGINT)
BEGIN
    UPDATE products 
    SET views_count = views_count + 1 
    WHERE id = p_product_id;
END //

-- ============================================
-- STORED PROCEDURE: Update Product Rating
-- ============================================
CREATE PROCEDURE update_product_rating(
    IN p_product_id BIGINT,
    IN p_new_rating DECIMAL(3,2)
)
BEGIN
    DECLARE current_avg DECIMAL(3,2);
    DECLARE current_count INT;
    DECLARE new_avg DECIMAL(3,2);
    
    SELECT rating_average, rating_count 
    INTO current_avg, current_count
    FROM products 
    WHERE id = p_product_id;
    
    SET new_avg = ((current_avg * current_count) + p_new_rating) / (current_count + 1);
    
    UPDATE products 
    SET rating_average = new_avg,
        rating_count = rating_count + 1
    WHERE id = p_product_id;
    
    SELECT id, name, rating_average, rating_count 
    FROM products 
    WHERE id = p_product_id;
END //

-- ============================================
-- STORED PROCEDURE: Get Low Stock Products
-- ============================================
CREATE PROCEDURE get_low_stock_products()
BEGIN
    SELECT 
        id, sku, name, stock_quantity, low_stock_threshold
    FROM products
    WHERE deleted_at IS NULL
      AND is_active = TRUE
      AND track_inventory = TRUE
      AND stock_quantity <= low_stock_threshold
    ORDER BY stock_quantity ASC;
END //

-- ============================================
-- STORED PROCEDURE: Cleanup Expired Discounts
-- ============================================
CREATE PROCEDURE cleanup_expired_discounts()
BEGIN
    UPDATE discounts 
    SET is_active = FALSE 
    WHERE ends_at < NOW() 
      AND is_active = TRUE;
    
    SELECT ROW_COUNT() AS deactivated_discounts;
END //

DELIMITER ;

-- ============================================
-- EVENT: Pulizia automatica sconti scaduti (ogni giorno alle 3:00)
-- ============================================
CREATE EVENT IF NOT EXISTS cleanup_discounts_daily
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY + INTERVAL 3 HOUR)
DO
    CALL cleanup_expired_discounts();

-- ============================================
-- TRIGGER: Aggiorna is_on_sale quando c'è compare_at_price
-- ============================================
DELIMITER //

CREATE TRIGGER set_on_sale_flag
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    IF NEW.compare_at_price_cents IS NOT NULL 
       AND NEW.compare_at_price_cents > NEW.price_cents THEN
        SET NEW.is_on_sale = TRUE;
    ELSE
        SET NEW.is_on_sale = FALSE;
    END IF;
END //

DELIMITER ;

-- ============================================
-- STATISTICHE FINALI
-- ============================================
SELECT 'Database retailsports_products creato con successo!' AS status;

SELECT 
    'Tabelle create:' AS info,
    COUNT(*) AS totale
FROM information_schema.tables 
WHERE table_schema = 'retailsports_products' 
  AND table_type = 'BASE TABLE';

SELECT 
    'Categorie inserite:' AS info,
    COUNT(*) AS totale
FROM categories;

SELECT 
    'Brand inseriti:' AS info,
    COUNT(*) AS totale
FROM brands;

SELECT 
    'Prodotti di esempio:' AS info,
    COUNT(*) AS totale
FROM products;

-- ============================================
-- FINE SCRIPT
-- ============================================
