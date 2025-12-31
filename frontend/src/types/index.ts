// ========== USER TYPES ==========

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  enabled: boolean;
  emailVerified: boolean;
  roles: string[];
  createdAt: string;
  updatedAt: string;
  addresses?: Address[];
}

export const UserRole = {
  USER: 'USER',
  ADMIN: 'ADMIN',
} as const;

export type UserRole = typeof UserRole[keyof typeof UserRole];

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
}

// ========== ADDRESS TYPES ==========

export interface Address {
  id: number;
  firstName: string;
  lastName: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  phoneNumber?: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AddressRequest {
  firstName: string;
  lastName: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  phoneNumber?: string;
  isDefault?: boolean;
}

// ========== PRODUCT TYPES ==========

export interface Product {
  id: number;
  sku: string;
  name: string;
  slug: string;
  description: string;
  shortDescription?: string;
  priceCents: number;
  compareAtPriceCents?: number;
  costCents?: number;
  priceFormatted: string;
  compareAtPriceFormatted?: string;
  discountPercentage?: number;
  stockQuantity: number;
  lowStockThreshold: number;
  weight?: number;
  length?: number;
  width?: number;
  height?: number;
  isActive: boolean;
  isFeatured: boolean;
  isNew: boolean;
  isOnSale: boolean;
  viewCount: number;
  metaTitle?: string;
  metaDescription?: string;
  metaKeywords?: string;
  category: Category;
  brand?: Brand;
  images: ProductImage[];
  tags: Tag[];
  discounts: Discount[];
  attributes: ProductAttributeValue[];
  createdAt: string;
  updatedAt: string;
}

export interface ProductSummary {
  id: number | string;
  sku: string;
  name: string;
  slug: string;
  shortDescription?: string;
  priceCents: number;
  compareAtPriceCents?: number;
  priceFormatted: string;
  compareAtPriceFormatted?: string;
  discountPercentage?: number;
  stockQuantity: number;
  isActive: boolean;
  isFeatured: boolean;
  isNew: boolean;
  isOnSale: boolean;
  primaryImageUrl?: string;
  categoryName: string;
  brandName?: string;
}

export interface ProductDetail {
  id: string;
  slug: string;
  name: string;
  shortDescription: string;
  fullDescription: string;
  priceFormatted: string;
  priceCents: number;
  compareAtPriceFormatted?: string;
  compareAtPriceCents?: number;
  isOnSale: boolean;
  discountPercentage?: number;
  isNew: boolean;
  isFeatured: boolean;
  stockQuantity: number;
  categoryName: string;
  brandName?: string;
  primaryImageUrl: string;
  imageUrls: string[];
  sizes?: string[];
  colors?: string[];
  specifications?: Record<string, string>;
}

export interface ProductImage {
  id: number;
  imageUrl: string;
  altText?: string;
  isPrimary: boolean;
  displayOrder: number;
}

// ========== CATEGORY TYPES ==========

export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  imageUrl?: string;
  displayOrder: number;
  isActive: boolean;
  parentId?: number;
  parentName?: string;
  level: number;
  productCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryTree extends Category {
  children: CategoryTree[];
}

// ========== BRAND TYPES ==========

export interface Brand {
  id: number;
  name: string;
  slug: string;
  description?: string;
  logoUrl?: string;
  websiteUrl?: string;
  isActive: boolean;
  productCount: number;
  createdAt: string;
  updatedAt: string;
}

// ========== TAG TYPES ==========

export interface Tag {
  id: number;
  name: string;
  slug: string;
  productCount: number;
  createdAt: string;
  updatedAt: string;
}

// ========== DISCOUNT TYPES ==========

export interface Discount {
  id: number;
  name: string;
  code?: string;
  description?: string;
  type: DiscountType;
  value: number;
  startsAt: string;
  endsAt: string;
  maxUses?: number;
  maxUsesPerUser: number;
  currentUses: number;
  minPurchaseAmountCents?: number;
  isActive: boolean;
  isValid: boolean;
  isExpired: boolean;
  remainingUses?: number;
  createdAt: string;
  updatedAt: string;
}

export const DiscountType = {
  PERCENTAGE: 'PERCENTAGE',
  FIXED_AMOUNT: 'FIXED_AMOUNT',
} as const;

export type DiscountType = typeof DiscountType[keyof typeof DiscountType];

// ========== PRODUCT ATTRIBUTE TYPES ==========

export interface ProductAttribute {
  id: number;
  name: string;
  type: AttributeType;
  displayOrder: number;
}

export interface AttributeValue {
  id: number;
  value: string;
  colorHex?: string;
  displayOrder: number;
}

export interface ProductAttributeValue {
  attributeName: string;
  attributeType: AttributeType;
  valueName: string;
  colorHex?: string;
}

export const AttributeType = {
  COLOR: 'COLOR',
  SIZE: 'SIZE',
  MATERIAL: 'MATERIAL',
  CUSTOM: 'CUSTOM',
} as const;

export type AttributeType = typeof AttributeType[keyof typeof AttributeType];

// ========== API RESPONSE TYPES ==========

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
  isFirst: boolean;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: ValidationError[];
}

export interface ValidationError {
  field: string;
  rejectedValue: any;
  message: string;
}

// ========== SEARCH & FILTER TYPES ==========

