import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuthStore, useCartStore, useUIStore } from '../../store';
import { UserRole } from '../../types';

/**
 * Navbar Component
 * Main navigation bar with search, cart, and user menu
 */
export function Navbar() {
  const { isAuthenticated, user, logout } = useAuthStore();
  const { totalItems } = useCartStore();
  const { toggleMobileMenu } = useUIStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <nav className="bg-white shadow-md sticky top-0 z-30">
      {/* Top bar */}
      <div className="bg-primary-600 text-white py-2">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between text-sm">
            <div className="flex items-center space-x-4">
              <span>📧 info@retailsports.com</span>
              <span>📞 +39 123 456 7890</span>
            </div>
            <div className="flex items-center space-x-4">
              {isAuthenticated && user?.role === UserRole.ADMIN && (
                <Link to="/admin" className="hover:text-primary-100 transition">
                  🔧 Admin Panel
                </Link>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Main navbar */}
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <motion.div
              whileHover={{ scale: 1.05 }}
              className="text-2xl font-bold text-primary-600"
            >
              ⚽ RetailSports
            </motion.div>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-8">
            <Link
              to="/"
              className="text-gray-700 hover:text-primary-600 font-medium transition"
            >
              Home
            </Link>
            <Link
              to="/products"
              className="text-gray-700 hover:text-primary-600 font-medium transition"
            >
              Products
            </Link>
            <Link
              to="/products?featured=true"
              className="text-gray-700 hover:text-primary-600 font-medium transition"
            >
              Featured
            </Link>
            <Link
              to="/products?onSale=true"
              className="text-gray-700 hover:text-primary-600 font-medium transition"
            >
              Sale 🔥
            </Link>
          </div>

          {/* Search bar */}
          <div className="hidden lg:flex flex-1 max-w-md mx-8">
            <div className="relative w-full">
              <input
                type="text"
                placeholder="Search products..."
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
              <svg
                className="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>
            </div>
          </div>

          {/* Right side icons */}
          <div className="flex items-center space-x-4">
            {/* Cart */}
            <Link to="/cart" className="relative group">
              <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.95 }}>
                <svg
                  className="h-6 w-6 text-gray-700 group-hover:text-primary-600 transition"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
                {totalItems > 0 && (
                  <span className="absolute -top-2 -right-2 bg-secondary-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                    {totalItems}
                  </span>
                )}
              </motion.div>
            </Link>

            {/* User menu */}
            {isAuthenticated ? (
              <div className="relative group">
                <button className="flex items-center space-x-2 text-gray-700 hover:text-primary-600 transition">
                  <svg
                    className="h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                  <span className="hidden lg:inline font-medium">
                    {user?.firstName}
                  </span>
                </button>

                {/* Dropdown menu */}
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200">
                  <Link
                    to="/profile"
                    className="block px-4 py-2 text-gray-700 hover:bg-gray-100 transition"
                  >
                    👤 My Profile
                  </Link>
                  <Link
                    to="/profile/addresses"
                    className="block px-4 py-2 text-gray-700 hover:bg-gray-100 transition"
                  >
                    📍 My Addresses
                  </Link>
                  <hr className="my-2" />
                  <button
                    onClick={handleLogout}
                    className="block w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100 transition"
                  >
                    🚪 Logout
                  </button>
                </div>
              </div>
            ) : (
              <Link
                to="/login"
                className="hidden md:inline-flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition font-medium"
              >
                Sign In
              </Link>
            )}

            {/* Mobile menu button */}
            <button
              onClick={toggleMobileMenu}
              className="md:hidden text-gray-700 hover:text-primary-600"
            >
              <svg
                className="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>

      {/* Mobile search */}
      <div className="lg:hidden border-t px-4 py-3">
        <input
          type="text"
          placeholder="Search products..."
          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
        />
      </div>
    </nav>
  );
}
