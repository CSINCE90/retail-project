import { apiService } from './api.service';
import API_CONFIG from '../config/api.config';
import type {
  PaymentResponse,
  ProcessPaymentRequest,
  PageResponse,
  ApiResponse,
} from '../types';

/**
 * Payment Service
 * API service for payment operations
 */
class PaymentService {
  /**
   * Process a payment
   */
  async processPayment(request: ProcessPaymentRequest): Promise<ApiResponse<PaymentResponse>> {
    return apiService.post<ApiResponse<PaymentResponse>>(
      API_CONFIG.ENDPOINTS.PAYMENTS.PROCESS,
      request
    );
  }

  /**
   * Get payment by ID
   */
  async getPaymentById(id: number): Promise<ApiResponse<PaymentResponse>> {
    return apiService.get<ApiResponse<PaymentResponse>>(
      API_CONFIG.ENDPOINTS.PAYMENTS.BY_ID(id)
    );
  }

  /**
   * Get payments for an order
   */
  async getPaymentsByOrder(orderId: number): Promise<ApiResponse<PaymentResponse[]>> {
    return apiService.get<ApiResponse<PaymentResponse[]>>(
      API_CONFIG.ENDPOINTS.PAYMENTS.BY_ORDER(orderId)
    );
  }

  /**
   * Get all payments with filters
   */
  async getAllPayments(params: {
    status?: string;
    paymentMethod?: string;
    startDate?: string;
    endDate?: string;
    page?: number;
    size?: number;
  }): Promise<ApiResponse<PageResponse<PaymentResponse>>> {
    return apiService.get<ApiResponse<PageResponse<PaymentResponse>>>(
      API_CONFIG.ENDPOINTS.PAYMENTS.ALL,
      params
    );
  }

  /**
   * Refund a payment
   */
  async refundPayment(id: number, reason?: string): Promise<ApiResponse<PaymentResponse>> {
    return apiService.post<ApiResponse<PaymentResponse>>(
      API_CONFIG.ENDPOINTS.PAYMENTS.REFUND(id),
      null,
      { reason }
    );
  }
}

// Export singleton instance
export const paymentService = new PaymentService();
export default paymentService;
