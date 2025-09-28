import React, { useState } from 'react';
import { useEffect } from 'react';
import { fetchClients, addClient, Client, Page } from '../services/clientService';
import Layout from '../components/Layout';

const ClientsPage: React.FC = () => {
  const [clients, setClients] = useState<Client[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [newClient, setNewClient] = useState<Client>({
    firstName: '',
    lastName: '',
    cedula: '',
    email: '',
    phoneNumber: '',
    address: '',
  });

  const handleClientInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setNewClient(prev => ({ ...prev, [name]: value }));
  };

  const handleAddClient = async (e: React.FormEvent) => {
    e.preventDefault();
    if (newClient.firstName && newClient.lastName && newClient.cedula) {
      try {
        const saved = await addClient(newClient);
        setClients(prev => [...prev, saved]);
        setNewClient({
          firstName: '',
          lastName: '',
          cedula: '',
          email: '',
          phoneNumber: '',
          address: '',
        });
      } catch (err) {
        alert('Error adding client');
      }
    }
  };

  useEffect(() => {
    fetchClients(page, 20)
      .then((p: Page<Client>) => {
        setClients(p.content);
        setTotalPages(p.totalPages);
      })
      .catch(() => setClients([]));
  }, [page]);

  return (
    <Layout>
      <div className="min-h-screen bg-gradient-to-br from-dark-50 via-white to-gold-50 p-6">
        <div className="max-w-4xl mx-auto">
          <div className="mb-8">
            <h1 className="text-4xl font-display font-bold text-dark-900 mb-2">Clients Registry</h1>
            <p className="text-lg text-dark-600">Register and manage your clients for invoicing</p>
          </div>
          <form className="space-y-4 mb-6" onSubmit={handleAddClient}>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">First Name</label>
                <input name="firstName" type="text" value={newClient.firstName} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="First name" required />
              </div>
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Last Name</label>
                <input name="lastName" type="text" value={newClient.lastName} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Last name" required />
              </div>
            </div>
            <div>
              <label className="block text-lg font-semibold text-dark-900 mb-2">Cedula</label>
              <input name="cedula" type="text" value={newClient.cedula} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Cedula" required />
            </div>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Email (optional)</label>
                <input name="email" type="email" value={newClient.email} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Email" />
              </div>
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Phone Number (optional)</label>
                <input name="phoneNumber" type="text" value={newClient.phoneNumber} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Phone number" />
              </div>
            </div>
            <div>
              <label className="block text-lg font-semibold text-dark-900 mb-2">Address (optional)</label>
              <input name="address" type="text" value={newClient.address} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Address" />
            </div>
            <button type="submit" className="w-full py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gradient-to-r from-gold-500 to-gold-700 hover:from-gold-600 hover:to-gold-800 focus:outline-none focus:ring-2 focus:ring-gold-400 shadow-lg transition-all duration-200">
              Register Client
            </button>
          </form>
          {/* List of registered clients */}
          {clients.length > 0 && (
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
                  </tr>
                </thead>
                <tbody>
                  {clients.map((c, idx) => (
                    <tr key={idx} className="border-b">
                      <td className="py-2 px-4">{c.firstName}</td>
                      <td className="py-2 px-4">{c.lastName}</td>
                      <td className="py-2 px-4">{c.cedula}</td>
                      <td className="py-2 px-4">{c.email}</td>
                      <td className="py-2 px-4">{c.phoneNumber}</td>
                      <td className="py-2 px-4">{c.address}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default ClientsPage;
