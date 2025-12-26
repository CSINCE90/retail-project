import { Outlet } from 'react-router-dom';

/**
 * Main Layout Component
 * Wrapper for all pages with navbar and footer
 */
export function MainLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      {/* Navbar will be added here */}
      <header className="bg-white shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold text-primary-600">RetailSports</h1>
            <nav className="flex items-center space-x-6">
              <a href="/" className="text-gray-700 hover:text-primary-600">
                Home
              </a>
              <a href="/products" className="text-gray-700 hover:text-primary-600">
                Products
              </a>
              <a href="/cart" className="text-gray-700 hover:text-primary-600">
                Cart
              </a>
              <a href="/login" className="text-gray-700 hover:text-primary-600">
                Login
              </a>
            </nav>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Footer will be added here */}
      <footer className="bg-gray-900 text-white py-8 mt-auto">
        <div className="container mx-auto px-4 text-center">
          <p>&copy; 2025 RetailSports. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
