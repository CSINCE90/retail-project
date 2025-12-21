# 👤 User Service - RetailSports Microservices

**User Authentication and Management Service**

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![Eureka](https://img.shields.io/badge/Eureka-Client-green?style=flat-square)

---

## 📋 Descrizione

Il **User Service** è il microservizio responsabile della gestione completa degli utenti nella piattaforma e-commerce RetailSports. Gestisce autenticazione, autorizzazione, profili utente e controllo degli accessi.

### 🎯 Responsabilità principali:
- ✅ Registrazione e login utenti
- ✅ Autenticazione JWT (Access + Refresh Token)
- ✅ Gestione profili utente
- ✅ Sistema di ruoli e permessi (RBAC)
- ✅ Verifica email
- ✅ Reset password
- ✅ Gestione indirizzi di spedizione/fatturazione
- ✅ Audit log degli accessi
- ✅ Soft delete per compliance GDPR

---

## 🏗️ Architettura

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway (8080)                      │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   User Service (8081)                       │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                           │
│    ├── AuthController (login, register, refresh)           │
│    ├── UserController (CRUD, profile)                      │
│    └── AddressController (indirizzi)                       │
├─────────────────────────────────────────────────────────────┤
│  Service Layer                                              │
│    ├── AuthService (JWT, authentication)                   │
│    ├── UserService (business logic)                        │
│    └── EmailService (verification, reset)                  │
├─────────────────────────────────────────────────────────────┤
│  Repository Layer (Spring Data JPA)                         │
│    ├── UserRepository                                       │
│    ├── RoleRepository                                       │
│    ├── AddressRepository                                    │
│    └── TokenRepository                                      │
├─────────────────────────────────────────────────────────────┤
│  Security Layer                                             │
│    ├── JwtAuthenticationFilter                             │
│    ├── JwtTokenProvider                                     │
│    └── CustomUserDetailsService                            │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │  MySQL Database      │
              │  retailsports_users  │
              └──────────────────────┘
```

---

## 🗄️ Schema Database

### Tabelle principali:

| Tabella | Descrizione |
|---------|-------------|
| `users` | Dati utenti con soft delete |
| `roles` | Ruoli sistema (USER, ADMIN, EMPLOYEE) |
| `user_roles` | Associazione many-to-many utenti-ruoli |
| `addresses` | Indirizzi di spedizione/fatturazione |
| `refresh_tokens` | Token JWT per refresh |
| `verification_tokens` | Token per email verification e password reset |
| `login_audit` | Storico accessi (audit log) |

### ER Diagram:
```
┌─────────────┐         ┌──────────────┐         ┌─────────┐
│    users    │────────▶│  user_roles  │◀────────│  roles  │
└─────────────┘         └──────────────┘         └─────────┘
      │                                                
      │ 1:N                                           
      ▼                                                
┌─────────────┐                                       
│  addresses  │                                       
└─────────────┘                                       
      │                                                
      │ 1:N                                           
      ▼                                                
┌─────────────────────┐                               
│ verification_tokens │                               
└─────────────────────┘                               
      │                                                
      │ 1:N                                           
      ▼                                                
┌─────────────────┐                                   
│ refresh_tokens  │                                   
└─────────────────┘                                   
      │                                                
      │ 1:N                                           
      ▼                                                
┌─────────────┐                                       
│ login_audit │                                       
└─────────────┘                                       
```

---

## 🚀 Stack Tecnologico

| Tecnologia | Versione | Utilizzo |
|------------|----------|----------|
| **Java** | 21 | Linguaggio principale |
| **Spring Boot** | 3.5.9 | Framework applicativo |
| **Spring Cloud** | 2025.0.1 | Microservices support |
| **Spring Data JPA** | - | ORM / Database access |
| **Spring Security** | - | Autenticazione e autorizzazione |
| **MySQL** | 8.0 | Database relazionale |
| **Hibernate** | - | JPA implementation |
| **Lombok** | - | Riduzione boilerplate code |
| **Gradle** | 8.14.3 | Build tool |
| **JWT (jjwt)** | 0.11.5 | Token-based authentication |
| **Eureka Client** | - | Service discovery |

---

## 📦 Setup e Installazione

### Prerequisiti:
- ✅ Java 21 (JDK)
- ✅ MySQL 8.0+
- ✅ Gradle 8.14.3+ (incluso wrapper)
- ✅ Eureka Server in esecuzione (porta 8761)

### 1️⃣ Clone del repository:
```bash
git clone https://github.com/CSINCE90/retail-project.git
cd retail-project/user-service
```

### 2️⃣ Configurazione Database:

**Esegui lo script SQL:**
```bash
mysql -u root -p < ../scripts/user-service-db-schema.sql
```

**Oppure tramite MySQL Workbench:**
- File → Open SQL Script → `user-service-db-schema.sql`
- Execute (⚡)

### 3️⃣ Configurazione `application.yaml`:

Modifica `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/retailsports_users
    username: root
    password: TUA_PASSWORD_MYSQL  # <-- MODIFICA QUI
```

### 4️⃣ Avvio del servizio:

```bash
# Build del progetto
./gradlew build

# Avvio in modalità development
./gradlew bootRun
```

**Il servizio sarà disponibile su:** `http://localhost:8081`

### 5️⃣ Verifica stato:

```bash
# Health check
curl http://localhost:8081/actuator/health

# Verifica registrazione su Eureka
# Apri http://localhost:8761 nel browser
```

---

## 🔌 API Endpoints

### 🔐 Authentication (`/api/auth`)

| Method | Endpoint | Descrizione | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/auth/register` | Registrazione nuovo utente | ❌ |
| `POST` | `/api/auth/login` | Login utente | ❌ |
| `POST` | `/api/auth/refresh` | Refresh access token | ✅ |
| `POST` | `/api/auth/logout` | Logout utente | ✅ |
| `POST` | `/api/auth/verify-email` | Verifica email | ❌ |
| `POST` | `/api/auth/forgot-password` | Richiesta reset password | ❌ |
| `POST` | `/api/auth/reset-password` | Reset password | ❌ |

### 👤 Users (`/api/users`)

| Method | Endpoint | Descrizione | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| `GET` | `/api/users/me` | Profilo utente corrente | ✅ | USER |
| `PUT` | `/api/users/me` | Aggiorna profilo | ✅ | USER |
| `DELETE` | `/api/users/me` | Elimina account (soft delete) | ✅ | USER |
| `GET` | `/api/users` | Lista tutti gli utenti | ✅ | ADMIN |
| `GET` | `/api/users/{id}` | Dettagli utente | ✅ | ADMIN |
| `PUT` | `/api/users/{id}` | Aggiorna utente | ✅ | ADMIN |
| `DELETE` | `/api/users/{id}` | Elimina utente | ✅ | ADMIN |
| `POST` | `/api/users/{id}/restore` | Ripristina utente soft-deleted | ✅ | ADMIN |

### 📍 Addresses (`/api/addresses`)

| Method | Endpoint | Descrizione | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/addresses` | Lista indirizzi utente | ✅ |
| `POST` | `/api/addresses` | Crea nuovo indirizzo | ✅ |
| `GET` | `/api/addresses/{id}` | Dettagli indirizzo | ✅ |
| `PUT` | `/api/addresses/{id}` | Aggiorna indirizzo | ✅ |
| `DELETE` | `/api/addresses/{id}` | Elimina indirizzo | ✅ |
| `PUT` | `/api/addresses/{id}/set-default` | Imposta come predefinito | ✅ |

### 📊 Monitoring (`/actuator`)

| Method | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/info` | Info applicazione |
| `GET` | `/actuator/metrics` | Metriche applicazione |

---

## 📝 Esempi di Request/Response

### Registrazione utente:

**Request:**
```bash
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "mario.rossi",
  "email": "mario.rossi@example.com",
  "password": "SecurePass123!",
  "firstName": "Mario",
  "lastName": "Rossi",
  "phone": "+39 333 1234567"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "mario.rossi",
  "email": "mario.rossi@example.com",
  "firstName": "Mario",
  "lastName": "Rossi",
  "roles": ["ROLE_USER"],
  "emailVerified": false,
  "enabled": true,
  "createdAt": "2024-12-21T10:30:00"
}
```

### Login:

**Request:**
```bash
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "mario.rossi",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Profilo utente:

