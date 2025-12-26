import apiService from './api.service';
import API_CONFIG from '../config/api.config';
import type { ApiResponse, Category, CategoryTree } from '../types';

/**
 * Category Service
 * Handles all category-related API calls
 */
class CategoryService {
  /**
   * Get all categories
   */
  async getAllCategories(): Promise<Category[]> {
    const response = await apiService.get<ApiResponse<Category[]>>(
      API_CONFIG.ENDPOINTS.CATEGORIES.ALL
    );
    return response.data;
  }

  /**
   * Get active categories only
   */
  async getActiveCategories(): Promise<Category[]> {
    const response = await apiService.get<ApiResponse<Category[]>>(
      API_CONFIG.ENDPOINTS.CATEGORIES.ACTIVE
    );
    return response.data;
  }

  /**
   * Get category by ID
   */
  async getCategoryById(id: number): Promise<Category> {
    const response = await apiService.get<ApiResponse<Category>>(
      API_CONFIG.ENDPOINTS.CATEGORIES.BY_ID(id)
    );
    return response.data;
  }

  /**
   * Get category by slug
   */
  async getCategoryBySlug(slug: string): Promise<Category> {
    const response = await apiService.get<ApiResponse<Category>>(
      API_CONFIG.ENDPOINTS.CATEGORIES.BY_SLUG(slug)
    );
    return response.data;
  }

  /**
   * Get category tree (hierarchical structure)
   */
  async getCategoryTree(): Promise<CategoryTree[]> {
    const response = await apiService.get<ApiResponse<CategoryTree[]>>(
      API_CONFIG.ENDPOINTS.CATEGORIES.TREE
    );
    return response.data;
  }

  /**
   * Get root categories (level 1)
   */
  async getRootCategories(): Promise<Category[]> {
    const response = await apiService.get<ApiResponse<Category[]>>(
      API_CONFIG.ENDPOINTS.CATEGORIES.ROOT
    );
    return response.data;
  }

  /**
   * Get subcategories of a category
   */
  async getSubcategories(id: number): Promise<Category[]> {
    const response = await apiService.get<ApiResponse<Category[]>>(
      API_CONFIG.ENDPOINTS.CATEGORIES.SUBCATEGORIES(id)
    );
    return response.data;
  }
}

export const categoryService = new CategoryService();
export default categoryService;
