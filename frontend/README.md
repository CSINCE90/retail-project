# RetailSports Frontend

Modern React + TypeScript frontend per la piattaforma e-commerce RetailSports.

## 🚀 Tech Stack

- **React** 18
- **TypeScript** 5
- **Vite** 6
- **Tailwind CSS** 3
- **Zustand** (State Management)
- **React Router** 7
- **Framer Motion** (Animations)
- **Axios** (HTTP Client)

---

## 📦 Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Environment Configuration

Il file `.env` contiene configurazioni sensibili e **NON** è tracciato in git.

**Copia il file di esempio:**

```bash
cp .env.example .env
```

**Configurazione predefinita (.env):**

```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080

# App Configuration
VITE_APP_NAME=RetailSports
VITE_APP_DESCRIPTION=Your Sports E-commerce Platform
```

**IMPORTANTE:** 
- Il file `.env` è nel `.gitignore` e non verrà committato
- Modifica `VITE_API_BASE_URL` se l'API Gateway è su un altro indirizzo
- In produzione, imposta le variabili d'ambiente nel sistema di deployment

### 3. Run Development Server

```bash
npm run dev
```

Il frontend sarà disponibile su `http://localhost:5173`

---

## 🏗️ Project Structure

```
frontend/
├── src/
│   ├── components/       # UI Components
│   │   ├── ui/          # Generic UI components (Button, Input, etc.)
│   │   ├── forms/       # Form components (Login, Register)
│   │   ├── product/     # Product-specific components
│   │   └── layout/      # Layout components (Navbar, Footer)
│   │
│   ├── pages/           # Page components
│   │   ├── public/      # Public pages (Home, Products, Cart)
│   │   ├── protected/   # Protected pages (Profile, Addresses)
│   │   └── admin/       # Admin pages (Dashboard, Products)
│   │
│   ├── services/        # API services
│   │   ├── api.service.ts
│   │   ├── auth.service.ts
│   │   ├── product.service.ts
│   │   ├── cart.service.ts
│   │   └── ...
│   │
│   ├── store/           # Zustand state management
│   │   └── slices/      # Store slices
│   │       ├── authSlice.ts
│   │       ├── cartSlice.ts
│   │       └── uiSlice.ts
│   │
│   ├── types/           # TypeScript type definitions
│   ├── config/          # Configuration files
│   ├── routes/          # Route definitions
│   └── App.tsx          # Main App component
│
├── .env.example         # Environment variables template
├── vite.config.ts       # Vite configuration
├── tailwind.config.js   # Tailwind CSS configuration
└── tsconfig.json        # TypeScript configuration
```

---

## 🔌 Backend Integration

### API Gateway

Il frontend comunica con il backend tramite l'**API Gateway** (porta 8080):

```
Frontend (5173) → API Gateway (8080) → Microservices
```

### Microservizi Integrati

1. **User Service** (8081): Autenticazione, profilo, indirizzi
2. **Product Service** (8082): Prodotti, categorie, brand
3. **Cart Service** (8087): Carrello della spesa

### Esempio Utilizzo Servizi

```typescript
import { authService, productService, cartService } from '@/services';

// Login
const response = await authService.login({
  email: 'user@example.com',
  password: 'password123'
});

// Ottenere prodotti
const products = await productService.getProducts();

// Aggiungere al carrello
const cart = await cartService.addItem(userId, {
  productId: 123,
  quantity: 2
});
```

---

## 🛒 Cart Service Integration

Il frontend è integrato con il nuovo **Cart Service** backend.

### Features

- ✅ Sincronizzazione carrello con backend per utenti autenticati
- ✅ Validazione stock automatica
- ✅ Calcolo prezzi e sconti server-side
- ✅ Persistenza in database MySQL
- ✅ Fallback locale (localStorage) per utenti non autenticati

### Documentazione

Per dettagli sull'integrazione del Cart Service:
- [CART_INTEGRATION.md](./CART_INTEGRATION.md) - Guida integrazione completa
- [../cart-service/README.md](../cart-service/README.md) - Backend Cart Service

