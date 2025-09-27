import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAuth } from '../hooks/useAuth';
import { Coins, Eye, EyeOff, UserPlus } from 'lucide-react';
import toast from 'react-hot-toast';

const schema = yup.object({
  username: yup
    .string()
    .required('Username is required')
    .min(3, 'Username must be at least 3 characters'),
  email: yup
    .string()
    .required('Email is required')
    .email('Invalid email format'),
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  password: yup
    .string()
    .required('Password is required')
    .min(6, 'Password must be at least 6 characters'),
  confirmPassword: yup
    .string()
    .required('Confirm password is required')
    .oneOf([yup.ref('password')], 'Passwords must match'),
});

type RegisterFormData = yup.InferType<typeof schema>;

const RegisterPage: React.FC = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);
    try {
      await registerUser({
        username: data.username,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        password: data.password,
      });
      toast.success('Registration successful! You are now logged in.');
      navigate('/dashboard');
    } catch (error: any) {
      const message = error.response?.data?.message || error.message || 'Registration failed';
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-dark-900 via-dark-800 to-primary-900 flex items-center justify-center px-4 py-8 relative overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute inset-0">
        <div className="absolute top-20 left-20 w-72 h-72 bg-gold-400/10 rounded-full blur-3xl animate-pulse-soft"></div>
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-primary-400/10 rounded-full blur-3xl animate-pulse-soft" style={{ animationDelay: '1s' }}></div>
        <div className="absolute top-1/2 left-1/3 w-64 h-64 bg-accent-400/10 rounded-full blur-3xl animate-pulse-soft" style={{ animationDelay: '2s' }}></div>
      </div>

      <div className="relative z-10 max-w-md w-full">
        {/* Register Card */}
        <div className="bg-white/10 backdrop-blur-xl border border-white/20 rounded-3xl shadow-large p-8 animate-fade-in">
          {/* Header */}
          <div className="text-center mb-8">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-gold-400 to-gold-600 rounded-2xl mb-6 shadow-glow">
              <Coins className="w-10 h-10 text-white" />
            </div>
            <h1 className="text-3xl font-display font-bold text-white mb-2">Join GoldTrader</h1>
            <p className="text-white/70 text-lg">Create your gold trading account</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {/* Name Fields */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label htmlFor="firstName" className="block text-sm font-medium text-white/90">
                  First Name
                </label>
                <input
                  {...register('firstName')}
                  type="text"
                  className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15"
                  placeholder="First name"
                />
                {errors.firstName && (
                  <p className="text-sm text-danger-400">{errors.firstName.message}</p>
                )}
              </div>
              <div className="space-y-2">
                <label htmlFor="lastName" className="block text-sm font-medium text-white/90">
                  Last Name
                </label>
                <input
                  {...register('lastName')}
                  type="text"
                  className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15"
                  placeholder="Last name"
                />
                {errors.lastName && (
                  <p className="text-sm text-danger-400">{errors.lastName.message}</p>
                )}
              </div>
            </div>

            {/* Username Field */}
            <div className="space-y-2">
              <label htmlFor="username" className="block text-sm font-medium text-white/90">
                Username
              </label>
              <input
                {...register('username')}
                type="text"
                className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15"
                placeholder="Choose a username"
              />
              {errors.username && (
                <p className="text-sm text-danger-400">{errors.username.message}</p>
              )}
            </div>

            {/* Email Field */}
            <div className="space-y-2">
              <label htmlFor="email" className="block text-sm font-medium text-white/90">
                Email
              </label>
              <input
                {...register('email')}
                type="email"
                className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15"
                placeholder="Enter your email"
              />
              {errors.email && (
                <p className="text-sm text-danger-400">{errors.email.message}</p>
              )}
            </div>

            {/* Password Field */}
            <div className="space-y-2">
              <label htmlFor="password" className="block text-sm font-medium text-white/90">
                Password
              </label>
              <div className="relative">
                <input
                  {...register('password')}
                  type={showPassword ? 'text' : 'password'}
                  className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15 pr-12"
                  placeholder="Create a password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-4 flex items-center text-white/70 hover:text-white transition-colors"
                >
                  {showPassword ? (
                    <EyeOff className="h-5 w-5" />
                  ) : (
                    <Eye className="h-5 w-5" />
                  )}
                </button>
              </div>
              {errors.password && (
                <p className="text-sm text-danger-400">{errors.password.message}</p>
              )}
            </div>

            {/* Confirm Password Field */}
            <div className="space-y-2">
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-white/90">
                Confirm Password
              </label>
              <div className="relative">
                <input
                  {...register('confirmPassword')}
                  type={showConfirmPassword ? 'text' : 'password'}
                  className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15 pr-12"
                  placeholder="Confirm your password"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute inset-y-0 right-0 pr-4 flex items-center text-white/70 hover:text-white transition-colors"
                >
                  {showConfirmPassword ? (
                    <EyeOff className="h-5 w-5" />
                  ) : (
                    <Eye className="h-5 w-5" />
                  )}
                </button>
              </div>
              {errors.confirmPassword && (
                <p className="text-sm text-danger-400">{errors.confirmPassword.message}</p>
              )}
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-gradient-to-r from-gold-500 to-gold-600 text-white py-3 px-6 rounded-xl font-semibold hover:from-gold-600 hover:to-gold-700 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:ring-offset-2 focus:ring-offset-transparent disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-medium hover:shadow-glow transform hover:scale-[1.02] active:scale-[0.98]"
            >
              {isLoading ? (
                <div className="flex items-center justify-center">
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2"></div>
                  Creating account...
                </div>
              ) : (
                <div className="flex items-center justify-center">
                  <UserPlus className="w-5 h-5 mr-2" />
                  Create Account
                </div>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="mt-8 text-center">
            <p className="text-white/70">
              Already have an account?{' '}
              <Link 
                to="/login" 
                className="text-gold-400 hover:text-gold-300 font-semibold transition-colors hover:underline"
              >
                Sign In
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;