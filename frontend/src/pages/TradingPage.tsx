
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout';


type CartItem = {
  carat: number;
  grams: number;
  pricePerGram: number;
};

const caratOptions = [14, 18, 22, 24];


const TradingPage: React.FC = () => {
  // ...existing code...
  const [customer, setCustomer] = useState({ name: '', email: '', phone: '' });
  const [customerSet, setCustomerSet] = useState(false);
  const [carat, setCarat] = useState(24);
  const [grams, setGrams] = useState(1);
  const [pricePerGram, setPricePerGram] = useState(0);
  const [action, setAction] = useState<'BUY' | 'SELL'>('BUY'); // Invoice-wide action
  const [cart, setCart] = useState<CartItem[]>([]);
  const [showInvoice, setShowInvoice] = useState(false);
  // ...existing code...

  // Simulate fetching carat prices (replace with API call in production)
  const caratPrices: { carat: number; pricePerGram: number }[] = [
    { carat: 14, pricePerGram: 38.5 },
    { carat: 18, pricePerGram: 49.2 },
    { carat: 22, pricePerGram: 60.1 },
    { carat: 24, pricePerGram: 65.0 },
  ];

  React.useEffect(() => {
    const found = caratPrices.find(p => p.carat === carat);
    setPricePerGram(found ? found.pricePerGram : 0);
  }, [carat]);

  const handleCustomerSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (customer.name && customer.email && customer.phone) {
      setCustomerSet(true);
    }
  };

  const handleAddToCart = (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerSet) return;
    if (grams > 0 && pricePerGram > 0) {
      setCart(prev => {
        const idx = prev.findIndex(item => item.carat === carat && item.pricePerGram === pricePerGram);
        if (idx !== -1) {
          // Add grams to existing item
          const updated = [...prev];
          updated[idx] = { ...updated[idx], grams: updated[idx].grams + grams };
          return updated;
        }
        return [...prev, { carat, grams, pricePerGram }];
      });
      setGrams(1);
    }
  };

  const handleRemoveItem = (idx: number) => {
    setCart(cart.filter((_, i) => i !== idx));
  };

  const total = cart.reduce((sum, item) => sum + item.grams * item.pricePerGram, 0);

  const handleCheckout = () => {
    setShowInvoice(true);
  };

  return (
    <Layout>
      <div className="min-h-screen bg-gradient-to-br from-dark-50 via-white to-gold-50 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="mb-8">
            <div className="flex justify-between items-center mb-2">
              <div>
                <h1 className="text-4xl font-display font-bold text-dark-900">Gold Trading</h1>
                <p className="text-lg text-dark-600">Trade gold at live market prices with instant execution</p>
              </div>
              <Link to="/ClientsPage" className="inline-block py-2 px-6 rounded-xl font-bold bg-gold-100 text-gold-800 border border-gold-400 hover:bg-gold-200 transition-all duration-150 cursor-pointer">
                Clients Registry
              </Link>
            </div>
          </div>
          {/* Clients Section removed, now in ClientsPage.tsx */}
          {/* Customer Data Section */}
          {!customerSet && (
            <div className="mb-12">
              <h2 className="text-2xl font-bold text-dark-900 mb-4">Customer Information</h2>
              <form className="space-y-6" onSubmit={handleCustomerSubmit}>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Buy or Sell</label>
                  <div className="flex gap-4">
                    <button type="button" onClick={() => setAction('BUY')} className={`py-2 px-6 rounded-xl font-bold ${action === 'BUY' ? 'bg-success-600 text-white' : 'bg-white text-success-600 border border-success-600'}`}>Buy</button>
                    <button type="button" onClick={() => setAction('SELL')} className={`py-2 px-6 rounded-xl font-bold ${action === 'SELL' ? 'bg-danger-600 text-white' : 'bg-white text-danger-600 border border-danger-600'}`}>Sell</button>
                  </div>
                </div>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Name</label>
                  <input type="text" value={customer.name} onChange={e => setCustomer({ ...customer, name: e.target.value })} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Customer name" required />
                </div>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Email</label>
                  <input type="email" value={customer.email} onChange={e => setCustomer({ ...customer, email: e.target.value })} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Customer email" required />
                </div>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Phone</label>
                  <input type="tel" value={customer.phone} onChange={e => setCustomer({ ...customer, phone: e.target.value })} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Customer phone" required />
                </div>
                <button type="submit" className="w-full py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gradient-to-r from-success-500 to-success-700 hover:from-success-600 hover:to-success-800 focus:outline-none focus:ring-2 focus:ring-success-400 shadow-lg transition-all duration-200">
                  Save Customer
                </button>
              </form>
            </div>
          )}
          {/* Carat/Gram Trading Section */}
          {customerSet && !showInvoice && (
            <div className="mb-12">
              <h2 className="text-2xl font-bold text-dark-900 mb-4">Add Gold Product</h2>
              <form className="space-y-6" onSubmit={handleAddToCart}>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Carat</label>
                  <select value={carat} onChange={e => setCarat(Number(e.target.value))} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold">
                    {caratOptions.map(c => (
                      <option key={c} value={c}>{c}k</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Amount (grams)</label>
                  <input type="number" min="0.1" step="0.1" value={grams} onChange={e => setGrams(Number(e.target.value))} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Enter grams" />
                </div>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Price per gram</label>
                  <span className="text-success-700 font-bold">${pricePerGram.toFixed(2)} ({carat}k)</span>
                </div>
                <div>
                  <label className="block text-lg font-semibold text-dark-900 mb-2">Total for this item</label>
                  <input type="text" className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" value={`$${(grams * pricePerGram).toFixed(2)}`} readOnly />
                </div>
                <button type="submit" className="w-full py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gradient-to-r from-gold-500 to-gold-700 hover:from-gold-600 hover:to-gold-800 focus:outline-none focus:ring-2 focus:ring-gold-400 shadow-lg transition-all duration-200">
                  Add to Cart
                </button>
              </form>
            </div>
          )}
          {/* Cart Section */}
          {customerSet && cart.length > 0 && !showInvoice && (
            <div className="mb-12">
              <h2 className="text-2xl font-bold text-dark-900 mb-4">Cart</h2>
              <table className="w-full text-left border-collapse mb-6">
                <thead>
                  <tr className="bg-gold-100">
                    <th className="py-3 px-4">Action</th>
                    <th className="py-3 px-4">Carat</th>
                    <th className="py-3 px-4">Grams</th>
                    <th className="py-3 px-4">Price/Gram</th>
                    <th className="py-3 px-4">Total</th>
                    <th className="py-3 px-4">Remove</th>
                  </tr>
                </thead>
                <tbody>
                  {cart.map((item, idx) => (
                    <tr key={idx} className="border-b">
                      <td className="py-2 px-4">{action}</td>
                      <td className="py-2 px-4">{item.carat}k</td>
                      <td className="py-2 px-4">{item.grams}</td>
                      <td className="py-2 px-4">${item.pricePerGram.toFixed(2)}</td>
                      <td className="py-2 px-4">${(item.grams * item.pricePerGram).toFixed(2)}</td>
                      <td className="py-2 px-4">
                        <button onClick={() => handleRemoveItem(idx)} className="text-danger-600 hover:underline">Remove</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {/* Invoice Summary */}
              <div className="bg-white/80 rounded-2xl p-6 shadow-soft">
                <h3 className="text-xl font-bold text-dark-900 mb-4">Invoice Summary</h3>
                <div className="flex justify-between items-center mb-2">
                  <span className="font-semibold text-dark-700">Total Items</span>
                  <span className="font-bold text-dark-900">{cart.length}</span>
                </div>
                <div className="flex justify-between items-center mb-2">
                  <span className="font-semibold text-dark-700">Total Amount</span>
                  <span className="font-bold text-gold-700 text-2xl">${total.toFixed(2)}</span>
                </div>
                <button onClick={handleCheckout} className="w-full mt-6 py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gradient-to-r from-success-500 to-success-700 hover:from-success-600 hover:to-success-800 focus:outline-none focus:ring-2 focus:ring-success-400 shadow-lg transition-all duration-200">
                  Checkout
                </button>
              </div>
            </div>
          )}
          {/* Final Invoice Section */}
          {showInvoice && (
            <div className="mb-12">
              <h2 className="text-2xl font-bold text-dark-900 mb-4">Final Invoice</h2>
              <div className="bg-white/90 rounded-2xl p-8 shadow-soft mb-6">
                <h3 className="text-xl font-bold text-dark-900 mb-4">Customer Information</h3>
                <div className="mb-2"><span className="font-semibold">Name:</span> {customer.name}</div>
                <div className="mb-2"><span className="font-semibold">Email:</span> {customer.email}</div>
                <div className="mb-2"><span className="font-semibold">Phone:</span> {customer.phone}</div>
                <div className="mb-2"><span className="font-semibold">Action:</span> {action}</div>
              </div>
              <div className="bg-white/80 rounded-2xl p-6 shadow-soft mb-6">
                <h3 className="text-xl font-bold text-dark-900 mb-4">Products</h3>
                <table className="w-full text-left border-collapse mb-6">
                  <thead>
                    <tr className="bg-gold-100">
                      <th className="py-3 px-4">Carat</th>
                      <th className="py-3 px-4">Grams</th>
                      <th className="py-3 px-4">Price/Gram</th>
                      <th className="py-3 px-4">Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {cart.map((item, idx) => (
                      <tr key={idx} className="border-b">
                        <td className="py-2 px-4">{item.carat}k</td>
                        <td className="py-2 px-4">{item.grams}</td>
                        <td className="py-2 px-4">${item.pricePerGram.toFixed(2)}</td>
                        <td className="py-2 px-4">${(item.grams * item.pricePerGram).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                <div className="flex justify-between items-center mb-2">
                  <span className="font-semibold text-dark-700">Total Items</span>
                  <span className="font-bold text-dark-900">{cart.length}</span>
                </div>
                <div className="flex justify-between items-center mb-2">
                  <span className="font-semibold text-dark-700">Total Amount</span>
                  <span className="font-bold text-gold-700 text-2xl">${total.toFixed(2)}</span>
                </div>
              </div>
              <div className="text-center mt-8">
                <span className="text-lg font-bold text-success-700">Thank you for your purchase!</span>
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default TradingPage;