---

## 🔐 Authentication

### Login Flow

1. User inserisce credenziali
2. `authService.login()` chiama User Service
3. Riceve `accessToken` e `refreshToken`
4. Tokens salvati in `localStorage`
5. `accessToken` inviato in ogni richiesta API (Bearer token)

### Protected Routes

Le route protette richiedono autenticazione:

```typescript
<ProtectedRoute>
  <ProfilePage />
</ProtectedRoute>
```

### Token Refresh

L'API service gestisce automaticamente il refresh del token quando scade.

---

## 🎨 Styling

### Tailwind CSS

Il progetto usa Tailwind CSS per lo styling:

```tsx
<button className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg">
  Add to Cart
</button>
```

### Custom Colors

Definiti in `tailwind.config.js`:

```js
colors: {
  primary: { /* blu */ },
  secondary: { /* arancione */ },
  success: { /* verde */ },
  // ...
}
```

---

## 📱 Pages

### Public Pages

- `/` - Homepage
- `/products` - Lista prodotti
- `/products/:slug` - Dettaglio prodotto
- `/cart` - Carrello
- `/login` - Login
- `/register` - Registrazione

### Protected Pages (Require Auth)

- `/profile` - Profilo utente
- `/addresses` - Gestione indirizzi
- `/orders` - Ordini (WIP)

### Admin Pages (Require ADMIN role)

- `/admin` - Dashboard admin
- `/admin/products` - Gestione prodotti

---

## 🔧 Development

### Available Scripts

```bash
# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint
npm run lint
```

### Environment Variables

Le variabili d'ambiente sono accessibili tramite `import.meta.env`:

```typescript
const apiUrl = import.meta.env.VITE_API_BASE_URL;
```

---

## 📦 Build & Deploy

### Production Build

```bash
npm run build
```

Output in `dist/`

### Preview Build

```bash
npm run preview
```

### Deployment

Il frontend può essere deployato su:
- **Vercel** (consigliato per Vite)
- **Netlify**
- **AWS S3 + CloudFront**
- **Nginx** (static files)

**Configurazione variabili d'ambiente in produzione:**

```env
VITE_API_BASE_URL=https://api.retailsports.com
```

---

## 🧪 Testing (WIP)

```bash
# Unit tests
npm run test

# E2E tests
npm run test:e2e
```

---

## 📚 Documentation

- [Cart Integration](./CART_INTEGRATION.md) - Integrazione Cart Service
- [API Config](./src/config/api.config.ts) - Configurazione endpoints
- [Types](./src/types/index.ts) - Type definitions

---

## ⚠️ Important Notes

1. **Environment File**: Il file `.env` NON è tracciato in git. Usa `.env.example` come template.
2. **API Gateway**: Assicurati che l'API Gateway sia in esecuzione su porta 8080
3. **CORS**: Il backend deve avere CORS configurato per `http://localhost:5173`
4. **Microservices**: Tutti i microservizi necessari devono essere in esecuzione

---

## 🤝 Contributing

1. Crea un branch per le tue modifiche
2. Segui le convenzioni di naming esistenti
3. Testa le modifiche localmente
4. Crea una Pull Request

---

## 📝 License

Proprietary - RetailSports Platform

---

## 🆘 Troubleshooting

### Errore: "Network Error"

Verifica che:
1. API Gateway sia in esecuzione (`http://localhost:8080`)
2. CORS sia configurato correttamente
3. `VITE_API_BASE_URL` in `.env` sia corretto

### Errore: "401 Unauthorized"

1. Verifica che il token sia valido
2. Riprova a fare login
3. Controlla che User Service sia in esecuzione

### Carrello non si sincronizza

1. Verifica che Cart Service sia in esecuzione (porta 8087)
2. Controlla che l'utente sia autenticato
3. Verifica i logs del Cart Service

---

## 📞 Support

Per problemi o domande:
- Consulta la documentazione dei microservizi
- Verifica i logs dei servizi backend
- Controlla la console del browser per errori

