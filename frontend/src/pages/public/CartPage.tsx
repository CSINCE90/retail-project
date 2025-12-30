import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useCartStore } from '../../store';
import { Button } from '../../components/ui';

/**
 * Shopping Cart Page
 * Displays cart items with quantity management and checkout
 */
export function CartPage() {
  const navigate = useNavigate();
  const { items, totalItems, subtotalFormatted, updateQuantity, removeItem, clearCart } =
    useCartStore();

  const handleUpdateQuantity = (productId: number | string, newQuantity: number) => {
    if (newQuantity > 0) {
      updateQuantity(productId, newQuantity);
    }
  };

  const handleRemoveItem = (productId: number | string) => {
    removeItem(productId);
  };

  const handleCheckout = () => {
    navigate('/checkout');
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Page Header */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold">Shopping Cart</h1>
        {totalItems > 0 && (
          <button
            onClick={clearCart}
            className="text-red-600 hover:text-red-700 font-medium text-sm"
          >
            Clear Cart
          </button>
        )}
      </div>

      {totalItems === 0 ? (
        /* Empty Cart State */
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-2xl shadow-md p-12 text-center"
        >
          <svg
            className="h-24 w-24 mx-auto text-gray-300 mb-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={1.5}
              d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
            />
          </svg>
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">Your cart is empty</h2>
          <p className="text-gray-600 mb-6">
            Looks like you haven't added any items to your cart yet.
          </p>
          <Link to="/products">
            <Button size="lg">Continue Shopping</Button>
          </Link>
        </motion.div>
      ) : (
        /* Cart with Items */
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Cart Items List */}
          <div className="lg:col-span-2 space-y-4">
            {items.map((item) => (
              <motion.div
                key={item.product.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, x: -100 }}
                className="bg-white rounded-xl shadow-md p-6 flex gap-6"
              >
                {/* Product Image */}
                <Link
                  to={`/products/${item.product.slug}`}
                  className="flex-shrink-0 w-32 h-32 bg-gray-100 rounded-lg overflow-hidden"
                >
                  <img
                    src={item.product.primaryImageUrl}
                    alt={item.product.name}
                    className="w-full h-full object-cover hover:scale-105 transition-transform"
                  />
                </Link>

                {/* Product Info */}
                <div className="flex-1 flex flex-col">
                  {/* Product Name & Category */}
                  <div className="flex-1">
                    <Link to={`/products/${item.product.slug}`}>
                      <h3 className="text-lg font-semibold text-gray-900 hover:text-primary-600 transition">
                        {item.product.name}
                      </h3>
                    </Link>
                    <p className="text-sm text-gray-500 mt-1">{item.product.categoryName}</p>
                    {item.product.brandName && (
                      <p className="text-sm text-gray-400">by {item.product.brandName}</p>
                    )}
                  </div>

                  {/* Price & Quantity Controls */}
                  <div className="flex items-center justify-between mt-4">
                    {/* Price */}
                    <div className="flex flex-col">
                      <span className="text-xl font-bold text-primary-600">
                        {item.product.priceFormatted}
                      </span>
                      {item.product.isOnSale && item.product.compareAtPriceFormatted && (
                        <span className="text-sm text-gray-400 line-through">
                          {item.product.compareAtPriceFormatted}
                        </span>
                      )}
                    </div>

                    {/* Quantity Controls */}
                    <div className="flex items-center gap-4">
                      <div className="flex items-center border-2 border-gray-300 rounded-lg">
                        <button
                          onClick={() => handleUpdateQuantity(item.product.id, item.quantity - 1)}
                          disabled={item.quantity <= 1}
                          className="px-3 py-1 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed font-bold text-lg"
                        >
                          −
                        </button>
                        <span className="px-4 py-1 font-semibold min-w-[3rem] text-center">
                          {item.quantity}
                        </span>
                        <button
                          onClick={() => handleUpdateQuantity(item.product.id, item.quantity + 1)}
                          disabled={item.quantity >= item.product.stockQuantity}
                          className="px-3 py-1 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed font-bold text-lg"
                        >
                          +
                        </button>
                      </div>

                      {/* Remove Button */}
                      <button
                        onClick={() => handleRemoveItem(item.product.id)}
                        className="text-red-600 hover:text-red-700 p-2 rounded-lg hover:bg-red-50 transition"
                        title="Remove item"
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
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                          />
                        </svg>
                      </button>
                    </div>
                  </div>

                  {/* Item Subtotal */}
                  <div className="mt-3 pt-3 border-t">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Item Subtotal:</span>
                      <span className="text-lg font-semibold text-gray-900">
                        €{((item.product.priceCents * item.quantity) / 100).toFixed(2)}
                      </span>
                    </div>
                  </div>
                </div>
              </motion.div>
            ))}
          </div>

          {/* Order Summary Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl shadow-md p-6 sticky top-4">
              <h2 className="text-xl font-semibold mb-6">Order Summary</h2>

              {/* Summary Details */}
              <div className="space-y-3 mb-6">
                <div className="flex justify-between text-gray-600">
                  <span>Items ({totalItems})</span>
                  <span>{subtotalFormatted}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Shipping</span>
                  <span className="text-green-600 font-medium">FREE</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Tax (22%)</span>
                  <span>
                    €
                    {(
                      items.reduce(
                        (total, item) => total + item.product.priceCents * item.quantity,
                        0
                      ) *
                      0.22 /
                      100
                    ).toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Total */}
              <div className="pt-4 border-t-2 mb-6">
                <div className="flex justify-between items-center">
                  <span className="text-lg font-semibold">Total</span>
                  <span className="text-2xl font-bold text-primary-600">
                    €
                    {(
                      items.reduce(
                        (total, item) => total + item.product.priceCents * item.quantity,
                        0
                      ) *
                      1.22 /
                      100
                    ).toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Checkout Button */}
              <Button onClick={handleCheckout} size="lg" fullWidth className="mb-3">
                Proceed to Checkout
              </Button>

              {/* Continue Shopping Link */}
              <Link to="/products">
                <Button variant="outline" size="lg" fullWidth>
                  Continue Shopping
                </Button>
              </Link>

              {/* Trust Badges */}
              <div className="mt-6 pt-6 border-t space-y-3">
                <div className="flex items-center gap-3 text-sm text-gray-600">
                  <svg className="h-5 w-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fillRule="evenodd"
                      d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                      clipRule="evenodd"
                    />
                  </svg>
                  <span>Free shipping on all orders</span>
                </div>
                <div className="flex items-center gap-3 text-sm text-gray-600">
                  <svg className="h-5 w-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fillRule="evenodd"
                      d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                      clipRule="evenodd"
                    />
                  </svg>
                  <span>30-day return guarantee</span>
                </div>
                <div className="flex items-center gap-3 text-sm text-gray-600">
                  <svg className="h-5 w-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fillRule="evenodd"
                      d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                      clipRule="evenodd"
                    />
                  </svg>
                  <span>Secure checkout</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
