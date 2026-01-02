import { useState, useEffect } from 'react';
import { stockService } from '../../services';
import type { StockResponse, LowStockAlertResponse, MovementType } from '../../types/stock.types';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { Spinner } from '../../components/ui/Spinner';
import { Modal } from '../../components/ui/Modal';
import { Input } from '../../components/ui/Input';

/**
 * Admin Stock Management Page
 */
export const AdminStockPage = () => {
  const [stocks, setStocks] = useState<StockResponse[]>([]);
  const [lowStockAlerts, setLowStockAlerts] = useState<LowStockAlertResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedStock, setSelectedStock] = useState<StockResponse | null>(null);
  const [showAdjustModal, setShowAdjustModal] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showMinimumModal, setShowMinimumModal] = useState(false);
  
  // Adjust form
  const [movementType, setMovementType] = useState<MovementType>('IN' as MovementType);
  const [quantity, setQuantity] = useState('');
  const [notes, setNotes] = useState('');
  
  // Create form
  const [newProductId, setNewProductId] = useState('');
  const [initialQuantity, setInitialQuantity] = useState('');
  const [minimumQuantity, setMinimumQuantity] = useState('10');
  
  // Update minimum form
  const [newMinimumQuantity, setNewMinimumQuantity] = useState('');

  useEffect(() => {
    loadData();
  }, [currentPage]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [stockData, alertsData] = await Promise.all([
        stockService.getAllStock(currentPage, 20),
        stockService.getActiveLowStockAlerts(),
      ]);
      
      setStocks(stockData.content);
      setTotalPages(stockData.totalPages);
      setLowStockAlerts(alertsData);
    } catch (error) {
      console.error('Error loading stock data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAdjustStock = async () => {
    if (!selectedStock || !quantity) return;

    try {
      await stockService.adjustStock(selectedStock.productId, {
        movementType,
        quantity: parseInt(quantity),
        notes,
      });
      
      setShowAdjustModal(false);
      setQuantity('');
      setNotes('');
      loadData();
    } catch (error) {
      console.error('Error adjusting stock:', error);
    }
  };

  const handleCreateStock = async () => {
    if (!newProductId || !initialQuantity) return;

    try {
      await stockService.createStock({
        productId: parseInt(newProductId),
        initialQuantity: parseInt(initialQuantity),
        minimumQuantity: parseInt(minimumQuantity),
      });
      
      setShowCreateModal(false);
      setNewProductId('');
      setInitialQuantity('');
      setMinimumQuantity('10');
      loadData();
    } catch (error) {
      console.error('Error creating stock:', error);
    }
  };

  const handleUpdateMinimum = async () => {
    if (!selectedStock || !newMinimumQuantity) return;

    try {
      await stockService.updateMinimumQuantity(selectedStock.productId, {
        minimumQuantity: parseInt(newMinimumQuantity),
      });
      
      setShowMinimumModal(false);
      setNewMinimumQuantity('');
      loadData();
    } catch (error) {
      console.error('Error updating minimum quantity:', error);
    }
  };

  const openAdjustModal = (stock: StockResponse) => {
    setSelectedStock(stock);
    setShowAdjustModal(true);
  };

  const openMinimumModal = (stock: StockResponse) => {
    setSelectedStock(stock);
    setNewMinimumQuantity(stock.minimumQuantity.toString());
    setShowMinimumModal(true);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Stock Management</h1>
          <p className="text-gray-600 mt-1">Gestione giacenze e movimenti</p>
        </div>
        <Button onClick={() => setShowCreateModal(true)}>
          Crea Nuovo Stock
        </Button>
      </div>

      {/* Low Stock Alerts */}
      {lowStockAlerts.length > 0 && (
        <Card className="mb-6 border-l-4 border-orange-500">
          <div className="p-4">
            <h2 className="text-lg font-semibold text-orange-800 mb-3">
              ⚠️ Alert Scorte Basse ({lowStockAlerts.length})
            </h2>
            <div className="space-y-2">
              {lowStockAlerts.map((alert) => (
                <div key={alert.id} className="flex justify-between items-center bg-orange-50 p-3 rounded">
                  <div>
                    <span className="font-medium">{alert.productName || `Prodotto #${alert.productId}`}</span>
                    <p className="text-sm text-gray-600">
                      Disponibili: <strong>{alert.availableQuantity}</strong> / 
                      Minimo: <strong>{alert.minimumQuantity}</strong>
                    </p>
                  </div>
                  <Button
                    size="sm"
                    onClick={() => {
                      const stock = stocks.find(s => s.productId === alert.productId);
                      if (stock) openAdjustModal(stock);
                    }}
                  >
                    Ricarica
                  </Button>
                </div>
              ))}
            </div>
          </div>
        </Card>
      )}

      {/* Stock Table */}
      <Card>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Prodotto
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Disponibile
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Riservato
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Fisico
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Min
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Status
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  Azioni
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {stocks.map((stock) => (
                <tr key={stock.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4">
                    <div>
                      <div className="font-medium text-gray-900">
                        {stock.productName || `Prodotto #${stock.productId}`}
                      </div>
                      <div className="text-sm text-gray-500">ID: {stock.productId}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm font-semibold text-green-600">
                    {stock.availableQuantity}
                  </td>
                  <td className="px-6 py-4 text-sm text-orange-600">
                    {stock.reservedQuantity}
                  </td>
                  <td className="px-6 py-4 text-sm font-semibold">
                    {stock.physicalQuantity}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    {stock.minimumQuantity}
                  </td>
                  <td className="px-6 py-4">
                    {stock.isLowStock ? (
                      <Badge variant="warning">Scorta Bassa</Badge>
                    ) : (
                      <Badge variant="success">OK</Badge>
                    )}
                  </td>
                  <td className="px-6 py-4 text-right space-x-2">
                    <Button size="sm" onClick={() => openAdjustModal(stock)}>
                      Aggiusta
                    </Button>
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => openMinimumModal(stock)}
                    >
                      Soglia
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="px-6 py-4 flex justify-between items-center border-t">
            <Button
              variant="secondary"
              onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
              disabled={currentPage === 0}
            >
              Precedente
            </Button>
            <span className="text-sm text-gray-600">
              Pagina {currentPage + 1} di {totalPages}
            </span>
            <Button
              variant="secondary"
              onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
              disabled={currentPage === totalPages - 1}
            >
              Successiva
            </Button>
          </div>
        )}
      </Card>

      {/* Adjust Stock Modal */}
      <Modal
        isOpen={showAdjustModal}
        onClose={() => setShowAdjustModal(false)}
        title="Aggiusta Stock"
      >
        <div className="space-y-4">
          {selectedStock && (
            <div className="bg-gray-50 p-3 rounded">
              <p className="font-medium">{selectedStock.productName || `Prodotto #${selectedStock.productId}`}</p>
              <p className="text-sm text-gray-600">Disponibile: {selectedStock.availableQuantity}</p>
            </div>
          )}
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tipo Movimento
            </label>
            <select
              value={movementType}
              onChange={(e) => setMovementType(e.target.value as MovementType)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            >
              <option value="IN">IN - Carico</option>
              <option value="OUT">OUT - Scarico</option>
              <option value="ADJUSTMENT">ADJUSTMENT - Aggiustamento</option>
              <option value="RETURN">RETURN - Reso</option>
            </select>
          </div>

          <Input
            type="number"
            label="Quantità"
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            min="1"
          />

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Note
            </label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
              rows={3}
            />
          </div>

          <div className="flex justify-end space-x-2">
            <Button variant="secondary" onClick={() => setShowAdjustModal(false)}>
              Annulla
            </Button>
            <Button onClick={handleAdjustStock}>
              Conferma
            </Button>
          </div>
        </div>
      </Modal>

      {/* Create Stock Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        title="Crea Nuovo Stock"
      >
        <div className="space-y-4">
          <Input
            type="number"
            label="Product ID"
            value={newProductId}
            onChange={(e) => setNewProductId(e.target.value)}
            min="1"
          />

          <Input
            type="number"
            label="Quantità Iniziale"
            value={initialQuantity}
            onChange={(e) => setInitialQuantity(e.target.value)}
            min="0"
          />

          <Input
            type="number"
            label="Quantità Minima"
            value={minimumQuantity}
            onChange={(e) => setMinimumQuantity(e.target.value)}
            min="0"
          />

          <div className="flex justify-end space-x-2">
            <Button variant="secondary" onClick={() => setShowCreateModal(false)}>
              Annulla
            </Button>
            <Button onClick={handleCreateStock}>
              Crea
            </Button>
          </div>
        </div>
      </Modal>

      {/* Update Minimum Modal */}
      <Modal
        isOpen={showMinimumModal}
        onClose={() => setShowMinimumModal(false)}
        title="Aggiorna Soglia Minima"
      >
        <div className="space-y-4">
          {selectedStock && (
            <div className="bg-gray-50 p-3 rounded">
              <p className="font-medium">{selectedStock.productName || `Prodotto #${selectedStock.productId}`}</p>
              <p className="text-sm text-gray-600">Soglia attuale: {selectedStock.minimumQuantity}</p>
            </div>
          )}
          
          <Input
            type="number"
            label="Nuova Soglia Minima"
            value={newMinimumQuantity}
            onChange={(e) => setNewMinimumQuantity(e.target.value)}
            min="0"
          />

          <div className="flex justify-end space-x-2">
            <Button variant="secondary" onClick={() => setShowMinimumModal(false)}>
              Annulla
            </Button>
            <Button onClick={handleUpdateMinimum}>
              Aggiorna
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
