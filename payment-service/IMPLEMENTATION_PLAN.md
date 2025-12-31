# Payment Service - Implementation Plan

## 📋 Overview

Payment Service per gestire pagamenti, transazioni e metodi di pagamento nell'architettura RetailSports.

---

## 🗄️ Database Schema

### Database: `retailsports_payment`

#### Table: `payments`
```sql
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    amount_cents BIGINT NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_gateway VARCHAR(50),
    transaction_id VARCHAR(255),
    description TEXT,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

#### Table: `payment_transactions`
```sql
CREATE TABLE payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount_cents BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    gateway_response TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE,
    INDEX idx_payment_id (payment_id),
    INDEX idx_type (transaction_type)
);
```

#### Table: `payment_methods` (User saved payment methods)
```sql
CREATE TABLE payment_methods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    provider VARCHAR(50),
    last_four VARCHAR(4),
    card_brand VARCHAR(50),
    expiry_month INT,
    expiry_year INT,
    is_default BOOLEAN DEFAULT FALSE,
    token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
);
```

---

## 🎯 Funzionalità

### Phase 1: Core Payment Processing
1. ✅ Creare payment
2. ✅ Processare payment (integrazione gateway simulata)
3. ✅ Verificare payment status
4. ✅ Ottenere payment history per utente
5. ✅ Gestione refund

### Phase 2: Payment Methods (Future)
- Salvare metodi di pagamento
- Gestire carte salvate
- Set default payment method

### Phase 3: Gateway Integration (Future)
- Stripe integration
- PayPal integration
- Webhook handlers

---

## 📦 Entities

### 1. Payment
```java
- id: Long
- orderId: Long (UNIQUE, NOT NULL)
- userId: Long (NOT NULL)
- amountCents: Long (NOT NULL)
- currency: String (default "EUR")
- status: PaymentStatus (ENUM)
- paymentMethod: PaymentMethod (ENUM)
- paymentGateway: String
- transactionId: String
- description: String
- metadata: String (JSON as String)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- completedAt: LocalDateTime
- transactions: List<PaymentTransaction>
```

### 2. PaymentTransaction
```java
- id: Long
- payment: Payment
- transactionType: TransactionType (ENUM)
- amountCents: Long
- status: TransactionStatus (ENUM)
- gatewayResponse: String (TEXT)
- errorMessage: String
- createdAt: LocalDateTime
```

### 3. PaymentMethod (Future)
```java
- id: Long
- userId: Long
- type: String
- provider: String
- lastFour: String
- cardBrand: String
- expiryMonth: Integer
- expiryYear: Integer
- isDefault: Boolean
- token: String (encrypted)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

---

## 🔢 Enums

### PaymentStatus
```java
PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
```

### PaymentMethod
```java
CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER, CASH_ON_DELIVERY
```

### TransactionType
```java
AUTHORIZATION, CAPTURE, REFUND, VOID
```

### TransactionStatus
```java
SUCCESS, FAILED, PENDING
```

---

## 🛣️ REST API Endpoints

### Public/Customer Endpoints

#### Create Payment
```
POST /api/payments
Body: {
  "orderId": 123,
  "amountCents": 9999,
  "currency": "EUR",
  "paymentMethod": "CREDIT_CARD",
  "description": "Order #123 payment"
}
```

#### Process Payment
```
POST /api/payments/{id}/process
Body: {
  "paymentGateway": "STRIPE",
  "token": "tok_visa_test"
}
```

#### Get Payment by ID
```
GET /api/payments/{id}
```

#### Get Payment by Order ID
```
GET /api/payments/order/{orderId}
```

#### Get User Payments
```
GET /api/payments/user/{userId}
```

#### Request Refund
```
POST /api/payments/{id}/refund
Body: {
  "amountCents": 9999,  // partial or full
  "reason": "Customer requested"
}
```

### Admin Endpoints

#### Get All Payments (paginated)
```
GET /api/admin/payments?page=0&size=20
```

#### Update Payment Status
```
PUT /api/admin/payments/{id}/status
Body: {
  "status": "COMPLETED"
}
```

---

## 🏗️ Package Structure

```
com.retailsports.payment_service/
├── config/
│   └── PaymentConfig.java
├── controller/
│   ├── PaymentController.java
│   └── AdminPaymentController.java
├── dto/
│   ├── request/
│   │   ├── CreatePaymentRequest.java
│   │   ├── ProcessPaymentRequest.java
│   │   └── RefundRequest.java
│   └── response/
│       ├── PaymentResponse.java
│       ├── PaymentSummaryResponse.java
│       ├── TransactionResponse.java
│       └── ErrorResponse.java
├── entity/
│   ├── Payment.java
│   ├── PaymentTransaction.java
│   └── PaymentMethod.java (future)
├── enums/
│   ├── PaymentStatus.java
│   ├── PaymentMethodType.java
│   ├── TransactionType.java
│   └── TransactionStatus.java
├── exception/
│   ├── PaymentNotFoundException.java
│   ├── InvalidPaymentStateException.java
│   ├── PaymentProcessingException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── PaymentRepository.java
│   ├── PaymentTransactionRepository.java
│   └── PaymentMethodRepository.java (future)
├── service/
│   ├── PaymentService.java (interface)
│   ├── PaymentServiceImpl.java
│   └── PaymentGatewayService.java (mock implementation)
└── util/
    └── PaymentUtils.java
```

---

## 🔧 Implementation Details

### Business Rules

1. **Payment Creation**:
   - Validare orderId (deve essere unico)
   - Validare amount > 0
   - Status iniziale: PENDING

2. **Payment Processing**:
   - Solo PENDING payments possono essere processati
   - Creare PaymentTransaction per ogni tentativo
   - Su successo: status → COMPLETED
   - Su fallimento: status → FAILED

3. **Refund**:
   - Solo COMPLETED payments possono essere rimborsati
   - Supportare partial e full refund
   - Creare PaymentTransaction type=REFUND
   - Aggiornare status: REFUNDED o PARTIALLY_REFUNDED

4. **Prezzi in Centesimi**:
   - Tutti gli importi in LONG (centesimi)
   - Evitare Float/Double

---

## 🔌 Integration Points

### Future Integrations

1. **Order Service**: Verificare orderId valido
2. **User Service**: Verificare userId valido
3. **Notification Service**: Inviare notifiche payment status

---

## 🧪 Testing Strategy

### Mock Payment Gateway
Creare mock implementation per testing:
- Simulare successo/fallimento
- Simulare delays
- Testare edge cases

---

## 📝 Phase 1 Deliverables

- [x] Database schema SQL
- [ ] Application class con @EnableDiscoveryClient
- [ ] Entities con enums
- [ ] Repositories
- [ ] DTOs con validations
- [ ] Exceptions
- [ ] PaymentService interface + implementation
- [ ] Mock PaymentGatewayService
- [ ] PaymentController
- [ ] AdminPaymentController
- [ ] GlobalExceptionHandler
- [ ] application.yaml configurato
- [ ] README.md completo
- [ ] API documentation

---

## 🚀 Future Enhancements (Phase 2+)

- Stripe SDK integration
- PayPal SDK integration
- Webhook handlers
- Payment methods CRUD
- Recurring payments
- Invoice generation
- Payment analytics
- Fraud detection

