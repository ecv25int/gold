import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useAuth } from '../hooks/useAuth';
import { Eye, EyeOff, LogIn } from 'lucide-react';
import toast from 'react-hot-toast';

const schema = yup.object({
  username: yup.string().required('Username is required'),
  password: yup.string().required('Password is required'),
});

type LoginFormData = yup.InferType<typeof schema>;

const LoginPage: React.FC = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    try {
      await login(data.username, data.password);
      toast.success('Login successful!');
      navigate('/dashboard');
    } catch (error: any) {
      const message = error.response?.data?.message || error.message || 'Login failed';
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-yellow-900 flex items-center justify-center px-4 relative overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute inset-0">
        <div className="absolute top-20 left-20 w-72 h-72 bg-yellow-400/10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-yellow-400/10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute top-1/2 left-1/3 w-64 h-64 bg-blue-400/10 rounded-full blur-3xl animate-pulse"></div>
      </div>

      <div className="relative z-10 max-w-md w-full">
        {/* Login Card */}
        <div className="bg-white/10 backdrop-blur-xl border border-white/20 rounded-3xl shadow-2xl p-8">
          {/* Header */}
                    {/* Header */}
          <div className="text-center mb-8">
            <div className="w-16 h-16 bg-gradient-to-r from-yellow-400 to-yellow-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
              <span className="text-2xl font-bold text-gray-900">G</span>
            </div>
            <h1 className="text-3xl font-bold text-white mb-2">Welcome Back</h1>
            <p className="text-gray-300">Sign in to your Gold Trading account</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {/* Username Field */}
            <div className="space-y-2">
              <label htmlFor="username" className="block text-sm font-medium text-white/90">
                Username
              </label>
              <input
                {...register('username')}
                type="text"
                autoComplete="username"
                className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15"
                placeholder="Enter your username"
              />
              {errors.username && (
                <p className="text-sm text-red-400">{errors.username.message}</p>
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
                  autoComplete="current-password"
                  className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:border-transparent backdrop-blur-sm transition-all duration-200 hover:bg-white/15 pr-12"
                  placeholder="Enter your password"
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
                <p className="text-sm text-red-400">{errors.password.message}</p>
              )}
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-gradient-to-r from-yellow-500 to-yellow-600 text-white py-3 px-6 rounded-xl font-semibold hover:from-yellow-600 hover:to-yellow-700 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 focus:ring-offset-transparent disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-lg hover:shadow-xl transform hover:scale-[1.02] active:scale-[0.98]"
            >
              {isLoading ? (
                <div className="flex items-center justify-center">
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2"></div>
                  Signing in...
                </div>
              ) : (
                <div className="flex items-center justify-center">
                  <LogIn className="w-5 h-5 mr-2" />
                  Sign In
                </div>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="mt-8 text-center">
            <p className="text-white/70">
              Don't have an account?{' '}
              <Link 
                to="/register" 
                className="text-yellow-400 hover:text-yellow-300 font-semibold transition-colors hover:underline"
              >
                Create Account
              </Link>
            </p>
          </div>
        </div>

        {/* Demo Credentials Info */}
        <div className="mt-6 bg-white/5 backdrop-blur-sm border border-white/10 rounded-2xl p-4 text-center">
          <p className="text-white/70 text-sm mb-2">Demo Credentials:</p>
          <p className="text-white/90 text-sm">
            <span className="font-mono bg-white/10 px-2 py-1 rounded">testuser</span> / 
            <span className="font-mono bg-white/10 px-2 py-1 rounded ml-1">password123</span>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;