**Request:**
```bash
GET http://localhost:8081/api/users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 1,
  "username": "mario.rossi",
  "email": "mario.rossi@example.com",
  "firstName": "Mario",
  "lastName": "Rossi",
  "phone": "+39 333 1234567",
  "roles": ["ROLE_USER"],
  "emailVerified": true,
  "enabled": true,
  "createdAt": "2024-12-21T10:30:00",
  "updatedAt": "2024-12-21T15:45:00"
}
```

---

## 🔒 Sistema di Autenticazione

### JWT (JSON Web Token):

Il servizio utilizza **JWT** per l'autenticazione stateless:

- **Access Token**: valido 24 ore, usato per le richieste API
- **Refresh Token**: valido 7 giorni, usato per ottenere nuovi access token

### Flow di autenticazione:

```
┌─────────┐                ┌──────────────┐              ┌──────────┐
│ Client  │                │ User Service │              │  MySQL   │
└────┬────┘                └──────┬───────┘              └────┬─────┘
     │                             │                           │
     │  POST /api/auth/login       │                           │
     │────────────────────────────>│                           │
     │                             │  Verifica credenziali     │
     │                             │──────────────────────────>│
     │                             │<──────────────────────────│
     │                             │  Genera JWT tokens        │
     │  Access + Refresh Token     │                           │
     │<────────────────────────────│                           │
     │                             │                           │
     │  GET /api/users/me          │                           │
     │  Header: Bearer {token}     │                           │
     │────────────────────────────>│                           │
     │                             │  Valida JWT               │
     │                             │  Estrae userId            │
     │                             │  Query user               │
     │                             │──────────────────────────>│
     │  User data                  │<──────────────────────────│
     │<────────────────────────────│                           │
```

