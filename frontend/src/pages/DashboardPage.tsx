import React from 'react';
import { useQuery } from 'react-query';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout';
import { goldService } from '../services/goldService';
import { TrendingUp, TrendingDown, ShoppingCart, DollarSign, History, ArrowRight, BarChart3 } from 'lucide-react';

const DashboardPage: React.FC = () => {
  const { data: goldPrice, isLoading: isPriceLoading } = useQuery(
    'currentGoldPrice',
    goldService.getCurrentPrice,
    {
      refetchInterval: 30000, // Refresh every 30 seconds
    }
  );

  const { data: transactionHistory } = useQuery(
    'recentTransactions',
    () => goldService.getTransactionHistory(0, 5),
    {
      refetchInterval: 60000, // Refresh every minute
    }
  );

  return (
    <Layout>
      <div className="min-h-screen bg-gradient-to-br from-dark-50 via-white to-gold-50 p-6">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-display font-bold text-dark-900 mb-2">
              Gold Trading Dashboard
            </h1>
            <p className="text-lg text-dark-600">
              Monitor prices, manage trades, and track your portfolio
            </p>
          </div>

          {/* Current Gold Price Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            {/* Buy Price Card */}
            <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-6 shadow-soft hover:shadow-medium transition-all duration-300 transform hover:scale-105">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-gradient-to-br from-success-100 to-success-200 rounded-2xl">
                  <TrendingUp className="h-6 w-6 text-success-600" />
                </div>
                <span className="text-xs font-semibold text-success-600 bg-success-100 px-2 py-1 rounded-full">
                  BUY
                </span>
              </div>
              <div>
                <p className="text-sm font-medium text-dark-600 mb-1">Buy Price</p>
                <p className="text-2xl font-bold text-dark-900">
                  {isPriceLoading ? (
                    <div className="animate-pulse bg-gradient-to-r from-dark-200 to-dark-300 h-8 w-32 rounded-lg"></div>
                  ) : (
                    `$${goldPrice?.buyPrice.toFixed(2)}`
                  )}
                </p>
                <p className="text-xs text-dark-500 mt-1">
                  per {goldPrice?.unit.replace('_', ' ')}
                </p>
              </div>
            </div>

            {/* Sell Price Card */}
            <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-6 shadow-soft hover:shadow-medium transition-all duration-300 transform hover:scale-105">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-gradient-to-br from-danger-100 to-danger-200 rounded-2xl">
                  <TrendingDown className="h-6 w-6 text-danger-600" />
                </div>
                <span className="text-xs font-semibold text-danger-600 bg-danger-100 px-2 py-1 rounded-full">
                  SELL
                </span>
              </div>
              <div>
                <p className="text-sm font-medium text-dark-600 mb-1">Sell Price</p>
                <p className="text-2xl font-bold text-dark-900">
                  {isPriceLoading ? (
                    <div className="animate-pulse bg-gradient-to-r from-dark-200 to-dark-300 h-8 w-32 rounded-lg"></div>
                  ) : (
                    `$${goldPrice?.sellPrice.toFixed(2)}`
                  )}
                </p>
                <p className="text-xs text-dark-500 mt-1">
                  per {goldPrice?.unit.replace('_', ' ')}
                </p>
              </div>
            </div>

            {/* Spread Card */}
            <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-6 shadow-soft hover:shadow-medium transition-all duration-300 transform hover:scale-105">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-gradient-to-br from-gold-100 to-gold-200 rounded-2xl">
                  <DollarSign className="h-6 w-6 text-gold-600" />
                </div>
                <span className="text-xs font-semibold text-gold-600 bg-gold-100 px-2 py-1 rounded-full">
                  SPREAD
                </span>
              </div>
              <div>
                <p className="text-sm font-medium text-dark-600 mb-1">Price Spread</p>
                <p className="text-2xl font-bold text-dark-900">
                  {isPriceLoading ? (
                    <div className="animate-pulse bg-gradient-to-r from-dark-200 to-dark-300 h-8 w-24 rounded-lg"></div>
                  ) : (
                    `$${(goldPrice ? goldPrice.buyPrice - goldPrice.sellPrice : 0).toFixed(2)}`
                  )}
                </p>
                <p className="text-xs text-dark-500 mt-1">
                  difference
                </p>
              </div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <Link
              to="/trading"
              className="group bg-gradient-to-br from-gold-500 to-gold-600 p-8 rounded-3xl shadow-medium hover:shadow-glow transition-all duration-300 transform hover:scale-105 text-white relative overflow-hidden"
            >
              <div className="absolute inset-0 bg-gradient-to-br from-gold-400/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
              <div className="relative">
                <div className="flex items-center justify-between mb-4">
                  <div className="p-3 bg-white/20 rounded-2xl backdrop-blur-sm">
                    <ShoppingCart className="h-8 w-8" />
                  </div>
                  <ArrowRight className="h-6 w-6 transform group-hover:translate-x-1 transition-transform duration-300" />
                </div>
                <h3 className="text-2xl font-bold mb-2">Trade Gold</h3>
                <p className="text-white/80">
                  Buy and sell gold at current market prices with instant execution
                </p>
              </div>
            </Link>

            <Link
              to="/transactions"
              className="group bg-gradient-to-br from-accent-500 to-accent-600 p-8 rounded-3xl shadow-medium hover:shadow-glow transition-all duration-300 transform hover:scale-105 text-white relative overflow-hidden"
            >
              <div className="absolute inset-0 bg-gradient-to-br from-accent-400/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
              <div className="relative">
                <div className="flex items-center justify-between mb-4">
                  <div className="p-3 bg-white/20 rounded-2xl backdrop-blur-sm">
                    <BarChart3 className="h-8 w-8" />
                  </div>
                  <ArrowRight className="h-6 w-6 transform group-hover:translate-x-1 transition-transform duration-300" />
                </div>
                <h3 className="text-2xl font-bold mb-2">View History</h3>
                <p className="text-white/80">
                  Track your trading performance and transaction history
                </p>
              </div>
            </Link>
          </div>

          {/* Recent Transactions */}
          <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl shadow-soft overflow-hidden">
            <div className="p-8">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-2xl font-bold text-dark-900">Recent Transactions</h3>
                <Link
                  to="/transactions"
                  className="text-gold-600 hover:text-gold-700 font-semibold flex items-center gap-2 transition-colors"
                >
                  View All
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </div>

              {transactionHistory?.content.length === 0 ? (
                <div className="text-center py-16">
                  <div className="p-4 bg-gradient-to-br from-dark-100 to-dark-200 rounded-3xl w-24 h-24 mx-auto mb-6 flex items-center justify-center">
                    <History className="h-12 w-12 text-dark-400" />
                  </div>
                  <h3 className="text-xl font-semibold text-dark-900 mb-2">No transactions yet</h3>
                  <p className="text-dark-600 mb-8 max-w-md mx-auto">
                    Start your gold trading journey by making your first transaction. Monitor prices and execute trades with confidence.
                  </p>
                  <Link
                    to="/trading"
                    className="inline-flex items-center gap-2 bg-gradient-to-r from-gold-500 to-gold-600 text-white px-8 py-4 rounded-2xl font-semibold hover:from-gold-600 hover:to-gold-700 transition-all duration-200 shadow-medium hover:shadow-glow transform hover:scale-105"
                  >
                    <ShoppingCart className="h-5 w-5" />
                    Start Trading
                  </Link>
                </div>
              ) : (
                <div className="space-y-4">
                  {transactionHistory?.content.slice(0, 5).map((transaction) => (
                    <div
                      key={transaction.id}
                      className="flex items-center justify-between p-6 bg-white/50 rounded-2xl border border-white/30 hover:bg-white/70 transition-all duration-200"
                    >
                      <div className="flex items-center gap-4">
                        <div className={`p-3 rounded-2xl ${
                          transaction.type === 'BUY' 
                            ? 'bg-success-100 text-success-600' 
                            : 'bg-danger-100 text-danger-600'
                        }`}>
                          {transaction.type === 'BUY' ? (
                            <TrendingUp className="h-6 w-6" />
                          ) : (
                            <TrendingDown className="h-6 w-6" />
                          )}
                        </div>
                        <div>
                          <p className="font-semibold text-dark-900">
                            {transaction.type} {transaction.amount} oz
                          </p>
                          <p className="text-sm text-dark-600">
                            ${transaction.pricePerUnit.toFixed(2)} per oz
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="font-bold text-lg text-dark-900">
                          ${transaction.totalPrice.toFixed(2)}
                        </p>
                        <p className="text-sm text-dark-600">
                          {new Date(transaction.timestamp).toLocaleDateString()}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default DashboardPage;