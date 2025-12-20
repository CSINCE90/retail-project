## 📖 Spiegazione Configurazione

### **Server**
- `port: 8080` → Porta standard per l'API Gateway

### **Spring Cloud Gateway**

**Discovery Locator:**
- `enabled: true` → Il Gateway scopre automaticamente i servizi registrati su Eureka
- `lower-case-service-id: true` → Usa nomi servizi in minuscolo nelle URL

**Routes (Instradamento):**
Ogni route ha:
- **id**: identificatore univoco
- **uri**: `lb://nome-servizio` (lb = load balanced tramite Eureka)
- **predicates**: condizioni per attivare la route
  - `Path=/api/users/**` → tutte le richieste che iniziano con `/api/users/`
- **filters**: trasformazioni sulla richiesta
  - `StripPrefix=1` → rimuove il primo segmento del path (`/api`)

**Esempio pratico:**
```
Richiesta: http://localhost:8080/api/users/login
          ↓ (Gateway applica StripPrefix=1)
Inoltrata: http://user-service:8081/users/login