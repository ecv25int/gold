import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { LogOut, Coins, History, BarChart3, ShoppingCart } from 'lucide-react';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) {
    return <>{children}</>;
  }

  const isActive = (path: string) => location.pathname === path;

  return (
    <div className="min-h-screen bg-gradient-to-br from-dark-50 via-white to-gold-50">
      {/* Header */}
      <header className="bg-white/70 backdrop-blur-xl border-b border-white/20 sticky top-0 z-50 shadow-soft">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* Logo and Navigation */}
            <div className="flex items-center">
              <Link to="/dashboard" className="flex items-center space-x-3 group">
                <div className="p-2 bg-gradient-to-br from-gold-400 to-gold-600 rounded-xl shadow-medium group-hover:shadow-glow transition-all duration-300">
                  <Coins className="h-6 w-6 text-white" />
                </div>
                <span className="text-xl font-display font-bold bg-gradient-to-r from-gold-600 to-gold-800 bg-clip-text text-transparent">
                  GoldTrader
                </span>
              </Link>
              
              <nav className="hidden md:flex items-center space-x-2 ml-10">
                <Link
                  to="/dashboard"
                  className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200 ${
                    isActive('/dashboard')
                      ? 'bg-gold-100 text-gold-700 shadow-soft'
                      : 'text-dark-600 hover:text-dark-900 hover:bg-white/50'
                  }`}
                >
                  <BarChart3 className="h-4 w-4" />
                  <span>Dashboard</span>
                </Link>
                <Link
                  to="/operation"
                  className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200 ${
                    isActive('/operation')
                      ? 'bg-gold-100 text-gold-700 shadow-soft'
                      : 'text-dark-600 hover:text-dark-900 hover:bg-white/50'
                  }`}
                >
                  <ShoppingCart className="h-4 w-4" />
                  <span>Operation</span>
                </Link>
                <Link
                  to="/clients"
                  className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200 ${
                    isActive('/clients')
                      ? 'bg-gold-100 text-gold-700 shadow-soft'
                      : 'text-dark-600 hover:text-dark-900 hover:bg-white/50'
                  }`}
                >
                  <BarChart3 className="h-4 w-4" />
                  <span>Clients</span>
                </Link>
                <Link
                  to="/providers"
                  className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200 ${
                    isActive('/providers')
                      ? 'bg-gold-100 text-gold-700 shadow-soft'
                      : 'text-dark-600 hover:text-dark-900 hover:bg-white/50'
                  }`}
                >
                  <BarChart3 className="h-4 w-4" />
                  <span>Providers</span>
                </Link>
                <Link
                  to="/transactions"
                  className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200 ${
                    isActive('/transactions')
                      ? 'bg-gold-100 text-gold-700 shadow-soft'
                      : 'text-dark-600 hover:text-dark-900 hover:bg-white/50'
                  }`}
                >
                  <History className="h-4 w-4" />
                  <span>History</span>
                </Link>
              </nav>
            </div>

            {/* User menu */}
            <div className="flex items-center space-x-4">
              <div className="hidden sm:block">
                <span className="text-sm text-dark-600">Welcome, </span>
                <span className="text-sm font-semibold text-dark-900">{user.username}</span>
              </div>
              <button
                onClick={handleLogout}
                className="inline-flex items-center space-x-2 px-4 py-2 bg-white/50 border border-white/20 text-sm font-medium rounded-xl text-dark-600 hover:text-dark-900 hover:bg-white/70 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:ring-offset-2 focus:ring-offset-transparent transition-all duration-200 backdrop-blur-sm"
              >
                <LogOut className="h-4 w-4" />
                <span className="hidden sm:inline">Logout</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main>
        {children}
      </main>
    </div>
  );
};

export default Layout;