import { apiService } from './api.service';
import type {
  StockResponse,
  StockMovementResponse,
  ReservationResponse,
  LowStockAlertResponse,
  StockAdjustmentRequest,
  ReserveStockRequest,
  CreateStockRequest,
  UpdateMinimumQuantityRequest,
  ApiResponse,
  PageResponse,
} from '../types/stock.types';

/**
 * Stock Service
 * Handles all stock-related API calls
 */
class StockService {
  private readonly BASE_PATH = '/stock-service/api';

  // ========== STOCK MANAGEMENT ==========

  /**
   * Get stock by product ID
   */
  async getStockByProductId(productId: number): Promise<StockResponse> {
    return apiService.get<StockResponse>(`${this.BASE_PATH}/stock/${productId}`);
  }

  /**
   * Get all stock with pagination
   */
  async getAllStock(page = 0, size = 20): Promise<PageResponse<StockResponse>> {
    return apiService.get<PageResponse<StockResponse>>(
      `${this.BASE_PATH}/stock?page=${page}&size=${size}`
    );
  }

  /**
   * Adjust stock quantity
   */
  async adjustStock(
    productId: number,
    request: StockAdjustmentRequest
  ): Promise<ApiResponse<StockResponse>> {
    return apiService.post<ApiResponse<StockResponse>>(
      `${this.BASE_PATH}/stock/${productId}/adjust`,
      request
    );
  }

  /**
   * Get stock movements for product
   */
  async getMovementsByProductId(
    productId: number,
    page = 0,
    size = 20
  ): Promise<PageResponse<StockMovementResponse>> {
    return apiService.get<PageResponse<StockMovementResponse>>(
      `${this.BASE_PATH}/stock/${productId}/movements?page=${page}&size=${size}`
    );
  }

  /**
   * Get low stock products
   */
  async getLowStockProducts(): Promise<StockResponse[]> {
    return apiService.get<StockResponse[]>(`${this.BASE_PATH}/stock/low-stock`);
  }

  // ========== RESERVATIONS ==========

  /**
   * Reserve stock for order
   */
  async reserveStock(request: ReserveStockRequest): Promise<ApiResponse<ReservationResponse>> {
    return apiService.post<ApiResponse<ReservationResponse>>(
      `${this.BASE_PATH}/stock/reserve`,
      request
    );
  }

  /**
   * Confirm reservation (order paid)
   */
  async confirmReservation(reservationId: number): Promise<ApiResponse<ReservationResponse>> {
    return apiService.post<ApiResponse<ReservationResponse>>(
      `${this.BASE_PATH}/stock/confirm/${reservationId}`
    );
  }

  /**
   * Release reservation (order cancelled)
   */
  async releaseReservation(reservationId: number): Promise<ApiResponse<ReservationResponse>> {
    return apiService.post<ApiResponse<ReservationResponse>>(
      `${this.BASE_PATH}/stock/release/${reservationId}`
    );
  }

  /**
   * Get reservations by order ID
   */
  async getReservationsByOrderId(orderId: number): Promise<ReservationResponse[]> {
    return apiService.get<ReservationResponse[]>(
      `${this.BASE_PATH}/stock/reservations/${orderId}`
    );
  }

  // ========== ADMIN ==========

  /**
   * Create stock for new product
   */
  async createStock(request: CreateStockRequest): Promise<ApiResponse<StockResponse>> {
    return apiService.post<ApiResponse<StockResponse>>(
      `${this.BASE_PATH}/admin/stock`,
      request
    );
  }

  /**
   * Update minimum quantity threshold
   */
  async updateMinimumQuantity(
    productId: number,
    request: UpdateMinimumQuantityRequest
  ): Promise<ApiResponse<StockResponse>> {
    return apiService.put<ApiResponse<StockResponse>>(
      `${this.BASE_PATH}/admin/stock/${productId}/minimum`,
      request
    );
  }

  /**
   * Get active low stock alerts
   */
  async getActiveLowStockAlerts(): Promise<LowStockAlertResponse[]> {
    return apiService.get<LowStockAlertResponse[]>(`${this.BASE_PATH}/admin/stock/alerts`);
  }
}

// Export singleton instance
export const stockService = new StockService();
export default stockService;
