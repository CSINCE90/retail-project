import { useUIStore } from '../store';

/**
 * Toast notification hook
 */
export function useToast() {
  const addToast = useUIStore((state) => state.addToast);

  const success = (message: string, duration?: number) => {
    addToast({ type: 'success', message, duration });
  };

  const error = (message: string, duration?: number) => {
    addToast({ type: 'error', message, duration });
  };

  const info = (message: string, duration?: number) => {
    addToast({ type: 'info', message, duration });
  };

  const warning = (message: string, duration?: number) => {
    addToast({ type: 'warning', message, duration });
  };

  return {
    success,
    error,
    info,
    warning,
  };
}
