import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { RegisterForm } from '../../components/forms/RegisterForm';

/**
 * Register Page
 */
export function RegisterPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          {/* Header */}
          <div className="text-center mb-8">
            <Link to="/" className="inline-block mb-6">
              <motion.div
                whileHover={{ scale: 1.05 }}
                className="text-4xl font-bold text-primary-600"
              >
                ⚽ RetailSports
              </motion.div>
            </Link>
            <h2 className="text-3xl font-bold text-gray-900">Join RetailSports</h2>
            <p className="mt-2 text-gray-600">
              Already have an account?{' '}
              <Link to="/login" className="font-medium text-primary-600 hover:text-primary-500">
                Sign in
              </Link>
            </p>
          </div>

          {/* Form Card */}
          <div className="bg-white py-8 px-6 shadow-xl rounded-2xl">
            <RegisterForm />
          </div>

          {/* Benefits */}
          <div className="mt-8 bg-white rounded-2xl shadow-md p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Why join us?</h3>
            <ul className="space-y-3">
              <li className="flex items-start">
                <svg
                  className="h-6 w-6 text-green-500 mr-2 flex-shrink-0"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                <span className="text-gray-700">Exclusive deals and discounts</span>
              </li>
              <li className="flex items-start">
                <svg
                  className="h-6 w-6 text-green-500 mr-2 flex-shrink-0"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                <span className="text-gray-700">Fast checkout with saved addresses</span>
              </li>
              <li className="flex items-start">
                <svg
                  className="h-6 w-6 text-green-500 mr-2 flex-shrink-0"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                <span className="text-gray-700">Track your orders in real-time</span>
              </li>
              <li className="flex items-start">
                <svg
                  className="h-6 w-6 text-green-500 mr-2 flex-shrink-0"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                <span className="text-gray-700">Personalized product recommendations</span>
              </li>
            </ul>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
