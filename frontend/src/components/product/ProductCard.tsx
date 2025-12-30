import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import type { ProductSummary } from '../../types';
import { useCartStore } from '../../store';
import { useToast } from '../../hooks';
import { Badge } from '../ui/Badge';

interface ProductCardProps {
  product: ProductSummary;
}

/**
 * Product Card Component
 * Displays product summary in grid/list view
 */
export function ProductCard({ product }: ProductCardProps) {
  const { addItem } = useCartStore();
  const { success } = useToast();

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    addItem(product, 1);
    success(`${product.name} added to cart!`);
  };

  const imageUrl = product.primaryImageUrl || 'https://via.placeholder.com/400x400?text=No+Image';
  const isOnSale = product.isOnSale && product.compareAtPriceCents;
  const discountPercent = product.discountPercentage;

  return (
    <Link to={`/products/${product.slug}`}>
      <motion.div
        whileHover={{ y: -4 }}
        className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-all duration-300 h-full flex flex-col"
      >
        {/* Image Container */}
        <div className="relative h-64 bg-gray-100 overflow-hidden">
          <img
            src={imageUrl}
            alt={product.name}
            className="w-full h-full object-cover transition-transform duration-300 hover:scale-110"
          />

          {/* Badges */}
          <div className="absolute top-3 left-3 flex flex-col gap-2">
            {product.isNew && <Badge variant="info">New</Badge>}
            {product.isFeatured && <Badge variant="warning">Featured</Badge>}
            {isOnSale && discountPercent && (
              <Badge variant="danger">-{discountPercent}%</Badge>
            )}
          </div>

          {/* Stock status */}
          {product.stockQuantity === 0 && (
            <div className="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center">
              <span className="text-white font-semibold text-lg">Out of Stock</span>
            </div>
          )}
        </div>

        {/* Content */}
        <div className="p-4 flex-1 flex flex-col">
          {/* Category & Brand */}
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs text-gray-500 uppercase tracking-wide">
              {product.categoryName}
            </span>
            {product.brandName && (
              <span className="text-xs text-gray-400">{product.brandName}</span>
            )}
          </div>

          {/* Product Name */}
          <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2 flex-1">
            {product.name}
          </h3>

          {/* Short Description */}
          {product.shortDescription && (
            <p className="text-sm text-gray-600 mb-3 line-clamp-2">
              {product.shortDescription}
            </p>
          )}

          {/* Price & Add to Cart */}
          <div className="flex items-center justify-between mt-auto pt-3 border-t">
            <div className="flex flex-col">
              <span className="text-2xl font-bold text-primary-600">
                {product.priceFormatted}
              </span>
              {isOnSale && product.compareAtPriceFormatted && (
                <span className="text-sm text-gray-400 line-through">
                  {product.compareAtPriceFormatted}
                </span>
              )}
            </div>

            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={handleAddToCart}
              disabled={product.stockQuantity === 0}
              className={`
                p-3 rounded-lg transition-colors
                ${
                  product.stockQuantity > 0
                    ? 'bg-primary-600 text-white hover:bg-primary-700'
                    : 'bg-gray-200 text-gray-400 cursor-not-allowed'
                }
              `}
            >
              <svg
                className="h-5 w-5"
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
            </motion.button>
          </div>
        </div>
      </motion.div>
    </Link>
  );
}
