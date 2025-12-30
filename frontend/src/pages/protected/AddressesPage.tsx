import { useState } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { useForm } from 'react-hook-form';
import { Button, Input, Modal } from '../../components/ui';
import { useToast } from '../../hooks';

interface Address {
  id: string;
  label: string;
  fullName: string;
  street: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  phoneNumber: string;
  isDefault: boolean;
}

interface AddressFormData {
  label: string;
  fullName: string;
  street: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  phoneNumber: string;
  isDefault: boolean;
}

/**
 * User Addresses Page
 * Displays and manages user's shipping addresses
 */
export function AddressesPage() {
  const { success, error: showError } = useToast();
  const [addresses, setAddresses] = useState<Address[]>([
    {
      id: '1',
      label: 'Home',
      fullName: 'John Doe',
      street: 'Via Roma 123',
      city: 'Milan',
      province: 'MI',
      postalCode: '20100',
      country: 'Italy',
      phoneNumber: '+39 123 456 7890',
      isDefault: true,
    },
    {
      id: '2',
      label: 'Work',
      fullName: 'John Doe',
      street: 'Via Verdi 456',
      city: 'Rome',
      province: 'RM',
      postalCode: '00100',
      country: 'Italy',
      phoneNumber: '+39 098 765 4321',
      isDefault: false,
    },
  ]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState<Address | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<AddressFormData>();

  const openAddModal = () => {
    setEditingAddress(null);
    reset({
      label: '',
      fullName: '',
      street: '',
      city: '',
      province: '',
      postalCode: '',
      country: 'Italy',
      phoneNumber: '',
      isDefault: false,
    });
    setIsModalOpen(true);
  };

  const openEditModal = (address: Address) => {
    setEditingAddress(address);
    reset(address);
    setIsModalOpen(true);
  };

  const onSubmit = async (data: AddressFormData) => {
    try {
      if (editingAddress) {
        // Update existing address
        setAddresses(
          addresses.map((addr) =>
            addr.id === editingAddress.id ? { ...addr, ...data } : addr
          )
        );
        success('Address updated successfully!');
      } else {
        // Add new address
        const newAddress: Address = {
          id: Date.now().toString(),
          ...data,
        };
        setAddresses([...addresses, newAddress]);
        success('Address added successfully!');
      }
      setIsModalOpen(false);
    } catch (err: any) {
      showError('Failed to save address');
    }
  };

  const handleDelete = (id: string) => {
    const address = addresses.find((a) => a.id === id);
    if (address?.isDefault) {
      showError('Cannot delete default address');
      return;
    }
    setAddresses(addresses.filter((addr) => addr.id !== id));
    success('Address deleted successfully!');
  };

  const handleSetDefault = (id: string) => {
    setAddresses(
      addresses.map((addr) => ({
        ...addr,
        isDefault: addr.id === id,
      }))
    );
    success('Default address updated!');
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">My Account</h1>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Sidebar Navigation */}
        <aside className="lg:col-span-1">
          <nav className="bg-white rounded-xl shadow-md p-4">
            <ul className="space-y-2">
              <li>
                <Link
                  to="/profile"
                  className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-50 text-gray-700"
                >
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                  Profile
                </Link>
              </li>
              <li>
                <Link
                  to="/addresses"
                  className="flex items-center gap-3 px-4 py-3 rounded-lg bg-primary-50 text-primary-700 font-medium"
                >
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                    />
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                    />
                  </svg>
                  Addresses
                </Link>
              </li>
              <li>
                <Link
                  to="/orders"
                  className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-50 text-gray-700"
                >
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
                    />
                  </svg>
                  Orders
                </Link>
              </li>
              <li>
                <button className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-50 text-gray-700 w-full text-left">
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z"
                    />
                  </svg>
                  Change Password
                </button>
              </li>
            </ul>
          </nav>
        </aside>

        {/* Main Content */}
        <main className="lg:col-span-3">
          {/* Header */}
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold">My Addresses</h2>
            <Button onClick={openAddModal}>Add New Address</Button>
          </div>

          {/* Addresses Grid */}
          {addresses.length === 0 ? (
            <div className="bg-white rounded-xl shadow-md p-12 text-center">
              <svg
                className="h-24 w-24 mx-auto text-gray-300 mb-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                />
              </svg>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">No addresses saved</h3>
              <p className="text-gray-600 mb-6">Add a shipping address to get started</p>
              <Button onClick={openAddModal}>Add Your First Address</Button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <AnimatePresence>
                {addresses.map((address) => (
                  <motion.div
                    key={address.id}
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className="bg-white rounded-xl shadow-md p-6 relative"
                  >
                    {/* Default Badge */}
                    {address.isDefault && (
                      <div className="absolute top-4 right-4">
                        <span className="bg-primary-100 text-primary-700 text-xs font-semibold px-3 py-1 rounded-full">
                          Default
                        </span>
                      </div>
                    )}

                    {/* Address Label */}
                    <h3 className="text-lg font-semibold text-gray-900 mb-3">
                      {address.label}
                    </h3>

                    {/* Address Details */}
                    <div className="text-gray-600 space-y-1 mb-4">
                      <p className="font-medium text-gray-900">{address.fullName}</p>
                      <p>{address.street}</p>
                      <p>
                        {address.city}, {address.province} {address.postalCode}
                      </p>
                      <p>{address.country}</p>
                      <p className="pt-2 border-t mt-2">{address.phoneNumber}</p>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-2">
                      <button
                        onClick={() => openEditModal(address)}
                        className="flex-1 px-4 py-2 border-2 border-gray-300 rounded-lg hover:border-gray-400 transition text-sm font-medium"
                      >
                        Edit
                      </button>
                      {!address.isDefault && (
                        <button
                          onClick={() => handleSetDefault(address.id)}
                          className="flex-1 px-4 py-2 border-2 border-primary-600 text-primary-600 rounded-lg hover:bg-primary-50 transition text-sm font-medium"
                        >
                          Set Default
                        </button>
                      )}
                      {!address.isDefault && (
                        <button
                          onClick={() => handleDelete(address.id)}
                          className="px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition"
                          title="Delete"
                        >
                          <svg
                            className="h-5 w-5"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                            />
                          </svg>
                        </button>
                      )}
                    </div>
                  </motion.div>
                ))}
              </AnimatePresence>
            </div>
          )}
        </main>
      </div>

      {/* Add/Edit Address Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingAddress ? 'Edit Address' : 'Add New Address'}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {/* Label */}
          <Input
            label="Address Label"
            placeholder="e.g., Home, Work, Office"
            error={errors.label?.message}
            {...register('label', { required: 'Label is required' })}
          />

          {/* Full Name */}
          <Input
            label="Full Name"
            placeholder="John Doe"
            error={errors.fullName?.message}
            {...register('fullName', { required: 'Full name is required' })}
          />

          {/* Street */}
          <Input
            label="Street Address"
            placeholder="Via Roma 123"
            error={errors.street?.message}
            {...register('street', { required: 'Street address is required' })}
          />

          {/* City & Province */}
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="City"
              placeholder="Milan"
              error={errors.city?.message}
              {...register('city', { required: 'City is required' })}
            />
            <Input
              label="Province"
              placeholder="MI"
              error={errors.province?.message}
              {...register('province', { required: 'Province is required' })}
            />
          </div>

          {/* Postal Code & Country */}
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Postal Code"
              placeholder="20100"
              error={errors.postalCode?.message}
              {...register('postalCode', { required: 'Postal code is required' })}
            />
            <Input
              label="Country"
              placeholder="Italy"
              error={errors.country?.message}
              {...register('country', { required: 'Country is required' })}
            />
          </div>

          {/* Phone Number */}
          <Input
            label="Phone Number"
            type="tel"
            placeholder="+39 123 456 7890"
            error={errors.phoneNumber?.message}
            {...register('phoneNumber', { required: 'Phone number is required' })}
          />

          {/* Set as Default */}
          <div className="flex items-center">
            <input
              id="is-default"
              type="checkbox"
              className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              {...register('isDefault')}
            />
            <label htmlFor="is-default" className="ml-2 block text-sm text-gray-700">
              Set as default address
            </label>
          </div>

          {/* Actions */}
          <div className="flex gap-3 pt-4">
            <Button type="submit" fullWidth>
              {editingAddress ? 'Update Address' : 'Add Address'}
            </Button>
            <Button type="button" variant="outline" fullWidth onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
