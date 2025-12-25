-- ============================================
-- RetailSports - User Service Database Schema
-- ============================================
-- Versione: 1.0
-- Database: MySQL 8.0+
-- Charset: utf8mb4 (supporto emoji e caratteri speciali)
-- ============================================

CREATE DATABASE IF NOT EXISTS retailsports_users 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE retailsports_users;

-- ============================================
-- TABELLA UTENTI (con soft delete)
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Credenziali
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    
    -- Dati personali
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    
    -- Stato account
    enabled BOOLEAN DEFAULT TRUE COMMENT 'Account attivo/disattivato',
    email_verified BOOLEAN DEFAULT FALSE COMMENT 'Email verificata',
    
    -- Soft delete
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp',
    
    -- Timestamp audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indici per performance
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_deleted (deleted_at),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB COMMENT='Tabella principale utenti';

-- ============================================
-- TABELLA RUOLI
-- ============================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL,
    description VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Ruoli sistema (ROLE_USER, ROLE_ADMIN, etc.)';

-- ============================================
-- TABELLA ASSOCIAZIONE UTENTI-RUOLI (Many-to-Many)
-- ============================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    
    INDEX idx_user (user_id),
    INDEX idx_role (role_id)
) ENGINE=InnoDB COMMENT='Associazione utenti e ruoli';

-- ============================================
-- TABELLA INDIRIZZI
-- ============================================
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    -- Tipo indirizzo
    type ENUM('SHIPPING', 'BILLING') NOT NULL DEFAULT 'SHIPPING',
    
    -- Dati indirizzo completi
    address_line1 VARCHAR(255) NOT NULL COMMENT 'Via e numero civico',
    address_line2 VARCHAR(255) COMMENT 'Interno, scala, piano',
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) COMMENT 'Provincia/Regione',
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(2) DEFAULT 'IT' COMMENT 'Codice ISO 3166-1 alpha-2',
    
    -- Flag
    is_default BOOLEAN DEFAULT FALSE COMMENT 'Indirizzo predefinito',
    
    -- Timestamp
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_default (is_default)
) ENGINE=InnoDB COMMENT='Indirizzi di spedizione e fatturazione';

-- ============================================
-- TABELLA REFRESH TOKENS (per JWT)
-- ============================================
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    token VARCHAR(500) UNIQUE NOT NULL COMMENT 'Refresh token UUID',
    expires_at TIMESTAMP NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires (expires_at)
) ENGINE=InnoDB COMMENT='Refresh tokens per autenticazione JWT';

-- ============================================
-- TABELLA VERIFICATION TOKENS
-- ============================================
CREATE TABLE verification_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    token VARCHAR(500) UNIQUE NOT NULL COMMENT 'Token UUID',
    type ENUM('EMAIL_VERIFICATION', 'PASSWORD_RESET') NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE COMMENT 'Token già utilizzato',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP NULL DEFAULT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_expires (expires_at)
) ENGINE=InnoDB COMMENT='Token per verifica email e reset password';

-- ============================================
-- TABELLA LOGIN AUDIT (storico accessi)
-- ============================================
CREATE TABLE login_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    
    -- Dettagli login
    username VARCHAR(50) NOT NULL COMMENT 'Username utilizzato (anche se fallito)',
    success BOOLEAN NOT NULL COMMENT 'Login riuscito o fallito',
    ip_address VARCHAR(45) COMMENT 'IPv4 o IPv6',
    user_agent VARCHAR(500) COMMENT 'Browser/client info',
    failure_reason VARCHAR(100) COMMENT 'Motivo fallimento (se success=false)',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_created (created_at),
    INDEX idx_success (success)
) ENGINE=InnoDB COMMENT='Audit log degli accessi al sistema';

-- ============================================
-- INSERIMENTO DATI INIZIALI
-- ============================================

-- Ruoli base del sistema
INSERT INTO roles (name, description) VALUES 
('ROLE_USER', 'Cliente standard con accesso base'),
('ROLE_ADMIN', 'Amministratore con accesso completo'),
('ROLE_EMPLOYEE', 'Dipendente negozio con accesso gestionale');