export interface ProductSearchParams {
  keyword?: string;
  categoryId?: number;
  brandId?: number;
  minPrice?: number;
  maxPrice?: number;
  tags?: string[];
  isNew?: boolean;
  isFeatured?: boolean;
  isOnSale?: boolean;
  inStock?: boolean;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export interface PriceRange {
  min: number;
  max: number;
}

// ========== CART TYPES ==========

export interface CartItem {
  product: ProductSummary;
  quantity: number;
  selectedAttributes?: Record<string, string>;
}

export interface Cart {
  items: CartItem[];
  totalItems: number;
  subtotalCents: number;
  subtotalFormatted: string;
}

// ========== CART SERVICE BACKEND TYPES ==========

export interface CartItemResponse {
  id: number;
  productId: number;
  quantity: number;
  unitPriceCents: number;
  discountPercentage: number;
  subtotalCents: number;
  discountAmountCents: number;
  finalPriceCents: number;
  unitPriceFormatted: string;
  subtotalFormatted: string;
  finalPriceFormatted: string;
  addedAt: string;
  updatedAt: string;
}

export interface CartResponse {
  id: number;
  userId: number;
  items: CartItemResponse[];
  totalItems: number;
  subtotalCents: number;
  totalDiscountCents: number;
  totalCents: number;
  subtotalFormatted: string;
  totalDiscountFormatted: string;
  totalFormatted: string;
  createdAt: string;
  updatedAt: string;
}

export interface CartSummaryResponse {
  id: number;
  userId: number;
  totalItems: number;
  uniqueProducts: number;
  subtotalCents: number;
  totalDiscountCents: number;
  totalCents: number;
  subtotalFormatted: string;
  totalDiscountFormatted: string;
  totalFormatted: string;
}

export interface AddToCartRequest {
  productId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

// ========== ORDER TYPES ==========

export const OrderStatus = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  PROCESSING: 'PROCESSING',
  SHIPPED: 'SHIPPED',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED',
  REFUNDED: 'REFUNDED',
} as const;

export type OrderStatus = typeof OrderStatus[keyof typeof OrderStatus];

export const PaymentStatus = {
  PENDING: 'PENDING',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED',
} as const;

export type PaymentStatus = typeof PaymentStatus[keyof typeof PaymentStatus];

export const PaymentMethod = {
  CREDIT_CARD: 'CREDIT_CARD',
  PAYPAL: 'PAYPAL',
  BANK_TRANSFER: 'BANK_TRANSFER',
  CASH_ON_DELIVERY: 'CASH_ON_DELIVERY',
} as const;

export type PaymentMethod = typeof PaymentMethod[keyof typeof PaymentMethod];

export interface OrderItemResponse {
  id: number;
  productId: number;
  productName: string;
  productSku?: string;
  productImage?: string;
  quantity: number;
  unitPriceCents: number;
  discountPercentage: number;
  discountCents: number;
  subtotalCents: number;
  totalCents: number;
  unitPriceFormatted: string;
  totalFormatted: string;
  createdAt: string;
}

export interface OrderResponse {
  id: number;
  orderNumber: string;
  userId: number;
  subtotalCents: number;
  discountCents: number;
  shippingCents: number;
  taxCents: number;
  totalCents: number;
  subtotalFormatted: string;
  totalFormatted: string;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  paymentMethod?: PaymentMethod;
  shippingAddressLine1: string;
  shippingAddressLine2?: string;
  shippingCity: string;
  shippingState?: string;
  shippingPostalCode: string;
  shippingCountry: string;
  billingAddressLine1: string;
  billingAddressLine2?: string;
  billingCity: string;
  billingState?: string;
  billingPostalCode: string;
  billingCountry: string;
  customerNotes?: string;
  adminNotes?: string;
  trackingNumber?: string;
  createdAt: string;
  updatedAt: string;
  confirmedAt?: string;
  shippedAt?: string;
  deliveredAt?: string;
  cancelledAt?: string;
  items: OrderItemResponse[];
  totalItems: number;
  canBeCancelled: boolean;
  canBeRefunded: boolean;
}

export interface CreateOrderRequest {
  userId: number;
  items: OrderItemRequest[];
  paymentMethod: PaymentMethod;
  shippingAddressLine1: string;
  shippingAddressLine2?: string;
  shippingCity: string;
  shippingState?: string;
  shippingPostalCode: string;
  shippingCountry: string;
  billingAddressLine1: string;
  billingAddressLine2?: string;
  billingCity: string;
  billingState?: string;
  billingPostalCode: string;
  billingCountry: string;
  customerNotes?: string;
  shippingCents?: number;
  discountCents?: number;
}

export interface OrderItemRequest {
  productId: number;
  productName: string;
  productSku?: string;
  productImage?: string;
  quantity: number;
  unitPriceCents: number;
  discountPercentage?: number;
  discountCents?: number;
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
  notes?: string;
  trackingNumber?: string;
}

// ========== PAYMENT TYPES ==========

export interface PaymentResponse {
  id: number;
  orderId: number;
  paymentMethod: PaymentMethod;
  amountCents: number;
  currency: string;
  amountFormatted: string;
  status: PaymentStatus;
  transactionId?: string;
  paymentGateway?: string;
  cardLast4?: string;
  cardBrand?: string;
  notes?: string;
  errorMessage?: string;
  createdAt: string;
  completedAt?: string;
  failedAt?: string;
  canBeRefunded: boolean;
}

export interface ProcessPaymentRequest {
  orderId: number;
  paymentMethod: PaymentMethod;
  amountCents: number;
  currency?: string;
  cardLast4?: string;
  cardBrand?: string;
  transactionId?: string;
  paymentGateway?: string;
  notes?: string;
}
