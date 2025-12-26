import apiService from './api.service';
import API_CONFIG from '../config/api.config';
import type { ApiResponse, Brand } from '../types';

/**
 * Brand Service
 * Handles all brand-related API calls
 */
class BrandService {
  /**
   * Get all brands
   */
  async getAllBrands(): Promise<Brand[]> {
    const response = await apiService.get<ApiResponse<Brand[]>>(API_CONFIG.ENDPOINTS.BRANDS.ALL);
    return response.data;
  }

  /**
   * Get active brands only
   */
  async getActiveBrands(): Promise<Brand[]> {
    const response = await apiService.get<ApiResponse<Brand[]>>(
      API_CONFIG.ENDPOINTS.BRANDS.ACTIVE
    );
    return response.data;
  }

  /**
   * Get brand by ID
   */
  async getBrandById(id: number): Promise<Brand> {
    const response = await apiService.get<ApiResponse<Brand>>(
      API_CONFIG.ENDPOINTS.BRANDS.BY_ID(id)
    );
    return response.data;
  }

  /**
   * Get brand by slug
   */
  async getBrandBySlug(slug: string): Promise<Brand> {
    const response = await apiService.get<ApiResponse<Brand>>(
      API_CONFIG.ENDPOINTS.BRANDS.BY_SLUG(slug)
    );
    return response.data;
  }
}

export const brandService = new BrandService();
export default brandService;
