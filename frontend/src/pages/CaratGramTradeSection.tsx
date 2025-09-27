import React, { useState } from 'react';
import { useQuery, useMutation } from 'react-query';
import { caratService, CaratPrice } from '../services/caratService';
import toast from 'react-hot-toast';

const CaratGramTradeSection: React.FC = () => {
  const [carat, setCarat] = useState(24);
  const [grams, setGrams] = useState(1);
  const [tradeType, setTradeType] = useState<'BUY' | 'SELL'>('BUY');

  const { data: caratPrices, isLoading } = useQuery('caratPrices', caratService.getCaratPrices);
  const selectedPrice = caratPrices?.find((p: CaratPrice) => p.carat === carat)?.pricePerGram || 0;
  const total = grams * selectedPrice;

  const buyMutation = useMutation(() => caratService.buyGoldByCarat(carat, grams), {
    onSuccess: () => {
      toast.success('Gold purchase successful!');
      setGrams(1);
    },
    onError: (error: any) => {
      const message = error.response?.data || error.message || 'Purchase failed';
      toast.error(message);
    },
  });

  const sellMutation = useMutation(() => caratService.sellGoldByCarat(carat, grams), {
    onSuccess: () => {
      toast.success('Gold sale successful!');
      setGrams(1);
    },
    onError: (error: any) => {
      const message = error.response?.data || error.message || 'Sale failed';
      toast.error(message);
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (tradeType === 'BUY') {
      buyMutation.mutate();
    } else {
      sellMutation.mutate();
    }
  };

  return (
    <div className="mb-12">
      <h2 className="text-2xl font-bold text-dark-900 mb-4">Buy/Sell by Carat & Grams</h2>
      <div className="bg-white/70 backdrop-blur-sm border border-white/50 rounded-3xl p-8 shadow-soft">
        <form className="space-y-6" onSubmit={handleSubmit}>
          <div>
            <label className="block text-lg font-semibold text-dark-900 mb-2">Carat</label>
            <select value={carat} onChange={e => setCarat(Number(e.target.value))} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold">
              {caratPrices?.map((p: CaratPrice) => (
                <option key={p.carat} value={p.carat}>{p.carat}k</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-lg font-semibold text-dark-900 mb-2">Amount (grams)</label>
            <input type="number" min="0.1" step="0.1" value={grams} onChange={e => setGrams(Number(e.target.value))} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Enter grams" />
          </div>
          <div>
            <label className="block text-lg font-semibold text-dark-900 mb-2">Price per gram</label>
            <div className="flex gap-4 flex-wrap">
              {caratPrices?.map((p: CaratPrice) => (
                <span key={p.carat} className="text-success-700 font-bold">${p.pricePerGram.toFixed(2)} ({p.carat}k)</span>
              ))}
            </div>
          </div>
          <div>
            <label className="block text-lg font-semibold text-dark-900 mb-2">Total</label>
            <input type="text" className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" value={`$${total.toFixed(2)}`} readOnly />
          </div>
          <div className="flex gap-4">
            <button type="button" onClick={() => setTradeType('BUY')} className={`py-2 px-6 rounded-xl font-bold ${tradeType === 'BUY' ? 'bg-success-600 text-white' : 'bg-white text-success-600 border border-success-600'}`}>Buy</button>
            <button type="button" onClick={() => setTradeType('SELL')} className={`py-2 px-6 rounded-xl font-bold ${tradeType === 'SELL' ? 'bg-danger-600 text-white' : 'bg-white text-danger-600 border border-danger-600'}`}>Sell</button>
          </div>
          <button type="submit" disabled={isLoading || buyMutation.isLoading || sellMutation.isLoading} className="w-full py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gradient-to-r from-gold-500 to-gold-700 hover:from-gold-600 hover:to-gold-800 focus:outline-none focus:ring-2 focus:ring-gold-400 shadow-lg transition-all duration-200">
            {tradeType === 'BUY' ? 'Buy' : 'Sell'} Gold by Carat
          </button>
        </form>
      </div>
    </div>
  );
};

export default CaratGramTradeSection;
