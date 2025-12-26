import { RouterProvider } from 'react-router-dom';
import { router } from './routes';

/**
 * Main App Component
 */
function App() {
  return <RouterProvider router={router} />;
}

export default App;
