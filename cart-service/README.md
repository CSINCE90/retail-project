# Cart Service - RetailSports Microservices

Cart Service per la gestione del carrello della spesa nell'architettura microservizi RetailSports.

## Stack Tecnologico

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Spring Cloud**: 2025.0.1
- **Database**: MySQL 8.0
- **Build Tool**: Gradle
- **Service Discovery**: Eureka
- **Port**: 8087

## Setup Database

1. Esegui lo script SQL per creare il database:

```bash
mysql -u root -p < database/schema.sql
```

2. Verifica che il database `retailsports_cart` sia stato creato con le tabelle:
   - `carts`
   - `cart_items`

## Configurazione

### Setup application.yaml

Il file di configurazione contiene informazioni sensibili (password database) e **NON** è tracciato in git.

1. Copia il file di esempio:

```bash
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

2. Modifica `application.yaml` e aggiorna la password del database:

```yaml
spring:
  datasource:
    password: YOUR_PASSWORD  # Sostituisci con la tua password MySQL
```

**IMPORTANTE:** Il file `application.yaml` è nel `.gitignore` e non verrà committato.

## Avvio del Servizio

### Prerequisiti
1. Eureka Server deve essere in esecuzione sulla porta 8761
2. Product Service deve essere in esecuzione sulla porta 8082
3. MySQL deve essere in esecuzione sulla porta 3306

### Build e Run

```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun
```

Il servizio sarà disponibile su `http://localhost:8087`

## Endpoints REST API

### 1. Ottieni Carrello
```bash
GET /api/cart/{userId}
```

**Esempio:**
```bash
curl http://localhost:8087/api/cart/1
```

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "items": [],
  "totalItems": 0,
  "subtotalCents": 0,
  "totalDiscountCents": 0,
  "totalCents": 0,
  "subtotalFormatted": "0.00€",
  "totalDiscountFormatted": "0.00€",
  "totalFormatted": "0.00€"
}
```

### 2. Aggiungi Prodotto al Carrello
```bash
POST /api/cart/{userId}/items
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

**Esempio:**
```bash
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'
```

### 3. Aggiorna Quantità Item
```bash
PUT /api/cart/{userId}/items/{productId}
Content-Type: application/json

{
  "quantity": 3
}
```

**Esempio:**
```bash
curl -X PUT http://localhost:8087/api/cart/1/items/1 \
  -H "Content-Type: application/json" \
  -d '{"quantity": 3}'
```

### 4. Rimuovi Prodotto dal Carrello
```bash
DELETE /api/cart/{userId}/items/{productId}
```

**Esempio:**
```bash
curl -X DELETE http://localhost:8087/api/cart/1/items/1
```

### 5. Svuota Carrello
```bash
DELETE /api/cart/{userId}
```

**Esempio:**
```bash
curl -X DELETE http://localhost:8087/api/cart/1
```

### 6. Ottieni Riepilogo Carrello
```bash
GET /api/cart/{userId}/summary
```

**Esempio:**
```bash
curl http://localhost:8087/api/cart/1/summary
```

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "totalItems": 5,
  "uniqueProducts": 2,
  "subtotalCents": 19998,
  "totalDiscountCents": 0,
  "totalCents": 19998,
  "subtotalFormatted": "199.98€",
  "totalDiscountFormatted": "0.00€",
  "totalFormatted": "199.98€"
}
```

## Funzionalità Chiave

### 1. Auto-Create Cart
Il carrello viene creato automaticamente se non esiste quando un utente:
- Ottiene il carrello (`GET /api/cart/{userId}`)
- Aggiunge un prodotto (`POST /api/cart/{userId}/items`)
- Richiede il riepilogo (`GET /api/cart/{userId}/summary`)

### 2. Validazione Stock
Prima di aggiungere o aggiornare un item, il servizio:
1. Chiama il Product Service per verificare la disponibilità
2. Valida che il prodotto sia attivo
3. Verifica che ci sia stock sufficiente (se tracciato)

### 3. Snapshot Prezzi
I prezzi vengono salvati al momento dell'aggiunta al carrello e non vengono ricalcolati dinamicamente. Questo garantisce che il prezzo nel carrello rimanga stabile anche se il prezzo del prodotto cambia.

### 4. Gestione Quantità
Se un prodotto è già presente nel carrello:
- `POST /items` → incrementa la quantità esistente
- `PUT /items/{productId}` → sostituisce la quantità

### 5. Calcolo Prezzi
Tutti i prezzi sono gestiti in **centesimi** (Long) per evitare problemi di arrotondamento:
- `subtotal = unitPriceCents * quantity`
- `discountAmount = subtotal * (discountPercentage / 100)`
- `finalPrice = subtotal - discountAmount`

## Gestione Errori

Il servizio restituisce errori strutturati in formato JSON:

### Esempio: Prodotto Non Trovato
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/cart/1/items"
}
```

