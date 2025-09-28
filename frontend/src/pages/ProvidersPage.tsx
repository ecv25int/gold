import React, { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import api from '../services/api';

interface Provider {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  address: string;
  cedula?: string;
  companyName?: string;
  providerType?: string;
}

const ProvidersPage: React.FC = () => {
  const [providers, setProviders] = useState<Provider[]>([]);
  const [form, setForm] = useState<Partial<Provider>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    fetchProviders();
  }, []);

  const fetchProviders = async () => {
    const res = await api.get('/providers', { params: { page, size: 10 } });
    setProviders(res.data.content || []);
    setTotalPages(res.data.totalPages || 1);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editingId) {
      await api.put(`/providers/${editingId}`, form);
    } else {
      await api.post('/providers', form);
    }
    setForm({});
    setEditingId(null);
    fetchProviders();
  };

  const handleEdit = (provider: Provider) => {
    setForm(provider);
    setEditingId(provider.id);
  };

  const handleDelete = async (id: number) => {
    await api.delete(`/providers/${id}`);
    fetchProviders();
  };

  return (
    <Layout>
      <div className="min-h-screen bg-gradient-to-br from-dark-50 via-white to-gold-50 p-6">
        <div className="max-w-4xl mx-auto">
          <div className="mb-8">
            <h1 className="text-4xl font-display font-bold text-dark-900 mb-2">Providers Registry</h1>
            <p className="text-lg text-dark-600">Register and manage your providers for purchasing and inventory</p>
          </div>
          <form className="space-y-4 mb-6" onSubmit={handleSubmit}>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">First Name</label>
                <input name="firstName" type="text" value={form.firstName || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="First name" required />
              </div>
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Last Name</label>
                <input name="lastName" type="text" value={form.lastName || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Last name" required />
              </div>
            </div>
            <div>
              <label className="block text-lg font-semibold text-dark-900 mb-2">Cedula</label>
              <input name="cedula" type="text" value={form.cedula || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Cedula" required />
            </div>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Email (optional)</label>
                <input name="email" type="email" value={form.email || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Email" />
              </div>
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Phone Number (optional)</label>
                <input name="phoneNumber" type="text" value={form.phoneNumber || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Phone number" />
              </div>
            </div>
            <div>
              <label className="block text-lg font-semibold text-dark-900 mb-2">Address (optional)</label>
              <input name="address" type="text" value={form.address || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Address" />
            </div>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Company Name (optional)</label>
                <input name="companyName" type="text" value={form.companyName || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Company Name" />
              </div>
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Provider Type (optional)</label>
                <input name="providerType" type="text" value={form.providerType || ''} onChange={handleChange} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Provider Type" />
              </div>
            </div>
            <button type="submit" className="w-full py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gradient-to-r from-gold-500 to-gold-700 hover:from-gold-600 hover:to-gold-800 focus:outline-none focus:ring-2 focus:ring-gold-400 shadow-lg transition-all duration-200">
              {editingId ? 'Update' : 'Register'} Provider
            </button>
            {editingId && <button type="button" className="w-full py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gray-400 hover:bg-gray-500 focus:outline-none focus:ring-2 focus:ring-gray-300 shadow-lg transition-all duration-200 mt-2" onClick={() => { setEditingId(null); setForm({}); }}>Cancel</button>}
          </form>
          {/* List of registered providers */}
          {providers.length > 0 && (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-gold-100">
                    <th className="py-2 px-4">First Name</th>
                    <th className="py-2 px-4">Last Name</th>
                    <th className="py-2 px-4">Cedula</th>
                    <th className="py-2 px-4">Email</th>
                    <th className="py-2 px-4">Phone Number</th>
                    <th className="py-2 px-4">Address</th>
                    <th className="py-2 px-4">Company</th>
                    <th className="py-2 px-4">Type</th>
                    <th className="py-2 px-4">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {providers.map((provider, idx) => (
                    <tr key={provider.id} className="border-b">
                      <td className="py-2 px-4">{provider.firstName}</td>
                      <td className="py-2 px-4">{provider.lastName}</td>
                      <td className="py-2 px-4">{provider.cedula}</td>
                      <td className="py-2 px-4">{provider.email}</td>
                      <td className="py-2 px-4">{provider.phoneNumber}</td>
                      <td className="py-2 px-4">{provider.address}</td>
                      <td className="py-2 px-4">{provider.companyName}</td>
                      <td className="py-2 px-4">{provider.providerType}</td>
                      <td className="py-2 px-4">
                        <button className="btn btn-sm btn-gold mr-2" onClick={() => handleEdit(provider)}>Edit</button>
                        <button className="btn btn-sm btn-danger" onClick={() => handleDelete(provider.id)}>Delete</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          <div className="flex justify-between items-center mt-4">
            <button className="btn" disabled={page === 0} onClick={() => { setPage(page - 1); fetchProviders(); }}>Previous</button>
            <span>Page {page + 1} of {totalPages}</span>
            <button className="btn" disabled={page >= totalPages - 1} onClick={() => { setPage(page + 1); fetchProviders(); }}>Next</button>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default ProvidersPage;
