#!/bin/bash

# ================================================================================
# CART SERVICE - TEST SUITE
# ================================================================================

echo "================================================================================"
echo "CART SERVICE - TEST SUITE"
echo "================================================================================"

BASE_URL="http://localhost:8087"

# Colori per output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Funzione per test
test_endpoint() {
    echo -e "\n${YELLOW}[TEST $1]${NC} $2"
    echo "--------------------------------------------------------------------------------"
}

# Funzione per successo
success() {
    echo -e "${GREEN}✓ SUCCESS${NC}"
}

# Funzione per errore
error() {
    echo -e "${RED}✗ FAILED${NC}"
}

# ================================================================================
# PREREQUISITI
# ================================================================================

test_endpoint "0" "Verificando prerequisiti..."

# Verifica Cart Service
if curl -s "$BASE_URL/actuator/health" > /dev/null; then
    success
    echo "Cart Service is running on port 8087"
else
    error
    echo "Cart Service is NOT running. Please start it first."
    exit 1
fi

# ================================================================================
# TEST 1: Crea carrello e aggiungi primo prodotto
# ================================================================================

test_endpoint "1" "Aggiungendo prodotto 1 (qty=2) al carrello user 1..."

curl -s -X POST "$BASE_URL/api/cart/1/items" \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}' \
  | jq '.'

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 2: Aggiungi secondo prodotto
# ================================================================================

test_endpoint "2" "Aggiungendo prodotto 2 (qty=1) al carrello..."

curl -s -X POST "$BASE_URL/api/cart/1/items" \
  -H "Content-Type: application/json" \
  -d '{"productId": 2, "quantity": 1}' \
  | jq '.'

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 3: Visualizza carrello completo
# ================================================================================

test_endpoint "3" "Visualizzando carrello completo..."

curl -s "$BASE_URL/api/cart/1" | jq '.'

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 4: Aggiorna quantità primo prodotto
# ================================================================================

test_endpoint "4" "Aggiornando quantità prodotto 1 a 5..."

curl -s -X PUT "$BASE_URL/api/cart/1/items/1" \
  -H "Content-Type: application/json" \
  -d '{"quantity": 5}' \
  | jq '.'

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 5: Visualizza riepilogo carrello
# ================================================================================

test_endpoint "5" "Visualizzando riepilogo carrello..."

curl -s "$BASE_URL/api/cart/1/summary" | jq '.'

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 6: Rimuovi secondo prodotto
# ================================================================================

test_endpoint "6" "Rimuovendo prodotto 2 dal carrello..."

curl -s -X DELETE "$BASE_URL/api/cart/1/items/2" | jq '.'

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 7: Visualizza carrello dopo rimozione
# ================================================================================

test_endpoint "7" "Visualizzando carrello dopo rimozione..."

curl -s "$BASE_URL/api/cart/1" | jq '.'

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 8: Test validazione - Quantità invalida
# ================================================================================

test_endpoint "8" "Test validazione - Quantità 0 (deve fallire)..."

RESPONSE=$(curl -s -X POST "$BASE_URL/api/cart/1/items" \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 0}')

echo "$RESPONSE" | jq '.'

if echo "$RESPONSE" | grep -q "Validation Failed"; then
    success
    echo "Validation working correctly!"
else
    error
fi

sleep 1

# ================================================================================
# TEST 9: Test validazione - Prodotto non esistente
# ================================================================================

test_endpoint "9" "Test validazione - Prodotto inesistente (deve fallire)..."

RESPONSE=$(curl -s -X POST "$BASE_URL/api/cart/1/items" \
  -H "Content-Type: application/json" \
  -d '{"productId": 99999, "quantity": 1}')

echo "$RESPONSE" | jq '.'

if echo "$RESPONSE" | grep -q "not found"; then
    success
    echo "Product validation working correctly!"
else
    error
fi

sleep 1

# ================================================================================
# TEST 10: Svuota carrello
# ================================================================================

test_endpoint "10" "Svuotando carrello..."

curl -s -X DELETE "$BASE_URL/api/cart/1" -o /dev/null -w "%{http_code}\n"

if [ $? -eq 0 ]; then
    success
else
    error
fi

sleep 1

# ================================================================================
# TEST 11: Verifica carrello vuoto
# ================================================================================

test_endpoint "11" "Verificando carrello vuoto..."

RESPONSE=$(curl -s "$BASE_URL/api/cart/1")
echo "$RESPONSE" | jq '.'

ITEMS_COUNT=$(echo "$RESPONSE" | jq '.totalItems')

if [ "$ITEMS_COUNT" == "0" ]; then
    success
    echo "Cart is empty as expected"
else
    error
fi

# ================================================================================
# RIEPILOGO
# ================================================================================

echo ""
echo "================================================================================"
echo "TEST SUITE COMPLETATA"
echo "================================================================================"
echo ""
echo "Verifica i risultati sopra per assicurarti che tutti i test siano passati."
echo ""
echo "Per un test manuale, usa:"
echo "  - GET    $BASE_URL/api/cart/{userId}"
echo "  - POST   $BASE_URL/api/cart/{userId}/items"
echo "  - PUT    $BASE_URL/api/cart/{userId}/items/{productId}"
echo "  - DELETE $BASE_URL/api/cart/{userId}/items/{productId}"
echo "  - DELETE $BASE_URL/api/cart/{userId}"
echo "  - GET    $BASE_URL/api/cart/{userId}/summary"
echo ""
echo "================================================================================"
