# Payment Service - RetailSports Microservices

Payment Service per la gestione dei pagamenti nell'architettura microservizi RetailSports.

## 🚀 Stack Tecnologico

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Spring Cloud**: 2025.0.1
- **Database**: MySQL 8.0
- **Build Tool**: Gradle
- **Service Discovery**: Eureka
- **Port**: 8088

## 📋 Setup

### 1. Setup Database

```bash
# Crea il database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS retailsports_payment;"

# Verifica
mysql -u root -p -e "USE retailsports_payment; SHOW TABLES;"
```

### 2. Configurazione

Il file di configurazione contiene informazioni sensibili (password, API keys) e **NON** è tracciato in git.

**Copia il file di esempio:**

```bash
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

**Modifica `application.yaml` e aggiorna:**

```yaml
spring:
  datasource:
    password: YOUR_PASSWORD  # Sostituisci con la tua password MySQL

# Payment Gateway Configuration
payment:
  stripe:
    public-key: YOUR_STRIPE_PUBLIC_KEY
    secret-key: YOUR_STRIPE_SECRET_KEY
  paypal:
    client-id: YOUR_PAYPAL_CLIENT_ID
    client-secret: YOUR_PAYPAL_CLIENT_SECRET
```

**IMPORTANTE:** Il file `application.yaml` è nel `.gitignore` e non verrà committato.

### 3. Avvio del Servizio

#### Prerequisiti
1. Eureka Server in esecuzione sulla porta 8761
2. MySQL in esecuzione sulla porta 3306
3. Database `retailsports_payment` creato

#### Build e Run

```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun
```

Il servizio sarà disponibile su `http://localhost:8088`

### 4. Verifica

```bash
# Health check
curl http://localhost:8088/actuator/health

# Verifica registrazione su Eureka
open http://localhost:8761
```

Dovresti vedere **PAYMENT-SERVICE** registrato su Eureka.

---

## 🎯 Funzionalità (Planned)

- [ ] Integrazione Stripe
- [ ] Integrazione PayPal
- [ ] Gestione transazioni
- [ ] Gestione rimborsi
- [ ] Webhook handlers
- [ ] Payment history
- [ ] Invoice generation

---

## 🔧 Configurazione

### Port

Default: **8088**

### Database

Nome database: `retailsports_payment`

### Eureka

Il servizio si registra automaticamente su Eureka Server all'avvio.

---

## 📝 Note Importanti

1. **API Keys**: Configura le API keys dei payment gateway prima di usare il servizio
2. **Sicurezza**: Non committare mai `application.yaml` con credenziali reali
3. **Testing**: Usa le modalità sandbox/test dei payment gateway per lo sviluppo

---

## 📚 Riferimenti

- Spring Boot Documentation
- Stripe API Documentation
- PayPal API Documentation

