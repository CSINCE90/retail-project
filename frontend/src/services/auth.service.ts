import apiService from './api.service';
import API_CONFIG from '../config/api.config';
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, User } from '../types';

/**
 * Authentication Service
 * Handles all authentication-related API calls
 */
class AuthService {
  /**
   * Login user
   */
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await apiService.post<ApiResponse<AuthResponse>>(
      API_CONFIG.ENDPOINTS.AUTH.LOGIN,
      credentials
    );

    // Store tokens and user in localStorage
    this.storeAuthData(response.data);

    return response.data;
  }

  /**
   * Register new user
   */
  async register(userData: RegisterRequest): Promise<User> {
    const response = await apiService.post<ApiResponse<User>>(
      API_CONFIG.ENDPOINTS.AUTH.REGISTER,
      userData
    );

    // Registration returns only user data (no tokens)
    // User needs to verify email and then login
    return response.data;
  }

  /**
   * Logout user
   */
  async logout(): Promise<void> {
    try {
      await apiService.post(API_CONFIG.ENDPOINTS.AUTH.LOGOUT);
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      this.clearAuthData();
    }
  }

  /**
   * Refresh access token
   */
  async refreshToken(): Promise<string> {
    const refreshToken = localStorage.getItem(API_CONFIG.STORAGE_KEYS.REFRESH_TOKEN);

    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await apiService.post<ApiResponse<{ accessToken: string }>>(
      API_CONFIG.ENDPOINTS.AUTH.REFRESH,
      { refreshToken }
    );

    const { accessToken } = response.data;

    localStorage.setItem(API_CONFIG.STORAGE_KEYS.ACCESS_TOKEN, accessToken);

    return accessToken;
  }

  /**
   * Get current user profile
   */
  async getCurrentUser(): Promise<User> {
    const response = await apiService.get<ApiResponse<User>>(API_CONFIG.ENDPOINTS.AUTH.ME);
    return response.data;
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return !!localStorage.getItem(API_CONFIG.STORAGE_KEYS.ACCESS_TOKEN);
  }

  /**
   * Get stored user
   */
  getStoredUser(): User | null {
    const userStr = localStorage.getItem(API_CONFIG.STORAGE_KEYS.USER);
    if (!userStr) return null;

    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }

  /**
   * Get access token
   */
  getAccessToken(): string | null {
    return localStorage.getItem(API_CONFIG.STORAGE_KEYS.ACCESS_TOKEN);
  }

  /**
   * Get refresh token
   */
  getRefreshToken(): string | null {
    return localStorage.getItem(API_CONFIG.STORAGE_KEYS.REFRESH_TOKEN);
  }

  /**
   * Store authentication data in localStorage
   */
  private storeAuthData(authData: AuthResponse): void {
    localStorage.setItem(API_CONFIG.STORAGE_KEYS.ACCESS_TOKEN, authData.accessToken);
    localStorage.setItem(API_CONFIG.STORAGE_KEYS.REFRESH_TOKEN, authData.refreshToken);
    localStorage.setItem(API_CONFIG.STORAGE_KEYS.USER, JSON.stringify(authData.user));
  }

  /**
   * Clear authentication data from localStorage
   */
  private clearAuthData(): void {
    localStorage.removeItem(API_CONFIG.STORAGE_KEYS.ACCESS_TOKEN);
    localStorage.removeItem(API_CONFIG.STORAGE_KEYS.REFRESH_TOKEN);
    localStorage.removeItem(API_CONFIG.STORAGE_KEYS.USER);
  }
}

export const authService = new AuthService();
export default authService;
