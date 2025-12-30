import { useState } from 'react';
import { motion } from 'framer-motion';
import { ProductCard } from '../../components/product/ProductCard';
import { Spinner } from '../../components/ui/Spinner';
import type { ProductSummary } from '../../types';

/**
 * Products Catalog Page
 */
export function ProductsPage() {
  const [isLoading] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedBrand, setSelectedBrand] = useState<string>('all');
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 1000]);
  const [sortBy, setSortBy] = useState<string>('featured');

  // Mock products - in produzione questi verranno dal backend
  const mockProducts: ProductSummary[] = [
    {
      id: 1,
      sku: 'BALL-001',
      name: 'Professional Football',
      slug: 'professional-football',
      shortDescription: 'Official size 5 match ball',
      priceCents: 2999,
      priceFormatted: '€29.99',
      stockQuantity: 15,
      isActive: true,
      isFeatured: true,
      isNew: false,
      isOnSale: false,
      primaryImageUrl: 'https://images.unsplash.com/photo-1614632537197-38a17061c2bd?w=400',
      categoryName: 'Football',
      brandName: 'Nike',
    },
    {
      id: 2,
      sku: 'SHOE-001',
      name: 'Running Shoes Pro',
      slug: 'running-shoes-pro',
      shortDescription: 'Lightweight performance running shoes',
      priceCents: 8999,
      compareAtPriceCents: 12999,
      priceFormatted: '€89.99',
      compareAtPriceFormatted: '€129.99',
      discountPercentage: 31,
      stockQuantity: 8,
      isActive: true,
      isFeatured: false,
      isNew: true,
      isOnSale: true,
      primaryImageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400',
      categoryName: 'Running',
      brandName: 'Adidas',
    },
    {
      id: 3,
      sku: 'BASKET-001',
      name: 'Basketball Official Size',
      slug: 'basketball-official',
      shortDescription: 'Indoor/outdoor composite leather',
      priceCents: 3499,
      priceFormatted: '€34.99',
      stockQuantity: 20,
      isActive: true,
      isFeatured: true,
      isNew: false,
      isOnSale: false,
      primaryImageUrl: 'https://images.unsplash.com/photo-1546438664-39ab5b69c394?w=400',
      categoryName: 'Basketball',
      brandName: 'Spalding',
    },
    {
      id: 4,
      sku: 'TENNIS-001',
      name: 'Tennis Racket Carbon',
      slug: 'tennis-racket-carbon',
      shortDescription: 'Professional carbon fiber racket',
      priceCents: 15999,
      compareAtPriceCents: 19999,
      priceFormatted: '€159.99',
      compareAtPriceFormatted: '€199.99',
      discountPercentage: 20,
      stockQuantity: 5,
      isActive: true,
      isFeatured: false,
      isNew: false,
      isOnSale: true,
      primaryImageUrl: 'https://images.unsplash.com/photo-1617083369382-355b91a393f5?w=400',
      categoryName: 'Tennis',
      brandName: 'Wilson',
    },
    {
      id: 5,
      sku: 'GYM-001',
      name: 'Yoga Mat Premium',
      slug: 'yoga-mat-premium',
      shortDescription: 'Non-slip eco-friendly yoga mat',
      priceCents: 4999,
      priceFormatted: '€49.99',
      stockQuantity: 0,
      isActive: true,
      isFeatured: false,
      isNew: true,
      isOnSale: false,
      primaryImageUrl: 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400',
      categoryName: 'Gym',
    },
    {
      id: 6,
      sku: 'BIKE-001',
      name: 'Mountain Bike Helmet',
      slug: 'mountain-bike-helmet',
      shortDescription: 'Lightweight protective helmet with vents',
      priceCents: 6999,
      priceFormatted: '€69.99',
      stockQuantity: 12,
      isActive: true,
      isFeatured: true,
      isNew: true,
      isOnSale: false,
      primaryImageUrl: 'https://images.unsplash.com/photo-1557838923-2985c318be48?w=400',
      categoryName: 'Cycling',
      brandName: 'Giro',
    },
  ];

  const categories = ['All', 'Football', 'Basketball', 'Tennis', 'Running', 'Gym', 'Cycling'];
  const brands = ['All', 'Nike', 'Adidas', 'Spalding', 'Wilson', 'Giro'];
  const sortOptions = [
    { value: 'featured', label: 'Featured' },
    { value: 'price-asc', label: 'Price: Low to High' },
    { value: 'price-desc', label: 'Price: High to Low' },
    { value: 'name', label: 'Name: A-Z' },
    { value: 'newest', label: 'Newest' },
  ];

  return (
    <div className="bg-gray-50 min-h-screen py-8">
      <div className="container mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Shop All Products</h1>
          <p className="text-gray-600">
            Discover our complete collection of sports equipment and apparel
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Sidebar Filters */}
          <aside className="lg:col-span-1">
            <div className="bg-white rounded-xl shadow-md p-6 sticky top-24">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Filters</h2>

              {/* Category Filter */}
              <div className="mb-6">
                <h3 className="font-medium text-gray-900 mb-3">Category</h3>
                <div className="space-y-2">
                  {categories.map((category) => (
                    <label key={category} className="flex items-center cursor-pointer group">
                      <input
                        type="radio"
                        name="category"
                        value={category.toLowerCase()}
                        checked={selectedCategory === category.toLowerCase()}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                        className="h-4 w-4 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="ml-2 text-gray-700 group-hover:text-primary-600 transition">
                        {category}
                      </span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Brand Filter */}
              <div className="mb-6">
                <h3 className="font-medium text-gray-900 mb-3">Brand</h3>
                <div className="space-y-2">
                  {brands.map((brand) => (
                    <label key={brand} className="flex items-center cursor-pointer group">
                      <input
                        type="radio"
                        name="brand"
                        value={brand.toLowerCase()}
                        checked={selectedBrand === brand.toLowerCase()}
                        onChange={(e) => setSelectedBrand(e.target.value)}
                        className="h-4 w-4 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="ml-2 text-gray-700 group-hover:text-primary-600 transition">
                        {brand}
                      </span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Price Range */}
              <div className="mb-6">
                <h3 className="font-medium text-gray-900 mb-3">Price Range</h3>
                <div className="space-y-3">
                  <input
                    type="range"
                    min="0"
                    max="1000"
                    value={priceRange[1]}
                    onChange={(e) => setPriceRange([0, parseInt(e.target.value)])}
                    className="w-full"
                  />
                  <div className="flex items-center justify-between text-sm text-gray-600">
                    <span>€0</span>
                    <span>€{priceRange[1]}</span>
                  </div>
                </div>
              </div>

              {/* Reset Filters */}
              <button
                onClick={() => {
                  setSelectedCategory('all');
                  setSelectedBrand('all');
                  setPriceRange([0, 1000]);
                }}
                className="w-full py-2 px-4 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
              >
                Reset Filters
              </button>
            </div>
          </aside>

          {/* Products Grid */}
          <main className="lg:col-span-3">
            {/* Toolbar */}
            <div className="flex items-center justify-between mb-6 bg-white rounded-xl shadow-md p-4">
              <p className="text-gray-600">
                Showing <span className="font-semibold">{mockProducts.length}</span> products
              </p>

              <div className="flex items-center space-x-2">
                <label className="text-sm text-gray-600">Sort by:</label>
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  {sortOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Products */}
            {isLoading ? (
              <div className="flex justify-center items-center py-20">
                <Spinner size="xl" />
              </div>
            ) : (
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.5 }}
                className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6"
              >
                {mockProducts.map((product, index) => (
                  <motion.div
                    key={product.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.3, delay: index * 0.1 }}
                  >
                    <ProductCard product={product} />
                  </motion.div>
                ))}
              </motion.div>
            )}

            {/* Pagination */}
            <div className="mt-8 flex justify-center">
              <nav className="flex items-center space-x-2">
                <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
                  Previous
                </button>
                <button className="px-4 py-2 bg-primary-600 text-white rounded-lg">1</button>
                <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
                  2
                </button>
                <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
                  3
                </button>
                <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
                  Next
                </button>
              </nav>
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}
