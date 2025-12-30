import { useParams, useNavigate, Link } from 'react-router-dom';
import { useState } from 'react';
import { motion } from 'framer-motion';
import type { ProductDetail } from '../../types';
import { useCartStore } from '../../store';
import { useToast } from '../../hooks';
import { Badge, Button } from '../../components/ui';

/**
 * Product Detail Page
 * Displays full product information with image gallery, variants, and purchase options
 */
export function ProductDetailPage() {
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();
  const { addItem } = useCartStore();
  const { success } = useToast();

  // State
  const [selectedImage, setSelectedImage] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [selectedSize, setSelectedSize] = useState<string>('');
  const [selectedColor, setSelectedColor] = useState<string>('');

  // Mock product data (replace with API call)
  const product: ProductDetail = {
    id: '1',
    slug: slug || '',
    name: 'Professional Football',
    shortDescription: 'High-quality match football',
    fullDescription: 'This professional-grade football is designed for competitive play. Made with premium materials for superior durability and performance. Features advanced aerodynamic design for consistent flight and accurate passing.',
    priceFormatted: '€29.99',
    priceCents: 2999,
    compareAtPriceFormatted: '€49.99',
    compareAtPriceCents: 4999,
    isOnSale: true,
    discountPercentage: 40,
    isNew: false,
    isFeatured: true,
    stockQuantity: 15,
    categoryName: 'Football',
    brandName: 'Nike',
    primaryImageUrl: 'https://via.placeholder.com/600x600?text=Football',
    imageUrls: [
      'https://via.placeholder.com/600x600?text=Football+1',
      'https://via.placeholder.com/600x600?text=Football+2',
      'https://via.placeholder.com/600x600?text=Football+3',
      'https://via.placeholder.com/600x600?text=Football+4',
    ],
    sizes: ['Size 3', 'Size 4', 'Size 5'],
    colors: ['White', 'Black', 'Red'],
    specifications: {
      Material: 'Synthetic Leather',
      Weight: '410-450g',
      Circumference: '68-70cm',
      'Suitable For': 'Professional Play',
    },
  };

  const handleAddToCart = () => {
    if (product.sizes && product.sizes.length > 0 && !selectedSize) {
      useToast().error('Please select a size');
      return;
    }
    if (product.colors && product.colors.length > 0 && !selectedColor) {
      useToast().error('Please select a color');
      return;
    }

    addItem(
      {
        id: product.id,
        sku: 'SKU-001',
        slug: product.slug,
        name: product.name,
        shortDescription: product.shortDescription,
        priceFormatted: product.priceFormatted,
        priceCents: product.priceCents,
        compareAtPriceFormatted: product.compareAtPriceFormatted,
        compareAtPriceCents: product.compareAtPriceCents,
        isOnSale: product.isOnSale,
        discountPercentage: product.discountPercentage,
        isNew: product.isNew,
        isFeatured: product.isFeatured,
        stockQuantity: product.stockQuantity,
        categoryName: product.categoryName,
        brandName: product.brandName,
        primaryImageUrl: product.primaryImageUrl,
        isActive: true,
      },
      quantity
    );
    success(`${product.name} added to cart!`);
  };

  const handleBuyNow = () => {
    handleAddToCart();
    navigate('/cart');
  };

  const decrementQuantity = () => {
    if (quantity > 1) setQuantity(quantity - 1);
  };

  const incrementQuantity = () => {
    if (quantity < product.stockQuantity) setQuantity(quantity + 1);
  };

  const isOnSale = product.isOnSale && product.compareAtPriceCents;

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Breadcrumb */}
      <nav className="mb-6 text-sm">
        <Link to="/" className="text-gray-500 hover:text-gray-700">
          Home
        </Link>
        <span className="mx-2 text-gray-400">/</span>
        <Link to="/products" className="text-gray-500 hover:text-gray-700">
          Products
        </Link>
        <span className="mx-2 text-gray-400">/</span>
        <span className="text-gray-900">{product.categoryName}</span>
        <span className="mx-2 text-gray-400">/</span>
        <span className="text-gray-900">{product.name}</span>
      </nav>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
        {/* Left Column - Image Gallery */}
        <div>
          {/* Main Image */}
          <motion.div
            key={selectedImage}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="bg-gray-100 rounded-2xl overflow-hidden mb-4 aspect-square"
          >
            <img
              src={product.imageUrls[selectedImage]}
              alt={product.name}
              className="w-full h-full object-cover"
            />
          </motion.div>

          {/* Thumbnail Gallery */}
          <div className="grid grid-cols-4 gap-4">
            {product.imageUrls.map((img: string, idx: number) => (
              <button
                key={idx}
                onClick={() => setSelectedImage(idx)}
                className={`
                  aspect-square rounded-lg overflow-hidden border-2 transition
                  ${
                    selectedImage === idx
                      ? 'border-primary-600'
                      : 'border-transparent hover:border-gray-300'
                  }
                `}
              >
                <img src={img} alt={`${product.name} ${idx + 1}`} className="w-full h-full object-cover" />
              </button>
            ))}
          </div>
        </div>

        {/* Right Column - Product Info */}
        <div>
          {/* Badges */}
          <div className="flex gap-2 mb-4">
            {product.isNew && <Badge variant="info">New</Badge>}
            {product.isFeatured && <Badge variant="warning">Featured</Badge>}
            {isOnSale && product.discountPercentage && (
              <Badge variant="danger">-{product.discountPercentage}%</Badge>
            )}
          </div>

          {/* Product Name & Brand */}
          <h1 className="text-4xl font-bold text-gray-900 mb-2">{product.name}</h1>
          {product.brandName && (
            <p className="text-lg text-gray-500 mb-4">by {product.brandName}</p>
          )}

          {/* Price */}
          <div className="mb-6">
            <div className="flex items-baseline gap-3">
              <span className="text-4xl font-bold text-primary-600">
                {product.priceFormatted}
              </span>
              {isOnSale && product.compareAtPriceFormatted && (
                <span className="text-2xl text-gray-400 line-through">
                  {product.compareAtPriceFormatted}
                </span>
              )}
            </div>
            {isOnSale && (
              <p className="text-green-600 font-medium mt-1">
                You save {product.discountPercentage}%!
              </p>
            )}
          </div>

          {/* Short Description */}
          <p className="text-gray-700 mb-6 leading-relaxed">{product.shortDescription}</p>

          {/* Stock Status */}
          <div className="mb-6">
            {product.stockQuantity > 0 ? (
              <p className="text-green-600 font-medium">
                ✓ In Stock ({product.stockQuantity} available)
              </p>
            ) : (
              <p className="text-red-600 font-medium">✗ Out of Stock</p>
            )}
          </div>

          {/* Size Selection */}
          {product.sizes && product.sizes.length > 0 && (
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Size: {selectedSize && <span className="text-primary-600">{selectedSize}</span>}
              </label>
              <div className="flex gap-2 flex-wrap">
                {product.sizes.map((size: string) => (
                  <button
                    key={size}
                    onClick={() => setSelectedSize(size)}
                    className={`
                      px-4 py-2 border-2 rounded-lg font-medium transition
                      ${
                        selectedSize === size
                          ? 'border-primary-600 bg-primary-50 text-primary-600'
                          : 'border-gray-300 hover:border-gray-400'
                      }
                    `}
                  >
                    {size}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Color Selection */}
          {product.colors && product.colors.length > 0 && (
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Color: {selectedColor && <span className="text-primary-600">{selectedColor}</span>}
              </label>
              <div className="flex gap-2 flex-wrap">
                {product.colors.map((color: string) => (
                  <button
                    key={color}
                    onClick={() => setSelectedColor(color)}
                    className={`
                      px-4 py-2 border-2 rounded-lg font-medium transition
                      ${
                        selectedColor === color
                          ? 'border-primary-600 bg-primary-50 text-primary-600'
                          : 'border-gray-300 hover:border-gray-400'
                      }
                    `}
                  >
                    {color}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Quantity Selector */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">Quantity</label>
            <div className="flex items-center gap-3">
              <button
                onClick={decrementQuantity}
                disabled={quantity <= 1}
                className="w-10 h-10 rounded-lg border-2 border-gray-300 hover:border-gray-400 disabled:opacity-50 disabled:cursor-not-allowed font-bold"
              >
                −
              </button>
              <span className="text-xl font-semibold w-12 text-center">{quantity}</span>
              <button
                onClick={incrementQuantity}
                disabled={quantity >= product.stockQuantity}
                className="w-10 h-10 rounded-lg border-2 border-gray-300 hover:border-gray-400 disabled:opacity-50 disabled:cursor-not-allowed font-bold"
              >
                +
              </button>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-4 mb-8">
            <Button
              onClick={handleAddToCart}
              disabled={product.stockQuantity === 0}
              size="lg"
              className="flex-1"
            >
              Add to Cart
            </Button>
            <Button
              onClick={handleBuyNow}
              disabled={product.stockQuantity === 0}
              variant="outline"
              size="lg"
              className="flex-1"
            >
              Buy Now
            </Button>
          </div>

          {/* Full Description */}
          <div className="border-t pt-6 mb-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-3">Description</h3>
            <p className="text-gray-700 leading-relaxed">{product.fullDescription}</p>
          </div>

          {/* Specifications */}
          {product.specifications && (
            <div className="border-t pt-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-3">Specifications</h3>
              <dl className="space-y-2">
                {Object.entries(product.specifications).map(([key, value]: [string, string]) => (
                  <div key={key} className="flex">
                    <dt className="text-gray-600 w-1/3">{key}:</dt>
                    <dd className="text-gray-900 font-medium w-2/3">{value}</dd>
                  </div>
                ))}
              </dl>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
