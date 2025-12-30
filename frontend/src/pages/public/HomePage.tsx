import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';

/**
 * Home Page
 * Landing page with hero, features, and call-to-actions
 */
export function HomePage() {
  const categories = [
    { name: 'Football', icon: '⚽', color: 'bg-blue-500' },
    { name: 'Basketball', icon: '🏀', color: 'bg-orange-500' },
    { name: 'Tennis', icon: '🎾', color: 'bg-green-500' },
    { name: 'Running', icon: '👟', color: 'bg-red-500' },
    { name: 'Gym', icon: '🏋️', color: 'bg-purple-500' },
    { name: 'Cycling', icon: '🚴', color: 'bg-yellow-500' },
  ];

  const features = [
    {
      icon: '🚚',
      title: 'Free Shipping',
      description: 'On orders over €50',
    },
    {
      icon: '🔄',
      title: 'Easy Returns',
      description: '30-day return policy',
    },
    {
      icon: '💳',
      title: 'Secure Payment',
      description: '100% secure transactions',
    },
    {
      icon: '🎁',
      title: 'Special Offers',
      description: 'Weekly deals and promotions',
    },
  ];

  return (
    <div>
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-primary-600 via-primary-700 to-primary-900 text-white overflow-hidden">
        <div className="absolute inset-0 bg-black opacity-20"></div>
        <div className="absolute inset-0">
          <div className="absolute top-20 left-10 w-72 h-72 bg-primary-400 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob"></div>
          <div className="absolute top-40 right-10 w-72 h-72 bg-secondary-400 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000"></div>
          <div className="absolute -bottom-8 left-40 w-72 h-72 bg-primary-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-4000"></div>
        </div>

        <div className="container mx-auto px-4 py-24 md:py-32 relative z-10">
          <div className="max-w-3xl">
            <motion.h1
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              className="text-5xl md:text-6xl font-bold mb-6 leading-tight"
            >
              Elevate Your Game with Premium Sports Gear
            </motion.h1>
            <motion.p
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.2 }}
              className="text-xl md:text-2xl mb-8 text-primary-100"
            >
              Discover top-quality equipment and apparel for every sport. From beginners to professionals.
            </motion.p>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.4 }}
              className="flex flex-col sm:flex-row gap-4"
            >
              <Link
                to="/products"
                className="inline-flex items-center justify-center px-8 py-4 bg-white text-primary-700 rounded-lg font-semibold text-lg hover:bg-gray-100 transition shadow-lg"
              >
                Shop Now
                <svg className="ml-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
              </Link>
              <Link
                to="/products?onSale=true"
                className="inline-flex items-center justify-center px-8 py-4 border-2 border-white text-white rounded-lg font-semibold text-lg hover:bg-white hover:text-primary-700 transition"
              >
                View Sale 🔥
              </Link>
            </motion.div>
          </div>
        </div>
      </section>

      {/* Categories Section */}
      <section className="py-16 bg-white">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Shop by Sport
            </h2>
            <p className="text-gray-600 text-lg">
              Find the perfect equipment for your favorite activity
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
            {categories.map((category, index) => (
              <motion.div
                key={category.name}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: index * 0.1 }}
              >
                <Link
                  to={`/products?category=${category.name.toLowerCase()}`}
                  className="group"
                >
                  <div className={`${category.color} rounded-2xl p-8 text-center transition transform group-hover:scale-105 group-hover:shadow-xl`}>
                    <div className="text-6xl mb-3">{category.icon}</div>
                    <h3 className="text-white font-semibold text-lg">{category.name}</h3>
                  </div>
                </Link>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {features.map((feature, index) => (
              <motion.div
                key={feature.title}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: index * 0.1 }}
                className="bg-white rounded-lg p-6 text-center shadow-sm hover:shadow-md transition"
              >
                <div className="text-5xl mb-4">{feature.icon}</div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">{feature.title}</h3>
                <p className="text-gray-600">{feature.description}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-gradient-to-r from-primary-600 to-primary-800 text-white">
        <div className="container mx-auto px-4 text-center">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            whileInView={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.6 }}
            viewport={{ once: true }}
          >
            <h2 className="text-4xl md:text-5xl font-bold mb-6">
              Ready to Start Your Journey?
            </h2>
            <p className="text-xl mb-8 text-primary-100 max-w-2xl mx-auto">
              Join thousands of athletes who trust RetailSports for their equipment needs
            </p>
            <Link
              to="/products"
              className="inline-flex items-center px-8 py-4 bg-white text-primary-700 rounded-lg font-semibold text-lg hover:bg-gray-100 transition shadow-lg"
            >
              Explore Products
              <svg className="ml-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </Link>
          </motion.div>
        </div>
      </section>
    </div>
  );
}
