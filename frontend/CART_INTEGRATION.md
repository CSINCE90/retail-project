# Cart Service - Frontend Integration

Documentazione sull'integrazione del Cart Service backend nel frontend React.

## 📝 Modifiche Apportate

### 1. **Types TypeScript** ✅

Aggiunti nuovi types in `src/types/index.ts`:

```typescript
// Cart Service Backend Types
- CartItemResponse
- CartResponse
- CartSummaryResponse
- AddToCartRequest
- UpdateCartItemRequest
```

Questi types corrispondono esattamente ai DTOs del backend Cart Service.

### 2. **API Configuration** ✅

Aggiornato `src/config/api.config.ts` con gli endpoints del Cart Service:

```typescript
CART: {
  GET: (userId: number) => `/api/cart/${userId}`,
  ADD_ITEM: (userId: number) => `/api/cart/${userId}/items`,
  UPDATE_ITEM: (userId: number, productId: number) => `/api/cart/${userId}/items/${productId}`,
  REMOVE_ITEM: (userId: number, productId: number) => `/api/cart/${userId}/items/${productId}`,
  CLEAR: (userId: number) => `/api/cart/${userId}`,
  SUMMARY: (userId: number) => `/api/cart/${userId}/summary`,
}
```

### 3. **Cart Service** ✅

Creato nuovo servizio `src/services/cart.service.ts`:

```typescript
class CartService {
  async getCart(userId: number): Promise<CartResponse>
  async addItem(userId: number, request: AddToCartRequest): Promise<CartResponse>
  async updateItemQuantity(userId: number, productId: number, request: UpdateCartItemRequest): Promise<CartResponse>
  async removeItem(userId: number, productId: number): Promise<CartResponse>
  async clearCart(userId: number): Promise<void>
  async getCartSummary(userId: number): Promise<CartSummaryResponse>
}
```

Esportato in `src/services/index.ts`.

---

## 🔧 Integrazione con Store Zustand

Il frontend ha già uno store Zustand per il carrello (`src/store/slices/cartSlice.ts`) che gestisce il carrello **localmente** in localStorage.

### Opzioni di Integrazione

Ci sono due approcci per integrare il backend:

#### **Opzione 1: Dual Mode (Locale + Backend)**
- Utenti non autenticati: carrello in localStorage (attuale)
- Utenti autenticati: sincronizzazione con backend tramite `cartService`

#### **Opzione 2: Backend Only**
- Tutti i carrelli gestiti tramite backend
- Utenti non autenticati: carrello guest con ID temporaneo

### Implementazione Consigliata: Opzione 1 (Dual Mode)

Aggiornare `cartSlice.ts` per sincronizzare con backend quando l'utente è autenticato:

```typescript
import { cartService } from '../../services';
import { useAuthStore } from './authSlice';

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      // ... stato esistente ...

      addItem: async (product, quantity = 1, attributes) => {
        const user = useAuthStore.getState().user;
        
        // Se utente autenticato, sincronizza con backend
        if (user) {
          try {
            const response = await cartService.addItem(user.id, {
              productId: product.id as number,
              quantity,
            });
            
            // Aggiorna store locale con response backend
            // ... convertire response in formato locale store ...
          } catch (error) {
            console.error('Error syncing cart with backend:', error);
            // Fallback a gestione locale
          }
        }
        
        // Gestione locale (esistente)
        // ... codice attuale ...
      },

      // Ripetere per updateQuantity, removeItem, clearCart
    }),
    { name: API_CONFIG.STORAGE_KEYS.CART }
  )
);
```

---

## 📊 Flusso di Dati

### Utente Non Autenticato
```
Frontend Store (Zustand) <-> localStorage
```

### Utente Autenticato
```
Frontend Store (Zustand) <-> Cart Service (API) <-> Cart Service Backend (8087) <-> MySQL
                    ↓
              localStorage (backup)
```

---

## 🎯 Funzionalità Backend Disponibili

Il Cart Service backend offre:

1. ✅ **Auto-create carrello**: Creato automaticamente al primo accesso
2. ✅ **Snapshot prezzi**: Prezzi salvati al momento aggiunta (non ricalcolati)
3. ✅ **Validazione stock**: Verifica disponibilità via Product Service
4. ✅ **Calcolo sconti**: Supporto per discount_percentage
5. ✅ **Persistenza database**: Carrello persistito in MySQL
6. ✅ **Gestione quantità intelligente**: Se prodotto già presente, incrementa quantità

---

## 🚀 Prossimi Passi

### 1. Aggiornare cartSlice.ts
Modificare `src/store/slices/cartSlice.ts` per:
- Rilevare utente autenticato
- Sincronizzare operazioni con backend
- Gestire errori di rete con fallback locale

### 2. Sincronizzazione al Login
Quando utente fa login:
- Caricare carrello dal backend
- Mergare con carrello locale (se presente)
- Sincronizzare al backend

### 3. Sincronizzazione al Logout
Quando utente fa logout:
- Salvare carrello solo in localStorage
- Opzionalmente svuotare backend (o mantenerlo per prossimo login)

### 4. Gestione Errori
Implementare gestione errori:
- Stock insufficiente
- Prodotto non disponibile
- Errori di rete

---

## 📝 Esempio di Utilizzo

```typescript
import { cartService } from '@/services';

// Aggiungere prodotto al carrello (utente autenticato)
const userId = 1; // Da authStore
const response = await cartService.addItem(userId, {
  productId: 123,
  quantity: 2,
});

// Aggiornare quantità
await cartService.updateItemQuantity(userId, 123, {
  quantity: 5,
});

// Rimuovere prodotto
await cartService.removeItem(userId, 123);

// Ottenere riepilogo carrello
const summary = await cartService.getCartSummary(userId);
console.log(summary.totalFormatted); // "€199.98"

// Svuotare carrello
await cartService.clearCart(userId);
```

---

## ⚠️ Note Importanti

1. **Autenticazione Required**: Gli endpoints del Cart Service richiedono autenticazione
2. **User ID**: Il backend usa `userId` dalla richiesta, non dal token JWT (per semplicità)
3. **Validazione Stock**: Il backend valida automaticamente lo stock prima di aggiungere/aggiornare
4. **Prezzi in Centesimi**: Tutti i prezzi sono in centesimi (Long) per evitare errori di arrotondamento
5. **Product Service Required**: Il Cart Service deve poter comunicare con Product Service

---

## 🔗 Endpoints Disponibili

| Method | Endpoint | Descrizione |
|--------|----------|-------------|
| GET | `/api/cart/{userId}` | Ottieni carrello completo |
| POST | `/api/cart/{userId}/items` | Aggiungi prodotto |
| PUT | `/api/cart/{userId}/items/{productId}` | Aggiorna quantità |
| DELETE | `/api/cart/{userId}/items/{productId}` | Rimuovi prodotto |
| DELETE | `/api/cart/{userId}` | Svuota carrello |
| GET | `/api/cart/{userId}/summary` | Riepilogo carrello |

---

## ✅ Status Integrazione

- [x] Types TypeScript creati
- [x] API config aggiornato
- [x] Cart service creato
- [x] Service esportato in index
- [ ] cartSlice aggiornato con sync backend
- [ ] Gestione login/logout
- [ ] Gestione errori
- [ ] Testing integrazione

---

## 📚 Riferimenti

- Cart Service Backend: [cart-service/README.md](../cart-service/README.md)
- Cart Service API: [cart-service/QUICK_START.md](../cart-service/QUICK_START.md)
- Frontend Store: [src/store/slices/cartSlice.ts](src/store/slices/cartSlice.ts)

