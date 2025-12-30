# Cart Service - Quick Start Guide

## 🚀 Avvio Rapido

### Step 1: Setup Database

```bash
# Crea il database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS retailsports_cart;"

# Esegui lo schema
mysql -u root -p retailsports_cart < database/schema.sql

# Verifica la creazione
mysql -u root -p -e "USE retailsports_cart; SHOW TABLES;"
```

**Output atteso:**
```
+-----------------------------+
| Tables_in_retailsports_cart |
+-----------------------------+
| cart_items                  |
| carts                       |
+-----------------------------+
```

### Step 2: Configurazione application.yaml

```bash
# Copia il file di esempio
cp src/main/resources/application.yaml.example src/main/resources/application.yaml

# Modifica la password MySQL nel file
# Sostituisci YOUR_PASSWORD_HERE con la tua password
nano src/main/resources/application.yaml
```

**NOTA:** Il file `application.yaml` è nel `.gitignore` e non verrà committato.

### Step 3: Verifica Prerequisiti

```bash
# 1. Eureka Server deve essere in esecuzione
curl http://localhost:8761

# 2. Product Service deve essere in esecuzione
curl http://localhost:8082/actuator/health

# 3. MySQL deve essere in esecuzione
mysql -u root -p -e "SELECT 1;"
```

### Step 4: Build e Run

```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun
```

**Output atteso:**
```
Started CartServiceApplication in X.XXX seconds
Registering application cart-service with eureka
```

### Step 5: Verifica Registrazione

```bash
# Health check
curl http://localhost:8087/actuator/health

# Verifica registrazione su Eureka
open http://localhost:8761
```

Dovresti vedere **CART-SERVICE** registrato su Eureka.

---

## 🧪 Test Rapidi

### Test 1: Crea Carrello e Aggiungi Prodotto

```bash
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'
```

**Response attesa:**
```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "quantity": 2,
      "unitPriceCents": 9999,
      "subtotalCents": 19998,
      "finalPriceCents": 19998,
      "subtotalFormatted": "199.98€",
      "finalPriceFormatted": "199.98€"
    }
  ],
  "totalItems": 2,
  "subtotalCents": 19998,
  "totalCents": 19998,
  "totalFormatted": "199.98€"
}
```

### Test 2: Visualizza Carrello

```bash
curl http://localhost:8087/api/cart/1
```

### Test 3: Aggiorna Quantità

```bash
curl -X PUT http://localhost:8087/api/cart/1/items/1 \
  -H "Content-Type: application/json" \
  -d '{"quantity": 5}'
```

### Test 4: Visualizza Riepilogo

```bash
curl http://localhost:8087/api/cart/1/summary
```

**Response attesa:**
```json
{
  "id": 1,
  "userId": 1,
  "totalItems": 5,
  "uniqueProducts": 1,
  "subtotalCents": 49995,
  "totalCents": 49995,
  "totalFormatted": "499.95€"
}
```

### Test 5: Rimuovi Prodotto

```bash
curl -X DELETE http://localhost:8087/api/cart/1/items/1
```

### Test 6: Svuota Carrello

```bash
curl -X DELETE http://localhost:8087/api/cart/1
```

---

## 🔍 Test Validazioni

### Test 1: Quantità Invalida (< 1)

```bash
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 0}'
```

**Response attesa (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation failed for one or more fields",
  "validationErrors": [
    {
      "field": "quantity",
      "rejectedValue": 0,
      "message": "Quantity must be at least 1"
    }
  ]
}
```

### Test 2: Prodotto Non Esistente

```bash
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 99999, "quantity": 1}'
```

**Response attesa (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 99999"
}
```

### Test 3: Stock Insufficiente

```bash
# Se il prodotto ha stock = 5
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 100}'
```

**Response attesa (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Insufficient Stock",
  "message": "Insufficient stock for product 1. Available: 5, Requested: 100"
}
```

---

## 📊 Verifica Logs

```bash
# In un altro terminale, monitora i logs
tail -f logs/cart-service.log

# Oppure usa il logging della console
```

**Logs attesi:**
```
INFO  c.r.cart_service.service.CartServiceImpl : Getting cart for user: 1
INFO  c.r.cart_service.service.CartServiceImpl : Creating new cart for user: 1
INFO  c.r.cart_service.service.ProductServiceClient : Fetching product info from Product Service
INFO  c.r.cart_service.service.ProductServiceClient : Product info retrieved successfully: Product Name
INFO  c.r.cart_service.service.CartServiceImpl : Added new product 1 to cart
```

---

## 🐛 Troubleshooting

### Problema: "Connection refused" a Product Service

**Soluzione:**
```bash
# Verifica che product-service sia in esecuzione
curl http://localhost:8082/actuator/health

