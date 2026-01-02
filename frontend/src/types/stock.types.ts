/**
 * Stock Types
 * Definizioni TypeScript per Stock Service
 */

export enum MovementType {
  IN = 'IN',
  OUT = 'OUT',
  RESERVE = 'RESERVE',
  RELEASE = 'RELEASE',
  TRANSFER = 'TRANSFER',
  ADJUSTMENT = 'ADJUSTMENT',
  RETURN = 'RETURN',
}

export enum ReferenceType {
  ORDER = 'ORDER',
  PURCHASE = 'PURCHASE',
  MANUAL = 'MANUAL',
  TRANSFER = 'TRANSFER',
  RETURN = 'RETURN',
}

export enum ReservationStatus {
  ACTIVE = 'ACTIVE',
  CONFIRMED = 'CONFIRMED',
  RELEASED = 'RELEASED',
  EXPIRED = 'EXPIRED',
}

export enum AlertStatus {
  ACTIVE = 'ACTIVE',
  RESOLVED = 'RESOLVED',
}

/**
 * Stock Response
 */
export interface StockResponse {
  id: number;
  productId: number;
  productName?: string;
  availableQuantity: number;
  reservedQuantity: number;
  physicalQuantity: number;
  minimumQuantity: number;
  isLowStock: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Stock Movement Response
 */
export interface StockMovementResponse {
  id: number;
  productId: number;
  movementType: MovementType;
  quantity: number;
  previousQuantity: number;
  newQuantity: number;
  referenceType?: ReferenceType;
  referenceId?: number;
  notes?: string;
  createdByUserId?: number;
  createdAt: string;
}

/**
 * Stock Reservation Response
 */
export interface ReservationResponse {
  id: number;
  productId: number;
  orderId: number;
  quantity: number;
  status: ReservationStatus;
  expiresAt?: string;
  createdAt: string;
  updatedAt: string;
  confirmedAt?: string;
  releasedAt?: string;
}

/**
 * Low Stock Alert Response
 */
export interface LowStockAlertResponse {
  id: number;
  productId: number;
  productName?: string;
  availableQuantity: number;
  minimumQuantity: number;
  alertStatus: AlertStatus;
  createdAt: string;
  resolvedAt?: string;
}

/**
 * Stock Adjustment Request
 */
export interface StockAdjustmentRequest {
  movementType: MovementType;
  quantity: number;
  referenceType?: ReferenceType;
  referenceId?: number;
  notes?: string;
  userId?: number;
}

/**
 * Reserve Stock Request
 */
export interface ReserveStockRequest {
  productId: number;
  orderId: number;
  quantity: number;
}

/**
 * Create Stock Request
 */
export interface CreateStockRequest {
  productId: number;
  initialQuantity: number;
  minimumQuantity?: number;
}

/**
 * Update Minimum Quantity Request
 */
export interface UpdateMinimumQuantityRequest {
  minimumQuantity: number;
}

/**
 * API Response wrapper
 */
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}

/**
 * Page Response for paginated data
 */
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
