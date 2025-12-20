# 🌐 API Gateway - RetailSports

API Gateway centralizzato per l'architettura microservizi RetailSports, basato su **Spring Cloud Gateway**.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen?style=flat-square&logo=spring)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.1-blue?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)

---

## 📋 Descrizione

L'API Gateway funge da **punto di ingresso unico** per tutti i microservizi dell'ecosistema RetailSports. Gestisce il routing dinamico, il load balancing e si integra con Eureka Server per il service discovery.

---

## 🎯 Funzionalità

- ✅ **Routing Dinamico** - Instradamento automatico delle richieste verso i microservizi
- ✅ **Service Discovery** - Integrazione con Eureka per discovery automatico dei servizi
- ✅ **Load Balancing** - Distribuzione del carico tra istanze multiple dello stesso servizio
- ✅ **Path Rewriting** - Rimozione prefissi `/api` dalle URL
- ✅ **Monitoring** - Endpoint Actuator per health check e visualizzazione route
- 🔄 **CORS** *(pianificato)*
- 🔄 **Rate Limiting** *(pianificato)*
- 🔄 **Authentication/Authorization** *(pianificato)*

---

## 🏗️ Architettura
![Eureka Service](docs/images/Eureka.png)

```
Client Request
     ↓
API Gateway (8080)
     ↓
[Eureka Discovery]
     ↓
┌────────────┬──────────────┬─────────────┬──────────────┐
│   User     │   Product    │    Cart     │   Payment    │
│  Service   │   Service    │   Service   │   Service    │
│   (8081)   │   (8082)     │   (8087)    │   (8088)     │
└────────────┴──────────────┴─────────────┴──────────────┘
```

---

## 🔌 Routes Configurate

| Path | Servizio Target | Porta | Descrizione |
|------|----------------|-------|-------------|
| `/api/users/**` | user-service | 8081 | Autenticazione e gestione utenti |
| `/api/products/**` | product-service | 8082 | Catalogo prodotti |
| `/api/cart/**` | cart-service | 8087 | Gestione carrello |
| `/api/payments/**` | payment-service | 8088 | Processamento pagamenti |


### Prerequisiti

- Java 21
- Gradle 8.14+
- Eureka Server avviato su porta 8761

### Installazione

```bash
# Clone del repository
git clone https://github.com/CSINCE90/retail-project.git
cd retail-project/api-gateway

# Build del progetto
./gradlew build

# Avvio del servizio
./gradlew bootRun
```

### Verifica

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Visualizza Routes
curl http://localhost:8080/actuator/gateway/routes
```

**Dashboard Eureka:** http://localhost:8761  
L'API Gateway dovrebbe apparire come **API-GATEWAY** nell'elenco dei servizi registrati.

---


## 📊 Endpoints Actuator

| Endpoint | Descrizione |
|----------|-------------|
| `/actuator/health` | Stato di salute del servizio |
| `/actuator/info` | Informazioni sull'applicazione |
| `/actuator/gateway/routes` | Lista completa delle route configurate |

---

## 🛠️ Tecnologie Utilizzate

| Tecnologia | Versione | Scopo |
|------------|----------|-------|
| Spring Boot | 3.5.9 | Framework principale |
| Spring Cloud Gateway | 2025.0.1 | Routing e gateway |
| Spring Cloud Netflix Eureka Client | 2025.0.1 | Service discovery |
| Spring Boot Actuator | 3.5.9 | Monitoring e metriche |
| Java | 21 | Linguaggio di programmazione |
| Gradle | 8.14.3 | Build automation |

---

## 📁 Struttura Progetto

```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/com/retailsports/api_gateway/
│   │   │   └── ApiGatewayApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/retailsports/api_gateway/
│           └── ApiGatewayApplicationTests.java
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🔜 Roadmap

- [ ] Implementazione CORS policy
- [ ] Rate limiting per endpoint
- [ ] Circuit breaker pattern (Resilience4j)
- [ ] Request/Response logging
- [ ] JWT authentication filter
- [ ] Metriche con Micrometer/Prometheus
- [ ] Containerizzazione Docker

---

## 🐛 Troubleshooting

### Gateway non si registra su Eureka

**Problema:** Il Gateway non appare nella dashboard Eureka.

**Soluzione:**
1. Verifica che Eureka Server sia avviato su porta 8761
2. Controlla i log del Gateway per errori di connessione
3. Verifica la configurazione `eureka.client.service-url.defaultZone`

```bash
# Verifica connettività Eureka
curl http://localhost:8761/eureka/apps
```

### Porta 8080 già in uso

**Problema:** Errore `Port 8080 was already in use`

**Soluzione:**
Cambia porta nel `application.yml` oppure termina il processo:

```bash
# macOS/Linux - Trova processo sulla porta 8080
lsof -ti:8080 | xargs kill -9
```

### Route non funzionano

**Problema:** Le richieste non vengono instradate correttamente.

**Soluzione:**
1. Verifica che i microservizi target siano registrati su Eureka
2. Controlla i log del Gateway per errori di routing
3. Verifica le route configurate: `curl http://localhost:8080/actuator/gateway/routes`

---

## 📚 Documentazione

- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

## 👨‍💻 Autore

**Francesco Chifari**  
Software Engineer | Dottore in Informatica  
Backend Development | Microservices Architecture

- GitHub: [@CSINCE90](https://github.com/CSINCE90)
- LinkedIn: Francesco Chifari

---

## 📄 Licenza

Progetto sviluppato a scopo didattico/professionale.

---

**Parte del progetto:** [RetailSports Microservices](https://github.com/CSINCE90/retail-project)
