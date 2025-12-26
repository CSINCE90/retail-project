import apiService from './api.service';
import API_CONFIG from '../config/api.config';
import type { Address, AddressRequest, ApiResponse, User } from '../types';

/**
 * User Service
 * Handles all user-related API calls
 */
class UserService {
  /**
   * Get user profile
   */
  async getProfile(): Promise<User> {
    const response = await apiService.get<ApiResponse<User>>(
      API_CONFIG.ENDPOINTS.USERS.PROFILE
    );
    return response.data;
  }

  /**
   * Update user profile
   */
  async updateProfile(data: Partial<User>): Promise<User> {
    const response = await apiService.put<ApiResponse<User>>(
      API_CONFIG.ENDPOINTS.USERS.UPDATE_PROFILE,
      data
    );
    return response.data;
  }

  /**
   * Change password
   */
  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await apiService.post<ApiResponse<void>>(API_CONFIG.ENDPOINTS.USERS.CHANGE_PASSWORD, {
      currentPassword,
      newPassword,
    });
  }

  /**
   * Get all user addresses
   */
  async getAddresses(): Promise<Address[]> {
    const response = await apiService.get<ApiResponse<Address[]>>(
      API_CONFIG.ENDPOINTS.USERS.ADDRESSES
    );
    return response.data;
  }

  /**
   * Get address by ID
   */
  async getAddressById(id: number): Promise<Address> {
    const response = await apiService.get<ApiResponse<Address>>(
      API_CONFIG.ENDPOINTS.USERS.ADDRESS_BY_ID(id)
    );
    return response.data;
  }

  /**
   * Create new address
   */
  async createAddress(data: AddressRequest): Promise<Address> {
    const response = await apiService.post<ApiResponse<Address>>(
      API_CONFIG.ENDPOINTS.USERS.ADDRESSES,
      data
    );
    return response.data;
  }

  /**
   * Update address
   */
  async updateAddress(id: number, data: AddressRequest): Promise<Address> {
    const response = await apiService.put<ApiResponse<Address>>(
      API_CONFIG.ENDPOINTS.USERS.ADDRESS_BY_ID(id),
      data
    );
    return response.data;
  }

  /**
   * Delete address
   */
  async deleteAddress(id: number): Promise<void> {
    await apiService.delete<ApiResponse<void>>(API_CONFIG.ENDPOINTS.USERS.ADDRESS_BY_ID(id));
  }

  /**
   * Set default address
   */
  async setDefaultAddress(id: number): Promise<Address> {
    const response = await apiService.put<ApiResponse<Address>>(
      API_CONFIG.ENDPOINTS.USERS.SET_DEFAULT_ADDRESS(id)
    );
    return response.data;
  }
}

export const userService = new UserService();
export default userService;
