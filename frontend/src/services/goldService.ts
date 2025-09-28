import api from './api';

export interface GoldPrice {
  id: number;
  buyPrice: number;
  sellPrice: number;
  currency: string;
  // Removed unit; all prices are per gram
  timestamp: string;
  active: boolean;
}

export interface Transaction {
  id: number;
  user: { username: string };
  type: 'BUY' | 'SELL';
  amount: number;
  pricePerGram: number;
  totalPrice: number;
  timestamp: string;
}

export interface TransactionPage {
  content: Transaction[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export const goldService = {
  getCurrentPrice: async (): Promise<GoldPrice> => {
    const response = await api.get('/gold-prices/current');
    return response.data;
  },

  buyGold: async (amount: number): Promise<Transaction> => {
    const response = await api.post(`/transactions/buy?amount=${amount}`);
    return response.data;
  },

  sellGold: async (amount: number): Promise<Transaction> => {
    const response = await api.post(`/transactions/sell?amount=${amount}`);
    return response.data;
  },

  getTransactionHistory: async (page = 0, size = 10): Promise<TransactionPage> => {
    const response = await api.get(`/transactions/history?page=${page}&size=${size}`);
    return response.data;
  },
};