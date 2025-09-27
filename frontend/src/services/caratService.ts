import api from './api';

export interface CaratPrice {
  id: number;
  carat: number;
  pricePerGram: number;
}

export const caratService = {
  getCaratPrices: async (): Promise<CaratPrice[]> => {
    const response = await api.get('/carat-prices');
    return response.data;
  },
  buyGoldByCarat: async (carat: number, grams: number): Promise<any> => {
    const response = await api.post(`/carat-trade/buy?carat=${carat}&grams=${grams}`);
    return response.data;
  },
  sellGoldByCarat: async (carat: number, grams: number): Promise<any> => {
    const response = await api.post(`/carat-trade/sell?carat=${carat}&grams=${grams}`);
    return response.data;
  },
};
