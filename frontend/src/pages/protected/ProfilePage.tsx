import { useAuthStore } from '../../store';

/**
 * User Profile Page
 */
export function ProfilePage() {
  const { user } = useAuthStore();

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">My Profile</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Sidebar navigation */}
        <aside className="lg:col-span-1">
          <nav className="bg-white rounded-lg shadow p-4">
            <ul className="space-y-2">
              <li>
                <a href="#" className="block px-4 py-2 rounded bg-primary-50 text-primary-700">
                  Profile
                </a>
              </li>
              <li>
                <a href="#" className="block px-4 py-2 rounded hover:bg-gray-50">
                  Addresses
                </a>
              </li>
              <li>
                <a href="#" className="block px-4 py-2 rounded hover:bg-gray-50">
                  Orders
                </a>
              </li>
              <li>
                <a href="#" className="block px-4 py-2 rounded hover:bg-gray-50">
                  Change Password
                </a>
              </li>
            </ul>
          </nav>
        </aside>

        {/* Profile content */}
        <main className="lg:col-span-2">
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">Personal Information</h2>
            {user && (
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                  <p className="text-gray-900">
                    {user.firstName} {user.lastName}
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <p className="text-gray-900">{user.email}</p>
                </div>
                {user.phoneNumber && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
                    <p className="text-gray-900">{user.phoneNumber}</p>
                  </div>
                )}
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  );
}
