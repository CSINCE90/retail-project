// ========== USER TYPES ==========

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  role: UserRole;
  isActive: boolean;
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
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
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber?: string;
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
  id: number;
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
