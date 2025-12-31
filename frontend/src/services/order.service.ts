import { apiService } from './api.service';
import API_CONFIG from '../config/api.config';
import type {
  OrderResponse,
  CreateOrderRequest,
  UpdateOrderStatusRequest,
  PageResponse,
  ApiResponse,
} from '../types';

/**
 * Order Service
 * API service for order operations
 */
class OrderService {
  /**
   * Create a new order
   */
  async createOrder(request: CreateOrderRequest): Promise<ApiResponse<OrderResponse>> {
    return apiService.post<ApiResponse<OrderResponse>>(
      API_CONFIG.ENDPOINTS.ORDERS.CREATE,
      request
    );
  }

  /**
   * Get order by ID
   */
  async getOrderById(id: number): Promise<ApiResponse<OrderResponse>> {
    return apiService.get<ApiResponse<OrderResponse>>(
      API_CONFIG.ENDPOINTS.ORDERS.BY_ID(id)
    );
  }

  /**
   * Get order by order number
   */
  async getOrderByNumber(orderNumber: string): Promise<ApiResponse<OrderResponse>> {
    return apiService.get<ApiResponse<OrderResponse>>(
      API_CONFIG.ENDPOINTS.ORDERS.BY_ORDER_NUMBER(orderNumber)
    );
  }

  /**
   * Get user orders with pagination
   */
  async getUserOrders(
    userId: number,
    page: number = 0,
    size: number = 10
  ): Promise<ApiResponse<PageResponse<OrderResponse>>> {
    return apiService.get<ApiResponse<PageResponse<OrderResponse>>>(
      API_CONFIG.ENDPOINTS.ORDERS.BY_USER(userId),
      { page, size }
    );
  }

  /**
   * Get all orders with filters
   */
  async getAllOrders(params: {
    userId?: number;
    status?: string;
    paymentStatus?: string;
    startDate?: string;
    endDate?: string;
    page?: number;
    size?: number;
  }): Promise<ApiResponse<PageResponse<OrderResponse>>> {
    return apiService.get<ApiResponse<PageResponse<OrderResponse>>>(
      API_CONFIG.ENDPOINTS.ORDERS.ALL,
      params
    );
  }

  /**
   * Update order status
   */
  async updateOrderStatus(
    id: number,
    request: UpdateOrderStatusRequest,
    userId?: number,
    isAdmin: boolean = false
  ): Promise<ApiResponse<OrderResponse>> {
    return apiService.put<ApiResponse<OrderResponse>>(
      API_CONFIG.ENDPOINTS.ORDERS.UPDATE_STATUS(id),
      request,
      { userId, isAdmin }
    );
  }

  /**
   * Cancel an order
   */
  async cancelOrder(
    id: number,
    userId: number,
    reason?: string
  ): Promise<ApiResponse<OrderResponse>> {
    return apiService.post<ApiResponse<OrderResponse>>(
      API_CONFIG.ENDPOINTS.ORDERS.CANCEL(id),
      null,
      { userId, reason }
    );
  }
}

// Export singleton instance
export const orderService = new OrderService();
export default orderService;
