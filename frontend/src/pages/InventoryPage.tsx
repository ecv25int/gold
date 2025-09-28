import React, { useEffect, useState } from 'react';
import api from '../services/api';

interface GoldItem {
  id: number;
  carat: number;
  quantityInGrams: number;
  averageBuyPrice: number;
}

const DEFAULT_CARATS = [14, 18, 22, 24];

const InventoryPage: React.FC = () => {
  const [inventory, setInventory] = useState<GoldItem[]>([]);
  const [carats, setCarats] = useState<number[]>(DEFAULT_CARATS);
  const [form, setForm] = useState<Partial<GoldItem>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [newCarat, setNewCarat] = useState('');

  useEffect(() => {
    fetchInventory();
  }, []);

  const fetchInventory = async () => {
  const res = await api.get<GoldItem[]>('/inventory');
  setInventory(res.data);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editingId) {
      await api.put(`/inventory/${editingId}`, form);
    } else {
      await api.post('/inventory', form);
    }
    setForm({});
    setEditingId(null);
    fetchInventory();
  };

  const handleEdit = (item: GoldItem) => {
    setForm(item);
    setEditingId(item.id);
  };

  const handleDelete = async (id: number) => {
    await api.delete(`/inventory/${id}`);
    fetchInventory();
  };

  const handleAddCarat = () => {
    const caratNum = parseInt(newCarat);
    if (!carats.includes(caratNum) && caratNum > 0) {
      setCarats([...carats, caratNum]);
      setNewCarat('');
    }
  };

  const handleRemoveCarat = (carat: number) => {
    setCarats(carats.filter(c => c !== carat));
  };

  return (
    <div className="max-w-3xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-4">Gold Inventory</h2>
      <div className="mb-6">
        <h3 className="text-lg font-semibold mb-2">Carat Categories</h3>
        <div className="flex gap-2 mb-2">
          {carats.map(carat => (
            <span key={carat} className="px-3 py-1 bg-gold-100 rounded-full text-gold-800 font-medium mr-2">
              {carat}K
              <button className="ml-2 text-red-500" onClick={() => handleRemoveCarat(carat)} title="Remove">×</button>
            </span>
          ))}
        </div>
        <input
          type="number"
          min="1"
          value={newCarat}
          onChange={e => setNewCarat(e.target.value)}
          placeholder="Add carat (e.g. 20)"
          className="input w-32 mr-2"
        />
        <button className="btn btn-gold" onClick={handleAddCarat}>Add Carat</button>
      </div>
      <form onSubmit={handleSubmit} className="mb-6 grid grid-cols-1 gap-4">
        <select name="carat" value={form.carat || ''} onChange={handleChange} className="input" required>
          <option value="">Select Carat</option>
          {carats.map(carat => (
            <option key={carat} value={carat}>{carat}K</option>
          ))}
        </select>
  <input name="quantityInGrams" type="number" value={form.quantityInGrams || ''} onChange={handleChange} placeholder="Quantity (grams)" className="input" required />
  <input name="averageBuyPrice" type="number" value={form.averageBuyPrice || ''} onChange={handleChange} placeholder="Average buy price per gram" className="input" required />
        <button type="submit" className="btn btn-gold">{editingId ? 'Update' : 'Add'} Item</button>
        {editingId && <button type="button" className="btn btn-gray" onClick={() => { setEditingId(null); setForm({}); }}>Cancel</button>}
      </form>
      <table className="w-full border">
        <thead>
          <tr>
            <th>Carat</th>
            <th>Quantity (g)</th>
            <th>Price/Gram</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {inventory.map(item => (
            <tr key={item.id}>
              <td>{item.carat}K</td>
              <td>{item.quantityInGrams}</td>
              <td>${item.averageBuyPrice.toFixed(2)}</td>
              <td>
                <button className="btn btn-sm btn-gold mr-2" onClick={() => handleEdit(item)}>Edit</button>
                <button className="btn btn-sm btn-danger" onClick={() => handleDelete(item.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default InventoryPage;