# Se non risponde, avvia product-service
cd ../product-service && ./gradlew bootRun
```

### Problema: "Unable to connect to database"

**Soluzione:**
```bash
# Verifica MySQL
mysql -u root -p -e "SELECT 1;"

# Verifica password in application.yaml
# Assicurati che corrisponda alla tua password MySQL
```

### Problema: "Application failed to start"

**Soluzione:**
```bash
# Controlla che Eureka Server sia in esecuzione
curl http://localhost:8761

# Verifica le porte
lsof -i :8087  # Porta cart-service
lsof -i :8761  # Porta eureka-server
lsof -i :8082  # Porta product-service
```

### Problema: "Table doesn't exist"

**Soluzione:**
```bash
# Ricrea le tabelle
mysql -u root -p retailsports_cart < database/schema.sql

# Oppure usa ddl-auto=create (ATTENZIONE: cancella i dati)
# In application.yaml: spring.jpa.hibernate.ddl-auto: create
```

---

## 🎯 Scenario Test Completo

```bash
#!/bin/bash

echo "=== CART SERVICE TEST SUITE ==="

# Test 1: Crea carrello e aggiungi primo prodotto
echo -e "\n[1] Aggiungendo prodotto 1 (qty=2)..."
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}' \
  | jq

sleep 1

# Test 2: Aggiungi secondo prodotto
echo -e "\n[2] Aggiungendo prodotto 2 (qty=1)..."
curl -X POST http://localhost:8087/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 2, "quantity": 1}' \
  | jq

sleep 1

# Test 3: Visualizza carrello completo
echo -e "\n[3] Visualizzando carrello completo..."
curl http://localhost:8087/api/cart/1 | jq

sleep 1

# Test 4: Aggiorna quantità primo prodotto
echo -e "\n[4] Aggiornando quantità prodotto 1 a 5..."
curl -X PUT http://localhost:8087/api/cart/1/items/1 \
  -H "Content-Type: application/json" \
  -d '{"quantity": 5}' \
  | jq

sleep 1

# Test 5: Visualizza riepilogo
echo -e "\n[5] Visualizzando riepilogo..."
curl http://localhost:8087/api/cart/1/summary | jq

sleep 1

# Test 6: Rimuovi secondo prodotto
echo -e "\n[6] Rimuovendo prodotto 2..."
curl -X DELETE http://localhost:8087/api/cart/1/items/2 | jq

sleep 1

# Test 7: Visualizza carrello finale
echo -e "\n[7] Visualizzando carrello finale..."
curl http://localhost:8087/api/cart/1 | jq

sleep 1

# Test 8: Svuota carrello
echo -e "\n[8] Svuotando carrello..."
curl -X DELETE http://localhost:8087/api/cart/1 -v

sleep 1

# Test 9: Verifica carrello vuoto
echo -e "\n[9] Verificando carrello vuoto..."
curl http://localhost:8087/api/cart/1 | jq

echo -e "\n=== TEST SUITE COMPLETATA ==="
```

Salva come `test-cart-service.sh`, rendi eseguibile e lancia:

```bash
chmod +x test-cart-service.sh
./test-cart-service.sh
```

---

## 📝 Checklist Pre-Deploy

- [ ] MySQL in esecuzione sulla porta 3306
- [ ] Database `retailsports_cart` creato
- [ ] Tabelle `carts` e `cart_items` create
- [ ] Eureka Server in esecuzione sulla porta 8761
- [ ] Product Service in esecuzione sulla porta 8082
- [ ] Password MySQL corretta in application.yaml
- [ ] Build completato senza errori (`./gradlew clean build`)
- [ ] Cart Service in esecuzione sulla porta 8087
- [ ] Cart Service registrato su Eureka
- [ ] Health check passa: `curl http://localhost:8087/actuator/health`

---

## ✅ Verifica Successo

Tutti i test dovrebbero passare con successo. Il Cart Service è pronto per:

1. ✅ Gestire carrelli multipli (uno per utente)
2. ✅ Aggiungere/rimuovere/aggiornare prodotti
3. ✅ Validare stock tramite Product Service
4. ✅ Calcolare totali e sconti
5. ✅ Gestire errori in modo strutturato
6. ✅ Integrarsi con il microservices ecosystem

**CART SERVICE OPERATIVO! 🎉**
