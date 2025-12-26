import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { Cart, CartItem, ProductSummary } from '../../types';
import API_CONFIG from '../../config/api.config';

interface CartState extends Cart {
  // Actions
  addItem: (product: ProductSummary, quantity?: number, attributes?: Record<string, string>) => void;
  removeItem: (productId: number) => void;
  updateQuantity: (productId: number, quantity: number) => void;
  clearCart: () => void;
  getItemQuantity: (productId: number) => number;
}

/**
 * Cart Store
 * Manages shopping cart state with localStorage persistence
 */
export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],
      totalItems: 0,
      subtotalCents: 0,
      subtotalFormatted: '€0.00',

      addItem: (product, quantity = 1, attributes) => {
        const { items } = get();

        // Check if product already exists in cart
        const existingItemIndex = items.findIndex((item) => item.product.id === product.id);

        let updatedItems: CartItem[];

        if (existingItemIndex >= 0) {
          // Update quantity if product exists
          updatedItems = items.map((item, index) =>
            index === existingItemIndex
              ? {
                  ...item,
                  quantity: item.quantity + quantity,
                  selectedAttributes: attributes || item.selectedAttributes,
                }
              : item
          );
        } else {
          // Add new item
          updatedItems = [
            ...items,
            {
              product,
              quantity,
              selectedAttributes: attributes,
            },
          ];
        }

        // Recalculate totals
        const totals = calculateTotals(updatedItems);

        set({
          items: updatedItems,
          ...totals,
        });
      },

      removeItem: (productId) => {
        const { items } = get();
        const updatedItems = items.filter((item) => item.product.id !== productId);

        const totals = calculateTotals(updatedItems);

        set({
          items: updatedItems,
          ...totals,
        });
      },

      updateQuantity: (productId, quantity) => {
        const { items } = get();

        if (quantity <= 0) {
          get().removeItem(productId);
          return;
        }

        const updatedItems = items.map((item) =>
          item.product.id === productId ? { ...item, quantity } : item
        );

        const totals = calculateTotals(updatedItems);

        set({
          items: updatedItems,
          ...totals,
        });
      },

      clearCart: () => {
        set({
          items: [],
          totalItems: 0,
          subtotalCents: 0,
          subtotalFormatted: '€0.00',
        });
      },

      getItemQuantity: (productId) => {
        const { items } = get();
        const item = items.find((item) => item.product.id === productId);
        return item?.quantity || 0;
      },
    }),
    {
      name: API_CONFIG.STORAGE_KEYS.CART,
    }
  )
);

/**
 * Helper function to calculate cart totals
 */
function calculateTotals(items: CartItem[]): {
  totalItems: number;
  subtotalCents: number;
  subtotalFormatted: string;
} {
  const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);

  const subtotalCents = items.reduce((sum, item) => {
    return sum + item.product.priceCents * item.quantity;
  }, 0);

  const subtotalFormatted = formatPrice(subtotalCents);

  return {
    totalItems,
    subtotalCents,
    subtotalFormatted,
  };
}

/**
 * Helper function to format price in cents to currency string
 */
function formatPrice(cents: number): string {
  const euros = cents / 100;
  return new Intl.NumberFormat('it-IT', {
    style: 'currency',
    currency: 'EUR',
  }).format(euros);
}
