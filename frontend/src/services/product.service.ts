import apiService from './api.service';
import API_CONFIG from '../config/api.config';
import type { ApiResponse, PageResponse, Product, ProductSearchParams, ProductSummary } from '../types';

/**
 * Product Service
 * Handles all product-related API calls
 */
class ProductService {
  /**
   * Get all products with pagination
   */
  async getAllProducts(page = 0, size = 12): Promise<PageResponse<ProductSummary>> {
    const response = await apiService.get<ApiResponse<PageResponse<ProductSummary>>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.ALL,
      {
        params: { page, size },
      }
    );
    return response.data;
  }

  /**
   * Search products with filters
   */
  async searchProducts(params: ProductSearchParams): Promise<PageResponse<ProductSummary>> {
    const response = await apiService.get<ApiResponse<PageResponse<ProductSummary>>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.SEARCH,
      { params }
    );
    return response.data;
  }

  /**
   * Get product by ID
   */
  async getProductById(id: number): Promise<Product> {
    const response = await apiService.get<ApiResponse<Product>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.BY_ID(id)
    );
    return response.data;
  }

  /**
   * Get product by slug
   */
  async getProductBySlug(slug: string): Promise<Product> {
    const response = await apiService.get<ApiResponse<Product>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.BY_SLUG(slug)
    );
    return response.data;
  }

  /**
   * Get products by category
   */
  async getProductsByCategory(
    categoryId: number,
    page = 0,
    size = 12
  ): Promise<PageResponse<ProductSummary>> {
    const response = await apiService.get<ApiResponse<PageResponse<ProductSummary>>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.BY_CATEGORY(categoryId),
      {
        params: { page, size },
      }
    );
    return response.data;
  }

  /**
   * Get products by brand
   */
  async getProductsByBrand(
    brandId: number,
    page = 0,
    size = 12
  ): Promise<PageResponse<ProductSummary>> {
    const response = await apiService.get<ApiResponse<PageResponse<ProductSummary>>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.BY_BRAND(brandId),
      {
        params: { page, size },
      }
    );
    return response.data;
  }

  /**
   * Get featured products
   */
  async getFeaturedProducts(page = 0, size = 12): Promise<PageResponse<ProductSummary>> {
    const response = await apiService.get<ApiResponse<PageResponse<ProductSummary>>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.FEATURED,
      {
        params: { page, size },
      }
    );
    return response.data;
  }

  /**
   * Get new products
   */
  async getNewProducts(page = 0, size = 12): Promise<PageResponse<ProductSummary>> {
    const response = await apiService.get<ApiResponse<PageResponse<ProductSummary>>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.NEW,
      {
        params: { page, size },
      }
    );
    return response.data;
  }

  /**
   * Get on-sale products
   */
  async getOnSaleProducts(page = 0, size = 12): Promise<PageResponse<ProductSummary>> {
    const response = await apiService.get<ApiResponse<PageResponse<ProductSummary>>>(
      API_CONFIG.ENDPOINTS.PRODUCTS.ON_SALE,
      {
        params: { page, size },
      }
    );
    return response.data;
  }
}

export const productService = new ProductService();
export default productService;
