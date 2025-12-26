/**
 * Products Catalog Page
 */
export function ProductsPage() {
  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">Products</h1>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Sidebar with filters */}
        <aside className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow p-4">
            <h2 className="font-semibold mb-4">Filters</h2>
            {/* Filters will be added here */}
          </div>
        </aside>

        {/* Products grid */}
        <main className="lg:col-span-3">
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600">Products will be displayed here</p>
          </div>
        </main>
      </div>
    </div>
  );
}
