import React, { useEffect, useState } from 'react';
import api from '../services/api';

interface Provider {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
}

const ProvidersPage: React.FC = () => {
  const [providers, setProviders] = useState<Provider[]>([]);
  const [form, setForm] = useState<Partial<Provider>>({});
  const [editingId, setEditingId] = useState<number | null>(null);

  useEffect(() => {
    fetchProviders();
  }, []);

  const fetchProviders = async () => {
    const res = await api.get<Provider[]>('/providers');
    setProviders(res.data);
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
    <div className="max-w-3xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-4">Providers</h2>
      <form onSubmit={handleSubmit} className="mb-6 grid grid-cols-1 gap-4">
        <input name="name" value={form.name || ''} onChange={handleChange} placeholder="Name" className="input" required />
        <input name="email" value={form.email || ''} onChange={handleChange} placeholder="Email" className="input" required />
        <input name="phone" value={form.phone || ''} onChange={handleChange} placeholder="Phone" className="input" />
        <input name="address" value={form.address || ''} onChange={handleChange} placeholder="Address" className="input" />
        <button type="submit" className="btn btn-gold">{editingId ? 'Update' : 'Add'} Provider</button>
        {editingId && <button type="button" className="btn btn-gray" onClick={() => { setEditingId(null); setForm({}); }}>Cancel</button>}
      </form>
      <table className="w-full border">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {providers.map((provider) => (
            <tr key={provider.id}>
              <td>{provider.name}</td>
              <td>{provider.email}</td>
              <td>{provider.phone}</td>
              <td>{provider.address}</td>
              <td>
                <button className="btn btn-sm btn-gold mr-2" onClick={() => handleEdit(provider)}>Edit</button>
                <button className="btn btn-sm btn-danger" onClick={() => handleDelete(provider.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProvidersPage;
