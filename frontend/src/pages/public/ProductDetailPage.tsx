import { useParams } from 'react-router-dom';

/**
 * Product Detail Page
 */
export function ProductDetailPage() {
  const { slug } = useParams<{ slug: string }>();

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Product images */}
        <div className="bg-gray-100 rounded-lg h-96 flex items-center justify-center">
          <p className="text-gray-500">Product Image Gallery</p>
        </div>

        {/* Product info */}
        <div>
          <h1 className="text-3xl font-bold mb-4">Product: {slug}</h1>
          <p className="text-gray-600 mb-6">Product details will be loaded here</p>

          <button className="bg-primary-600 text-white px-6 py-3 rounded-lg hover:bg-primary-700 transition">
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
}
