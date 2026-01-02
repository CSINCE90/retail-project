import { Link } from 'react-router-dom';

/**
 * Admin Dashboard Page
 */
export function AdminDashboard() {
  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {/* Stats cards */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium">Total Products</h3>
          <p className="text-3xl font-bold mt-2">0</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium">Total Categories</h3>
          <p className="text-3xl font-bold mt-2">0</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium">Total Brands</h3>
          <p className="text-3xl font-bold mt-2">0</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium">Active Discounts</h3>
          <p className="text-3xl font-bold mt-2">0</p>
        </div>
      </div>

      {/* Management Sections */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <Link
          to="/admin/products"
          className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition group"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold">📦 Product Management</h3>
            <svg
              className="w-5 h-5 text-gray-400 group-hover:text-primary-600 transition"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 5l7 7-7 7"
              />
            </svg>
          </div>
          <p className="text-gray-600 text-sm">Gestisci prodotti, categorie e brand</p>
        </Link>

        <Link
          to="/admin/stock"
          className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition group"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold">📊 Stock Management</h3>
            <svg
              className="w-5 h-5 text-gray-400 group-hover:text-primary-600 transition"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 5l7 7-7 7"
              />
            </svg>
          </div>
          <p className="text-gray-600 text-sm">Gestisci giacenze e movimenti stock</p>
        </Link>

        <Link
          to="/admin"
          className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition group opacity-50 cursor-not-allowed"
          onClick={(e) => e.preventDefault()}
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold">📋 Order Management</h3>
            <svg
              className="w-5 h-5 text-gray-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 5l7 7-7 7"
              />
            </svg>
          </div>
          <p className="text-gray-600 text-sm">Coming soon...</p>
        </Link>
      </div>

      {/* Quick actions */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-semibold mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link
            to="/admin/products"
            className="px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition text-center"
          >
            Add Product
          </Link>
          <button className="px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
            Add Category
          </button>
          <button className="px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
            Add Brand
          </button>
          <Link
            to="/admin/stock"
            className="px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition text-center"
          >
            Manage Stock
          </Link>
        </div>
      </div>
    </div>
  );
}