### Esempio: Stock Insufficiente
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Insufficient Stock",
  "message": "Insufficient stock for product 1. Available: 5, Requested: 10",
  "path": "/api/cart/1/items"
}
```

### Esempio: Validazione Fallita
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation failed for one or more fields",
  "path": "/api/cart/1/items",
  "validationErrors": [
    {
      "field": "quantity",
      "rejectedValue": 0,
      "message": "Quantity must be at least 1"
    }
  ]
}
```

## Architettura

### Struttura Packages
```
com.retailsports.cart_service/
├── config/          → RestClientConfig
├── controller/      → CartController
├── dto/
│   ├── request/     → AddToCartRequest, UpdateCartItemRequest
│   └── response/    → CartResponse, CartItemResponse, CartSummaryResponse, ErrorResponse
├── entity/          → Cart, CartItem
├── exception/       → Custom exceptions + GlobalExceptionHandler
├── repository/      → CartRepository, CartItemRepository
└── service/         → CartService, CartServiceImpl, ProductServiceClient
```

### Pattern Utilizzati
- **Service Layer Pattern**: Separazione tra business logic e controller
- **DTO Pattern**: Separazione tra entity e response/request
- **Repository Pattern**: Astrazione dell'accesso ai dati
- **Global Exception Handling**: Gestione centralizzata degli errori
- **Soft Relationships**: Nessuna relazione JPA tra microservizi (solo ID)

## Monitoring

Il servizio espone endpoints Actuator:

```bash
# Health check
curl http://localhost:8087/actuator/health

# Info
curl http://localhost:8087/actuator/info
```

## Service Discovery

Il servizio si registra automaticamente su Eureka Server all'avvio. Verifica la registrazione su:

```
http://localhost:8761
```

## Note Importanti

1. **Prezzi in Centesimi**: Tutti i prezzi sono memorizzati e gestiti in centesimi (Long) per evitare errori di arrotondamento
2. **Snapshot Prezzi**: I prezzi vengono salvati al momento dell'aggiunta, non ricalcolati
3. **Soft Relationships**: Nessuna relazione JPA diretta con User Service o Product Service
4. **Validazione Stock**: Ogni aggiunta/modifica valida lo stock tramite Product Service
5. **Auto-Create**: Il carrello viene creato automaticamente se non esiste
6. **Unique Constraint**: Un prodotto può apparire una sola volta per carrello (cart_id + product_id unico)

## Testing con cURL

### Scenario Completo

```bash
# 1. Crea carrello e aggiungi prodotto
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'

# 2. Aggiungi altro prodotto
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 2, "quantity": 1}'

# 3. Visualizza carrello completo
curl http://localhost:8087/api/cart/1

# 4. Aggiorna quantità primo prodotto
curl -X PUT http://localhost:8087/api/cart/1/items/1 \
  -H "Content-Type: application/json" \
  -d '{"quantity": 5}'

# 5. Visualizza riepilogo
curl http://localhost:8087/api/cart/1/summary

# 6. Rimuovi un prodotto
curl -X DELETE http://localhost:8087/api/cart/1/items/2

# 7. Svuota carrello
curl -X DELETE http://localhost:8087/api/cart/1
```
