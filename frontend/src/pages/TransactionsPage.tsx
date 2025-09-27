import React, { useState } from 'react';
import { useQuery } from 'react-query';
import Layout from '../components/Layout';
import { goldService } from '../services/goldService';
import { TrendingUp, TrendingDown, ChevronLeft, ChevronRight, History, Search, Filter, ArrowUpRight, ArrowDownRight } from 'lucide-react';

const TransactionsPage: React.FC = () => {
  const [currentPage, setCurrentPage] = useState(0);
  const pageSize = 10;

  const { data: transactionHistory, isLoading } = useQuery(
    ['transactions', currentPage],
    () => goldService.getTransactionHistory(currentPage, pageSize),
    {
      keepPreviousData: true,
    }
  );

  const transactions = transactionHistory?.content || [];
  const totalPages = transactionHistory?.totalPages || 0;
  const totalElements = transactionHistory?.totalElements || 0;

  const handlePreviousPage = () => {
    setCurrentPage((prev) => Math.max(0, prev - 1));
  };

  const handleNextPage = () => {
    setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1));
  };

  return (
    <Layout>
      <div className="min-h-screen bg-gradient-to-br from-dark-50 via-white to-gold-50 p-6">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="flex flex-col md:flex-row md:items-center justify-between mb-8">
            <div>
              <h1 className="text-4xl font-display font-bold text-dark-900 mb-2">
                Transaction History
              </h1>
              <p className="text-lg text-dark-600">
                Track all your gold trading activity and performance
              </p>
            </div>
            <div className="mt-4 md:mt-0 flex items-center gap-4">
              {totalElements > 0 && (
                <div className="text-sm text-dark-600 bg-white/50 px-4 py-2 rounded-xl border border-white/30">
                  <span className="font-semibold">{totalElements}</span> total transactions
                </div>
              )}
            </div>
          </div>

          {/* Search and Filter Bar */}
          <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-6 mb-8 shadow-soft">
            <div className="flex flex-col md:flex-row gap-4">
              <div className="flex-1 relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <Search className="h-5 w-5 text-dark-400" />
                </div>
                <input
                  type="text"
                  placeholder="Search transactions..."
                  className="w-full pl-12 pr-4 py-3 bg-white/50 border border-white/30 rounded-2xl text-dark-900 placeholder-dark-500 focus:outline-none focus:ring-2 focus:ring-gold-400 focus:border-transparent backdrop-blur-sm transition-all duration-200"
                />
              </div>
              <button className="flex items-center gap-2 px-6 py-3 bg-white/50 border border-white/30 rounded-2xl text-dark-700 hover:bg-white/70 transition-all duration-200 font-medium">
                <Filter className="h-5 w-5" />
                Filter
              </button>
            </div>
          </div>

          {/* Transaction Statistics */}
          {totalElements > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-6 shadow-soft">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-success-100 rounded-xl">
                    <TrendingUp className="h-5 w-5 text-success-600" />
                  </div>
                  <span className="font-semibold text-dark-900">Buy Orders</span>
                </div>
                <p className="text-2xl font-bold text-success-600">
                  {transactions.filter(t => t.type === 'BUY').length}
                </p>
              </div>
              
              <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-6 shadow-soft">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-danger-100 rounded-xl">
                    <TrendingDown className="h-5 w-5 text-danger-600" />
                  </div>
                  <span className="font-semibold text-dark-900">Sell Orders</span>
                </div>
                <p className="text-2xl font-bold text-danger-600">
                  {transactions.filter(t => t.type === 'SELL').length}
                </p>
              </div>
              
              <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-6 shadow-soft">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-2 bg-gold-100 rounded-xl">
                    <History className="h-5 w-5 text-gold-600" />
                  </div>
                  <span className="font-semibold text-dark-900">Total Volume</span>
                </div>
                <p className="text-2xl font-bold text-gold-600">
                  {transactions.reduce((sum, t) => sum + t.amount, 0).toFixed(2)} oz
                </p>
              </div>
            </div>
          )}

          {/* Main Content */}
          <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl shadow-soft overflow-hidden">
            {isLoading ? (
              <div className="p-16 text-center">
                <div className="w-12 h-12 border-4 border-gold-200 border-t-gold-600 rounded-full animate-spin mx-auto mb-4"></div>
                <p className="text-lg font-semibold text-dark-600">Loading transactions...</p>
              </div>
            ) : transactions.length === 0 ? (
              <div className="text-center py-20">
                <div className="p-6 bg-gradient-to-br from-dark-100 to-dark-200 rounded-3xl w-32 h-32 mx-auto mb-8 flex items-center justify-center">
                  <History className="h-16 w-16 text-dark-400" />
                </div>
                <h3 className="text-2xl font-bold text-dark-900 mb-4">No transactions yet</h3>
                <p className="text-lg text-dark-600 mb-8 max-w-md mx-auto">
                  Start your gold trading journey to see your transaction history here. All your trades will be tracked automatically.
                </p>
              </div>
            ) : (
              <>
                {/* Desktop Table */}
                <div className="hidden md:block overflow-x-auto">
                  <table className="w-full">
                    <thead>
                      <tr className="border-b border-white/30">
                        <th className="text-left py-6 px-8 font-semibold text-dark-700 text-sm uppercase tracking-wider">
                          Transaction
                        </th>
                        <th className="text-left py-6 px-8 font-semibold text-dark-700 text-sm uppercase tracking-wider">
                          Amount
                        </th>
                        <th className="text-left py-6 px-8 font-semibold text-dark-700 text-sm uppercase tracking-wider">
                          Price per oz
                        </th>
                        <th className="text-left py-6 px-8 font-semibold text-dark-700 text-sm uppercase tracking-wider">
                          Total Value
                        </th>
                        <th className="text-left py-6 px-8 font-semibold text-dark-700 text-sm uppercase tracking-wider">
                          Date & Time
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {transactions.map((transaction, index) => (
                        <tr 
                          key={transaction.id} 
                          className={`border-b border-white/20 hover:bg-white/50 transition-all duration-200 ${
                            index % 2 === 0 ? 'bg-white/20' : 'bg-transparent'
                          }`}
                        >
                          <td className="py-6 px-8">
                            <div className="flex items-center gap-4">
                              <div className={`p-3 rounded-2xl ${
                                transaction.type === 'BUY' 
                                  ? 'bg-success-100 text-success-600' 
                                  : 'bg-danger-100 text-danger-600'
                              }`}>
                                {transaction.type === 'BUY' ? (
                                  <ArrowUpRight className="h-5 w-5" />
                                ) : (
                                  <ArrowDownRight className="h-5 w-5" />
                                )}
                              </div>
                              <div>
                                <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold ${
                                  transaction.type === 'BUY'
                                    ? 'bg-success-100 text-success-700'
                                    : 'bg-danger-100 text-danger-700'
                                }`}>
                                  {transaction.type}
                                </span>
                              </div>
                            </div>
                          </td>
                          <td className="py-6 px-8 text-dark-900 font-semibold">
                            {transaction.amount.toFixed(2)} oz
                          </td>
                          <td className="py-6 px-8 text-dark-900 font-semibold">
                            ${transaction.pricePerUnit.toFixed(2)}
                          </td>
                          <td className="py-6 px-8 text-dark-900 font-bold text-lg">
                            ${transaction.totalPrice.toFixed(2)}
                          </td>
                          <td className="py-6 px-8 text-dark-600">
                            <div>
                              <div className="font-semibold">
                                {new Date(transaction.timestamp).toLocaleDateString()}
                              </div>
                              <div className="text-sm text-dark-500">
                                {new Date(transaction.timestamp).toLocaleTimeString()}
                              </div>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Mobile Cards */}
                <div className="md:hidden p-6 space-y-4">
                  {transactions.map((transaction) => (
                    <div
                      key={transaction.id}
                      className="bg-white/50 border border-white/30 rounded-2xl p-6 hover:bg-white/70 transition-all duration-200"
                    >
                      <div className="flex items-start justify-between mb-4">
                        <div className="flex items-center gap-3">
                          <div className={`p-3 rounded-2xl ${
                            transaction.type === 'BUY' 
                              ? 'bg-success-100 text-success-600' 
                              : 'bg-danger-100 text-danger-600'
                          }`}>
                            {transaction.type === 'BUY' ? (
                              <ArrowUpRight className="h-5 w-5" />
                            ) : (
                              <ArrowDownRight className="h-5 w-5" />
                            )}
                          </div>
                          <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold ${
                            transaction.type === 'BUY'
                              ? 'bg-success-100 text-success-700'
                              : 'bg-danger-100 text-danger-700'
                          }`}>
                            {transaction.type}
                          </span>
                        </div>
                        <div className="text-right">
                          <div className="text-lg font-bold text-dark-900">
                            ${transaction.totalPrice.toFixed(2)}
                          </div>
                          <div className="text-sm text-dark-500">
                            {new Date(transaction.timestamp).toLocaleDateString()}
                          </div>
                        </div>
                      </div>
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <span className="text-sm text-dark-600">Amount</span>
                          <p className="font-semibold text-dark-900">{transaction.amount.toFixed(2)} oz</p>
                        </div>
                        <div>
                          <span className="text-sm text-dark-600">Price per oz</span>
                          <p className="font-semibold text-dark-900">${transaction.pricePerUnit.toFixed(2)}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className="border-t border-white/30 p-6">
                    <div className="flex items-center justify-between">
                      <div className="text-sm text-dark-600">
                        Page <span className="font-semibold">{currentPage + 1}</span> of{' '}
                        <span className="font-semibold">{totalPages}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <button
                          onClick={handlePreviousPage}
                          disabled={currentPage === 0}
                          className="p-3 bg-white/50 border border-white/30 rounded-xl text-dark-600 hover:bg-white/70 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
                        >
                          <ChevronLeft className="h-5 w-5" />
                        </button>
                        <div className="px-4 py-2 bg-white/30 rounded-xl text-sm font-semibold text-dark-700">
                          {currentPage + 1} of {totalPages}
                        </div>
                        <button
                          onClick={handleNextPage}
                          disabled={currentPage >= totalPages - 1}
                          className="p-3 bg-white/50 border border-white/30 rounded-xl text-dark-600 hover:bg-white/70 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
                        >
                          <ChevronRight className="h-5 w-5" />
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default TransactionsPage;