### Ruoli e Permessi (RBAC):

| Ruolo | Descrizione | Permessi |
|-------|-------------|----------|
| **ROLE_USER** | Cliente standard | Gestione profilo personale, indirizzi, ordini |
| **ROLE_ADMIN** | Amministratore | Tutti i permessi, gestione utenti |
| **ROLE_EMPLOYEE** | Dipendente negozio | Gestione ordini, stock, clienti |

---

## 🧪 Testing

### Unit Test:
```bash
./gradlew test
```

### Integration Test:
```bash
./gradlew integrationTest
```

### Test Coverage:
```bash
./gradlew jacocoTestReport
```

Il report sarà disponibile in: `build/reports/jacoco/test/html/index.html`

---

## 📊 Monitoring e Logging

### Health Check:
```bash
curl http://localhost:8081/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

### Logs:
I log sono salvati in `logs/user-service.log`

**Livelli di log:**
- `DEBUG`: Queries SQL, Spring Security
- `INFO`: Startup, Eureka registration
- `WARN`: Errori recuperabili
- `ERROR`: Errori critici

---

## 🔧 Configurazione

### Variabili d'ambiente (produzione):

```bash
# Database
export DB_URL=jdbc:mysql://prod-db-server:3306/retailsports_users
export DB_USERNAME=app_user
export DB_PASSWORD=secure_password

# JWT
export JWT_SECRET=your_secure_random_256bit_secret_key_here
export JWT_EXPIRATION=86400000

# Eureka
export EUREKA_URL=http://eureka-server:8761/eureka/
```

### application-prod.yaml:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL}
```

---

## 🐳 Docker

### Dockerfile:
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build e Run:
```bash
# Build
./gradlew build
docker build -t retailsports/user-service:latest .

# Run
docker run -p 8081:8081 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/retailsports_users \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  retailsports/user-service:latest
```

---

## 🔐 Sicurezza

### Best Practices implementate:

✅ **Password hashing** con BCrypt (strength 10)  
✅ **JWT** con secret key sicura  
✅ **Rate limiting** sui tentativi di login  
✅ **Soft delete** per compliance GDPR  
✅ **Audit log** completo degli accessi  
✅ **Email verification** per nuovi account  
✅ **Password reset** con token a scadenza  
✅ **HTTPS** ready (configurare reverse proxy)  
✅ **SQL Injection** protection (Prepared Statements)  
✅ **XSS** protection (Spring Security defaults)

### Token Expiration:
- Access Token: **24 ore**
- Refresh Token: **7 giorni**
- Email Verification: **24 ore**
- Password Reset: **1 ora**

---

## 📚 Dipendenze principali

```gradle
dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Spring Cloud
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    
    // Database
    runtimeOnly 'com.mysql:mysql-connector-j'
    
    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    
    // Utilities
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

---

## 🚦 Status del Progetto

| Feature | Status |
|---------|--------|
| Database Schema | ✅ Completato |
| Configuration | ✅ Completato |
| Entity Models | 🔄 In sviluppo |
| Repositories | 🔄 In sviluppo |
| Services | 🔄 In sviluppo |
| Controllers | 🔄 In sviluppo |
| Security (JWT) | 🔄 In sviluppo |
| Unit Tests | 📋 Pianificato |
| Integration Tests | 📋 Pianificato |
| API Documentation | 📋 Pianificato |
| Docker Setup | 📋 Pianificato |

---

## 📖 Documentazione Aggiuntiva

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JWT.io](https://jwt.io/)
- [Eureka](https://cloud.spring.io/spring-cloud-netflix/reference/html/)

---

## 👨‍💻 Autore

**Francesco Chifari**  
Software Engineer | Laureando in Scienze Informatiche  
Backend Development | Microservices Architecture

- GitHub: [@CSINCE90](https://github.com/CSINCE90)
- LinkedIn: Francesco Chifari

---

## 📄 Licenza

Progetto sviluppato a scopo didattico/professionale.

---

## 🤝 Contributi

Per contribuire al progetto:

1. Fork del repository
2. Crea un branch per la feature (`git checkout -b feature/AmazingFeature`)
3. Commit delle modifiche (`git commit -m 'Add some AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Apri una Pull Request

---

**Versione:** 1.0.0  
**Ultimo aggiornamento:** Dicembre 2025