import { create } from 'zustand';
import type { User, LoginRequest, RegisterRequest } from '../../types';
import { authService } from '../../services';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  loadUser: () => void;
  clearError: () => void;
  updateProfile: (data: Partial<User>) => Promise<void>;
}

/**
 * Auth Store
 * Manages authentication state
 */
export const useAuthStore = create<AuthState>((set) => ({
  user: authService.getStoredUser(),
  isAuthenticated: authService.isAuthenticated(),
  isLoading: false,
  error: null,

  login: async (credentials) => {
    set({ isLoading: true, error: null });
    try {
      const authData = await authService.login(credentials);
      set({
        user: authData.user,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      });
    } catch (error: any) {
      set({
        isLoading: false,
        error: error.response?.data?.message || 'Login failed. Please try again.',
      });
      throw error;
    }
  },

  register: async (userData) => {
    set({ isLoading: true, error: null });
    try {
      const authData = await authService.register(userData);
      set({
        user: authData.user,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      });
    } catch (error: any) {
      set({
        isLoading: false,
        error: error.response?.data?.message || 'Registration failed. Please try again.',
      });
      throw error;
    }
  },

  logout: async () => {
    set({ isLoading: true });
    try {
      await authService.logout();
      set({
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      });
    } catch (error) {
      set({
        user: null,
        isAuthenticated: false,
        isLoading: false,
      });
    }
  },

  loadUser: () => {
    const user = authService.getStoredUser();
    const isAuthenticated = authService.isAuthenticated();
    set({ user, isAuthenticated });
  },

  clearError: () => {
    set({ error: null });
  },

  updateProfile: async (data) => {
    set({ isLoading: true, error: null });
    try {
      // Mock implementation - replace with actual API call
      const updatedUser = { ...authService.getStoredUser()!, ...data };
      localStorage.setItem('user', JSON.stringify(updatedUser));
      set({
        user: updatedUser,
        isLoading: false,
        error: null,
      });
    } catch (error: any) {
      set({
        isLoading: false,
        error: error.response?.data?.message || 'Update failed. Please try again.',
      });
      throw error;
    }
  },
}));
