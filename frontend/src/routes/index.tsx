import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '../components/layout/MainLayout';
import { ProtectedRoute } from '../components/layout/ProtectedRoute';

// Public pages
import { HomePage } from '../pages/public/HomePage';
import { ProductsPage } from '../pages/public/ProductsPage';
import { ProductDetailPage } from '../pages/public/ProductDetailPage';
import { LoginPage } from '../pages/public/LoginPage';
import { RegisterPage } from '../pages/public/RegisterPage';
import { CartPage } from '../pages/public/CartPage';

// Protected pages
import { ProfilePage } from '../pages/protected/ProfilePage';
import { AddressesPage } from '../pages/protected/AddressesPage';

// Admin pages
import { AdminDashboard } from '../pages/admin/AdminDashboard';
import { AdminProductsPage } from '../pages/admin/AdminProductsPage';
import { AdminStockPage } from '../pages/admin/AdminStockPage';

/**
 * Application Routes Configuration
 */
export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      // Public routes
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: 'products',
        element: <ProductsPage />,
      },
      {
        path: 'products/:slug',
        element: <ProductDetailPage />,
      },
      {
        path: 'login',
        element: <LoginPage />,
      },
      {
        path: 'register',
        element: <RegisterPage />,
      },
      {
        path: 'cart',
        element: <CartPage />,
      },

      // Protected routes (require authentication)
      {
        path: 'profile',
        element: (
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'profile/addresses',
        element: (
          <ProtectedRoute>
            <AddressesPage />
          </ProtectedRoute>
        ),
      },

      // Admin routes (require admin role)
      {
        path: 'admin',
        element: (
          <ProtectedRoute requireAdmin>
            <AdminDashboard />
          </ProtectedRoute>
        ),
      },
      {
        path: 'admin/products',
        element: (
          <ProtectedRoute requireAdmin>
            <AdminProductsPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'admin/stock',
        element: (
          <ProtectedRoute requireAdmin>
            <AdminStockPage />
          </ProtectedRoute>
        ),
      },

      // Catch-all redirect to home
      {
        path: '*',
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);
