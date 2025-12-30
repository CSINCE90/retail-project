import { RouterProvider } from 'react-router-dom';
import { router } from './routes';
import { Toast } from './components/ui/Toast';

/**
 * Main App Component
 */
function App() {
  return (
    <>
      <RouterProvider router={router} />
      <Toast />
    </>
  );
}

export default App;
