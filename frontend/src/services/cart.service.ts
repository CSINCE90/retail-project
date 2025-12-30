import { apiService } from './api.service';
import API_CONFIG from '../config/api.config';
import type {
  CartResponse,
  CartSummaryResponse,
  AddToCartRequest,
  UpdateCartItemRequest,
} from '../types';

/**
 * Cart Service
 * API service for cart operations with backend integration
 */
class CartService {
  /**
   * Get cart for a user
   */
  async getCart(userId: number): Promise<CartResponse> {
    return apiService.get<CartResponse>(API_CONFIG.ENDPOINTS.CART.GET(userId));
  }

  /**
   * Add item to cart
   */
  async addItem(userId: number, request: AddToCartRequest): Promise<CartResponse> {
    return apiService.post<CartResponse>(
      API_CONFIG.ENDPOINTS.CART.ADD_ITEM(userId),
      request
    );
  }

  /**
   * Update cart item quantity
   */
  async updateItemQuantity(
    userId: number,
    productId: number,
    request: UpdateCartItemRequest
  ): Promise<CartResponse> {
    return apiService.put<CartResponse>(
      API_CONFIG.ENDPOINTS.CART.UPDATE_ITEM(userId, productId),
      request
    );
  }

  /**
   * Remove item from cart
   */
  async removeItem(userId: number, productId: number): Promise<CartResponse> {
    return apiService.delete<CartResponse>(
      API_CONFIG.ENDPOINTS.CART.REMOVE_ITEM(userId, productId)
    );
  }

  /**
   * Clear cart
   */
  async clearCart(userId: number): Promise<void> {
    return apiService.delete<void>(API_CONFIG.ENDPOINTS.CART.CLEAR(userId));
  }

  /**
   * Get cart summary
   */
  async getCartSummary(userId: number): Promise<CartSummaryResponse> {
    return apiService.get<CartSummaryResponse>(
      API_CONFIG.ENDPOINTS.CART.SUMMARY(userId)
    );
  }
}

// Export singleton instance
export const cartService = new CartService();
export default cartService;
