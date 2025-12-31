import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { useAuthStore } from '../../store';
import { useToast } from '../../hooks';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { isValidEmail } from '../../utils';
import type { RegisterRequest } from '../../types';

interface RegisterFormData extends RegisterRequest {
  confirmPassword: string;
  acceptTerms: boolean;
}

/**
 * Register Form Component
 * Handles user registration
 */
export function RegisterForm() {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>();

  const navigate = useNavigate();
  const { register: registerUser } = useAuthStore();
  const { success, error: showError } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const password = watch('password');

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);
    try {
      const { confirmPassword, acceptTerms, ...registerData } = data;
      console.log('Registration data:', registerData);
      await registerUser(registerData);
      success('Registration successful! Please check your email to verify your account, then login.');
      navigate('/login', { replace: true });
    } catch (err: any) {
      console.error('Registration error:', err);
      showError(err.response?.data?.message || err.message || 'Registration failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Username Field */}
      <Input
        label="Username"
        placeholder="johndoe"
        helperText="3-50 characters, letters, numbers, dots, underscores, hyphens only"
        error={errors.username?.message}
        {...register('username', {
          required: 'Username is required',
          minLength: {
            value: 3,
            message: 'Username must be at least 3 characters',
          },
          maxLength: {
            value: 50,
            message: 'Username must be less than 50 characters',
          },
          pattern: {
            value: /^[a-zA-Z0-9._-]+$/,
            message: 'Username can only contain letters, numbers, dots, underscores and hyphens',
          },
        })}
      />

      {/* First Name & Last Name */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="First Name"
          placeholder="John"
          error={errors.firstName?.message}
          {...register('firstName', {
            required: 'First name is required',
            minLength: {
              value: 2,
              message: 'First name must be at least 2 characters',
            },
          })}
        />

        <Input
          label="Last Name"
          placeholder="Doe"
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

      {/* Email Field */}
      <Input
        label="Email Address"
        type="email"
        placeholder="your@email.com"
        error={errors.email?.message}
        {...register('email', {
          required: 'Email is required',
          validate: (value) => isValidEmail(value) || 'Please enter a valid email address',
        })}
      />

      {/* Phone Number (Optional) */}
      <Input
        label="Phone Number"
        type="tel"
        placeholder="+39 123 456 7890"
        error={errors.phone?.message}
        {...register('phone', {
          pattern: {
            value: /^\+?[0-9]{10,20}$/,
            message: 'Phone number must be 10-20 digits, optionally starting with +'
          }
        })}
      />

      {/* Password Field */}
      <Input
        label="Password"
        type="password"
        placeholder="••••••••"
        helperText="At least 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character"
        error={errors.password?.message}
        {...register('password', {
          required: 'Password is required',
          minLength: {
            value: 8,
            message: 'Password must be at least 8 characters',
          },
          validate: (value) => {
            const hasUpperCase = /[A-Z]/.test(value);
            const hasLowerCase = /[a-z]/.test(value);
            const hasNumber = /[0-9]/.test(value);
            const hasSpecialChar = /[@#$%^&+=!]/.test(value);

            if (!hasUpperCase) return 'Password must contain at least one uppercase letter';
            if (!hasLowerCase) return 'Password must contain at least one lowercase letter';
            if (!hasNumber) return 'Password must contain at least one number';
            if (!hasSpecialChar) return 'Password must contain at least one special character (@#$%^&+=!)';

            return true;
          },
        })}
      />

      {/* Confirm Password Field */}
      <Input
        label="Confirm Password"
        type="password"
        placeholder="••••••••"
        error={errors.confirmPassword?.message}
        {...register('confirmPassword', {
          required: 'Please confirm your password',
          validate: (value) => value === password || 'Passwords do not match',
        })}
      />

      {/* Terms and Conditions */}
      <div className="flex items-start">
        <input
          id="accept-terms"
          type="checkbox"
          className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded mt-1"
          {...register('acceptTerms', {
            required: 'You must accept the terms and conditions',
          })}
        />
        <label htmlFor="accept-terms" className="ml-2 block text-sm text-gray-700">
          I agree to the{' '}
          <a href="#" className="text-primary-600 hover:text-primary-500">
            Terms and Conditions
          </a>{' '}
          and{' '}
          <a href="#" className="text-primary-600 hover:text-primary-500">
            Privacy Policy
          </a>
        </label>
      </div>
      {errors.acceptTerms && (
        <p className="text-sm text-red-600">{errors.acceptTerms.message}</p>
      )}

      {/* Submit Button */}
      <Button type="submit" fullWidth isLoading={isLoading} size="lg">
        Create Account
      </Button>
    </form>
  );
}
