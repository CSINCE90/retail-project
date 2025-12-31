import { useForm } from 'react-hook-form';
import { useNavigate, useLocation } from 'react-router-dom';
import { useState } from 'react';
import { useAuthStore } from '../../store';
import { useToast } from '../../hooks';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import type { LoginRequest } from '../../types';

/**
 * Login Form Component
 * Handles user authentication
 */
export function LoginForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>();

  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuthStore();
  const { success, error: showError } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const onSubmit = async (data: LoginRequest) => {
    setIsLoading(true);
    try {
      await login(data);
      success('Login successful! Welcome back.');

      // Redirect to the page user tried to access or home
      const from = (location.state as any)?.from?.pathname || '/';
      navigate(from, { replace: true });
    } catch (err: any) {
      showError(err.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Username Field */}
      <Input
        label="Username"
        type="text"
        placeholder="johndoe"
        error={errors.username?.message}
        {...register('username', {
          required: 'Username is required',
          minLength: {
            value: 3,
            message: 'Username must be at least 3 characters',
          },
        })}
      />

      {/* Password Field */}
      <Input
        label="Password"
        type="password"
        placeholder="••••••••"
        error={errors.password?.message}
        {...register('password', {
          required: 'Password is required',
          minLength: {
            value: 6,
            message: 'Password must be at least 6 characters',
          },
        })}
      />

      {/* Remember Me & Forgot Password */}
      <div className="flex items-center justify-between">
        <div className="flex items-center">
          <input
            id="remember-me"
            type="checkbox"
            className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-700">
            Remember me
          </label>
        </div>

        <a href="#" className="text-sm text-primary-600 hover:text-primary-500">
          Forgot password?
        </a>
      </div>

      {/* Submit Button */}
      <Button type="submit" fullWidth isLoading={isLoading} size="lg">
        Sign In
      </Button>
    </form>
  );
}
