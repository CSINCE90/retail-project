import { useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useForm } from 'react-hook-form';
import { useAuthStore } from '../../store';
import { useToast } from '../../hooks';
import { Input, Button } from '../../components/ui';
import { isValidEmail } from '../../utils';

interface ProfileFormData {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
}

/**
 * User Profile Page
 * Displays and allows editing of user profile information
 */
export function ProfilePage() {
  const { user, updateProfile } = useAuthStore();
  const { success, error: showError } = useToast();
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<ProfileFormData>({
    defaultValues: {
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      phoneNumber: user?.phoneNumber || '',
    },
  });

  const onSubmit = async (data: ProfileFormData) => {
    setIsLoading(true);
    try {
      await updateProfile(data);
      success('Profile updated successfully!');
      setIsEditing(false);
    } catch (err: any) {
      showError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    reset();
    setIsEditing(false);
  };

  if (!user) {
    return null;
  }

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
                  className="flex items-center gap-3 px-4 py-3 rounded-lg bg-primary-50 text-primary-700 font-medium"
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
                  className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-50 text-gray-700"
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
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white rounded-xl shadow-md p-8"
          >
            {/* Header */}
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center gap-4">
                <div className="w-20 h-20 bg-primary-100 rounded-full flex items-center justify-center">
                  <span className="text-3xl font-bold text-primary-600">
                    {user.firstName[0]}
                    {user.lastName[0]}
                  </span>
                </div>
                <div>
                  <h2 className="text-2xl font-bold text-gray-900">
                    {user.firstName} {user.lastName}
                  </h2>
                  <p className="text-gray-600">{user.email}</p>
                </div>
              </div>
              {!isEditing && (
                <Button onClick={() => setIsEditing(true)} variant="outline">
                  Edit Profile
                </Button>
              )}
            </div>

            {/* Profile Form */}
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* First Name */}
                <Input
                  label="First Name"
                  placeholder="John"
                  disabled={!isEditing}
                  error={errors.firstName?.message}
                  {...register('firstName', {
                    required: 'First name is required',
                    minLength: {
                      value: 2,
                      message: 'First name must be at least 2 characters',
                    },
                  })}
                />

                {/* Last Name */}
                <Input
                  label="Last Name"
                  placeholder="Doe"
                  disabled={!isEditing}
                  error={errors.lastName?.message}
                  {...register('lastName', {
                    required: 'Last name is required',
                    minLength: {
                      value: 2,
                      message: 'Last name must be at least 2 characters',
                    },
                  })}
                />
              </div>

              {/* Email */}
              <Input
                label="Email Address"
                type="email"
                placeholder="your@email.com"
                disabled={!isEditing}
                error={errors.email?.message}
                {...register('email', {
                  required: 'Email is required',
                  validate: (value) => isValidEmail(value) || 'Please enter a valid email',
                })}
              />

              {/* Phone Number */}
              <Input
                label="Phone Number"
                type="tel"
                placeholder="+39 123 456 7890"
                disabled={!isEditing}
                error={errors.phoneNumber?.message}
                {...register('phoneNumber')}
              />

              {/* Action Buttons */}
              {isEditing && (
                <div className="flex gap-4 pt-4 border-t">
                  <Button type="submit" isLoading={isLoading}>
                    Save Changes
                  </Button>
                  <Button type="button" variant="outline" onClick={handleCancel}>
                    Cancel
                  </Button>
                </div>
              )}
            </form>

            {/* Account Stats */}
            <div className="mt-8 pt-8 border-t">
              <h3 className="text-lg font-semibold mb-4">Account Overview</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-primary-50 rounded-lg p-4">
                  <p className="text-sm text-gray-600 mb-1">Total Orders</p>
                  <p className="text-2xl font-bold text-primary-600">0</p>
                </div>
                <div className="bg-green-50 rounded-lg p-4">
                  <p className="text-sm text-gray-600 mb-1">Total Spent</p>
                  <p className="text-2xl font-bold text-green-600">€0.00</p>
                </div>
                <div className="bg-blue-50 rounded-lg p-4">
                  <p className="text-sm text-gray-600 mb-1">Saved Addresses</p>
                  <p className="text-2xl font-bold text-blue-600">0</p>
                </div>
              </div>
            </div>
          </motion.div>
        </main>
      </div>
    </div>
  );
}