-- ============================================
-- UTENTE ADMIN DI DEFAULT (per testing)
-- ============================================
-- Password: Admin123! (BCrypt hash)
-- IMPORTANTE: Cambiare in produzione!
INSERT INTO users (username, email, password, first_name, last_name, enabled, email_verified) 
VALUES (
    'admin',
    'admin@retailsports.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye7.N8IAKp.7jz5J5P5P5P5P5P5P5P5P5', -- Placeholder hash
    'Admin',
    'System',
    TRUE,
    TRUE
);

-- Assegna ruolo ADMIN all'utente admin
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- ============================================
-- VIEW: Utenti attivi (esclude soft deleted)
-- ============================================
CREATE OR REPLACE VIEW active_users AS
SELECT 
    id, username, email, first_name, last_name, phone,
    enabled, email_verified, created_at, updated_at
FROM users
WHERE deleted_at IS NULL;

-- ============================================
-- VIEW: Utenti con ruoli
-- ============================================
CREATE OR REPLACE VIEW users_with_roles AS
SELECT 
    u.id, u.username, u.email, u.first_name, u.last_name,
    u.enabled, u.email_verified, u.deleted_at,
    GROUP_CONCAT(r.name ORDER BY r.name SEPARATOR ', ') AS roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id;

-- ============================================
-- STORED PROCEDURE: Soft Delete User
-- ============================================
DELIMITER //

CREATE PROCEDURE soft_delete_user(IN p_user_id BIGINT)
BEGIN
    UPDATE users 
    SET deleted_at = NOW(), 
        enabled = FALSE 
    WHERE id = p_user_id 
      AND deleted_at IS NULL;
      
    SELECT ROW_COUNT() AS affected_rows;
END //

-- ============================================
-- STORED PROCEDURE: Restore User
-- ============================================
CREATE PROCEDURE restore_user(IN p_user_id BIGINT)
BEGIN
    UPDATE users 
    SET deleted_at = NULL, 
        enabled = TRUE 
    WHERE id = p_user_id 
      AND deleted_at IS NOT NULL;
      
    SELECT ROW_COUNT() AS affected_rows;
END //

-- ============================================
-- STORED PROCEDURE: Cleanup expired tokens
-- ============================================
CREATE PROCEDURE cleanup_expired_tokens()
BEGIN
    DELETE FROM refresh_tokens WHERE expires_at < NOW();
    DELETE FROM verification_tokens WHERE expires_at < NOW() AND used = FALSE;
    
    SELECT 
        (SELECT COUNT(*) FROM refresh_tokens) AS active_refresh_tokens,
        (SELECT COUNT(*) FROM verification_tokens WHERE used = FALSE) AS active_verification_tokens;
END //

DELIMITER ;

-- ============================================
-- EVENT: Pulizia automatica token scaduti (ogni giorno alle 3:00)
-- ============================================
CREATE EVENT IF NOT EXISTS cleanup_tokens_daily
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY + INTERVAL 3 HOUR)
DO
    CALL cleanup_expired_tokens();

-- ============================================
-- TRIGGER: Aggiorna updated_at automaticamente
-- ============================================
-- (MySQL già lo fa con ON UPDATE CURRENT_TIMESTAMP, ma lo lasciamo documentato)

-- ============================================
-- STATISTICHE FINALI
-- ============================================
SELECT 'Database retailsports_users creato con successo!' AS status;

SELECT 
    'Tabelle create:' AS info,
    COUNT(*) AS totale
FROM information_schema.tables 
WHERE table_schema = 'retailsports_users' 
  AND table_type = 'BASE TABLE';

SELECT 
    'Ruoli inseriti:' AS info,
    COUNT(*) AS totale
FROM roles;

SELECT 
    'Utenti di sistema:' AS info,
    COUNT(*) AS totale
FROM users;

-- ============================================
-- FINE SCRIPT
-- ============================================
