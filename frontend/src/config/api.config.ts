/**
 * API Configuration
 * Centralized configuration for API endpoints and settings
 */

export const API_CONFIG = {
  // Base URLs
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',

  // Timeout
  TIMEOUT: 30000, // 30 seconds

  // Endpoints
  ENDPOINTS: {
    // Auth endpoints
    AUTH: {
      LOGIN: '/api/auth/login',
      REGISTER: '/api/auth/register',
      REFRESH: '/api/auth/refresh',
      LOGOUT: '/api/auth/logout',
      ME: '/api/auth/me',
    },

    // User endpoints
    USERS: {
      PROFILE: '/api/users/profile',
      UPDATE_PROFILE: '/api/users/profile',
      CHANGE_PASSWORD: '/api/users/change-password',
      ADDRESSES: '/api/users/addresses',
      ADDRESS_BY_ID: (id: number) => `/api/users/addresses/${id}`,
      SET_DEFAULT_ADDRESS: (id: number) => `/api/users/addresses/${id}/set-default`,
    },

    // Product endpoints
    PRODUCTS: {
      ALL: '/api/products',
      SEARCH: '/api/products/search',
      BY_ID: (id: number) => `/api/products/${id}`,
      BY_SLUG: (slug: string) => `/api/products/slug/${slug}`,
      BY_CATEGORY: (categoryId: number) => `/api/products/category/${categoryId}`,
      BY_BRAND: (brandId: number) => `/api/products/brand/${brandId}`,
      FEATURED: '/api/products/featured',
      NEW: '/api/products/new',
      ON_SALE: '/api/products/on-sale',
    },

    // Category endpoints
    CATEGORIES: {
      ALL: '/api/categories',
      ACTIVE: '/api/categories/active',
      BY_ID: (id: number) => `/api/categories/${id}`,
      BY_SLUG: (slug: string) => `/api/categories/slug/${slug}`,
      TREE: '/api/categories/tree',
      ROOT: '/api/categories/root',
      SUBCATEGORIES: (id: number) => `/api/categories/${id}/subcategories`,
    },

    // Brand endpoints
    BRANDS: {
      ALL: '/api/brands',
      ACTIVE: '/api/brands/active',
      BY_ID: (id: number) => `/api/brands/${id}`,
      BY_SLUG: (slug: string) => `/api/brands/slug/${slug}`,
    },

    // Admin Product endpoints
    ADMIN_PRODUCTS: {
      CREATE: '/api/admin/products',
      UPDATE: (id: number) => `/api/admin/products/${id}`,
      DELETE: (id: number) => `/api/admin/products/${id}`,
      RESTORE: (id: number) => `/api/admin/products/${id}/restore`,
      UPDATE_STOCK: (id: number) => `/api/admin/products/${id}/stock`,
      TOGGLE_ACTIVE: (id: number) => `/api/admin/products/${id}/toggle-active`,
    },

    // Admin Category endpoints
    ADMIN_CATEGORIES: {
      CREATE: '/api/admin/categories',
      UPDATE: (id: number) => `/api/admin/categories/${id}`,
      DELETE: (id: number) => `/api/admin/categories/${id}`,
      TOGGLE_ACTIVE: (id: number) => `/api/admin/categories/${id}/toggle-active`,
    },

    // Admin Brand endpoints
    ADMIN_BRANDS: {
      CREATE: '/api/admin/brands',
      UPDATE: (id: number) => `/api/admin/brands/${id}`,
      DELETE: (id: number) => `/api/admin/brands/${id}`,
      TOGGLE_ACTIVE: (id: number) => `/api/admin/brands/${id}/toggle-active`,
    },

    // Admin Discount endpoints
    ADMIN_DISCOUNTS: {
      ALL: '/api/admin/discounts',
      CREATE: '/api/admin/discounts',
      UPDATE: (id: number) => `/api/admin/discounts/${id}`,
      DELETE: (id: number) => `/api/admin/discounts/${id}`,
      BY_ID: (id: number) => `/api/admin/discounts/${id}`,
      ACTIVE: '/api/admin/discounts/active',
      APPLY_TO_PRODUCT: (discountId: number, productId: number) =>
        `/api/admin/discounts/${discountId}/products/${productId}`,
      REMOVE_FROM_PRODUCT: (discountId: number, productId: number) =>
        `/api/admin/discounts/${discountId}/products/${productId}`,
    },
  },

  // Storage keys
  STORAGE_KEYS: {
    ACCESS_TOKEN: 'retailsports_access_token',
    REFRESH_TOKEN: 'retailsports_refresh_token',
    USER: 'retailsports_user',
    CART: 'retailsports_cart',
  },

  // Pagination defaults
  PAGINATION: {
    DEFAULT_PAGE: 0,
    DEFAULT_SIZE: 12,
    SIZE_OPTIONS: [12, 24, 48, 96],
  },
} as const;

export default API_CONFIG;
