import { Link } from 'react-router-dom';
import { useCartStore } from '../../store';

/**
 * Shopping Cart Page
 */
export function CartPage() {
  const { totalItems, subtotalFormatted } = useCartStore();

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">Shopping Cart</h1>

      {totalItems === 0 ? (
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <p className="text-gray-600 mb-4">Your cart is empty</p>
          <Link
            to="/products"
            className="inline-block bg-primary-600 text-white px-6 py-3 rounded-lg hover:bg-primary-700 transition"
          >
            Continue Shopping
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Cart items */}
          <div className="lg:col-span-2 bg-white rounded-lg shadow p-6">
            <p className="text-gray-600">
              {totalItems} {totalItems === 1 ? 'item' : 'items'} in cart
            </p>
            {/* Cart items will be displayed here */}
          </div>

          {/* Order summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow p-6 sticky top-4">
              <h2 className="text-xl font-semibold mb-4">Order Summary</h2>
              <div className="space-y-2 mb-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">Subtotal</span>
                  <span className="font-semibold">{subtotalFormatted}</span>
                </div>
              </div>
              <button className="w-full bg-primary-600 text-white px-6 py-3 rounded-lg hover:bg-primary-700 transition">
                Proceed to Checkout
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
