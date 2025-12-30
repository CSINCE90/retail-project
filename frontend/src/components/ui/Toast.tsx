import { motion, AnimatePresence } from 'framer-motion';
import { useUIStore } from '../../store';

/**
 * Toast Notification Component
 * Displays toast messages
 */
export function Toast() {
  const { toasts, removeToast } = useUIStore();

  return (
    <div className="fixed top-4 right-4 z-50 space-y-2">
      <AnimatePresence>
        {toasts.map((toast) => (
          <motion.div
            key={toast.id}
            initial={{ opacity: 0, x: 100, scale: 0.8 }}
            animate={{ opacity: 1, x: 0, scale: 1 }}
            exit={{ opacity: 0, x: 100, scale: 0.8 }}
            transition={{ duration: 0.3 }}
            className={`
              max-w-sm w-full rounded-lg shadow-lg p-4 flex items-start
              ${
                toast.type === 'success'
                  ? 'bg-green-50 border border-green-200'
                  : toast.type === 'error'
                  ? 'bg-red-50 border border-red-200'
                  : toast.type === 'warning'
                  ? 'bg-yellow-50 border border-yellow-200'
                  : 'bg-blue-50 border border-blue-200'
              }
            `}
          >
            {/* Icon */}
            <div className="flex-shrink-0">
              {toast.type === 'success' && (
                <svg className="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
              )}
              {toast.type === 'error' && (
                <svg className="h-6 w-6 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              )}
              {toast.type === 'warning' && (
                <svg className="h-6 w-6 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
              )}
              {toast.type === 'info' && (
                <svg className="h-6 w-6 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              )}
            </div>

            {/* Message */}
            <div className="ml-3 flex-1">
              <p
                className={`text-sm font-medium ${
                  toast.type === 'success'
                    ? 'text-green-900'
                    : toast.type === 'error'
                    ? 'text-red-900'
                    : toast.type === 'warning'
                    ? 'text-yellow-900'
                    : 'text-blue-900'
                }`}
              >
                {toast.message}
              </p>
            </div>

            {/* Close button */}
            <button
              onClick={() => removeToast(toast.id)}
              className={`ml-4 flex-shrink-0 inline-flex rounded-md focus:outline-none focus:ring-2 ${
                toast.type === 'success'
                  ? 'text-green-500 hover:text-green-600 focus:ring-green-600'
                  : toast.type === 'error'
                  ? 'text-red-500 hover:text-red-600 focus:ring-red-600'
                  : toast.type === 'warning'
                  ? 'text-yellow-500 hover:text-yellow-600 focus:ring-yellow-600'
                  : 'text-blue-500 hover:text-blue-600 focus:ring-blue-600'
              }`}
            >
              <svg className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
              </svg>
            </button>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
}
