import React, { useState } from 'react';
import Layout from '../components/Layout';

const ClientsPage: React.FC = () => {
  const [clients, setClients] = useState<Array<{
    nombre: string;
    identificacionTipo: string;
    identificacionNumero: string;
    ubicacion?: string;
    telefono?: string;
    correoElectronico?: string;
  }>>([]);
  const [newClient, setNewClient] = useState({
    nombre: '',
    identificacionTipo: '',
    identificacionNumero: '',
    ubicacion: '',
    telefono: '',
    correoElectronico: '',
  });

  const handleClientInput = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setNewClient(prev => ({ ...prev, [name]: value }));
  };

  const handleAddClient = (e: React.FormEvent) => {
    e.preventDefault();
    if (newClient.nombre && newClient.identificacionTipo && newClient.identificacionNumero) {
      setClients(prev => [...prev, newClient]);
      setNewClient({
        nombre: '',
        identificacionTipo: '',
        identificacionNumero: '',
        ubicacion: '',
        telefono: '',
        correoElectronico: '',
      });
    }
  };

  return (
    <Layout>
      <div className="min-h-screen bg-gradient-to-br from-dark-50 via-white to-gold-50 p-6">
        <div className="max-w-4xl mx-auto">
          <div className="mb-8">
            <h1 className="text-4xl font-display font-bold text-dark-900 mb-2">Clients Registry</h1>
            <p className="text-lg text-dark-600">Register and manage your clients for invoicing</p>
          </div>
          <form className="space-y-4 mb-6" onSubmit={handleAddClient}>
            <div>
              <label className="block text-lg font-semibold text-dark-900 mb-2">Nombre</label>
              <input name="nombre" type="text" value={newClient.nombre} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Full name" required />
            </div>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Identificación Tipo</label>
                <select name="identificacionTipo" value={newClient.identificacionTipo} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" required>
                  <option value="">Seleccione</option>
                  <option value="01">Física</option>
                  <option value="02">Jurídica</option>
                  <option value="03">DIMEX</option>
                  <option value="04">NITE</option>
                </select>
              </div>
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Identificación Número</label>
                <input name="identificacionNumero" type="text" value={newClient.identificacionNumero} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Número" required />
              </div>
            </div>
            <div>
              <label className="block text-lg font-semibold text-dark-900 mb-2">Ubicación (opcional)</label>
              <input name="ubicacion" type="text" value={newClient.ubicacion} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Dirección" />
            </div>
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Teléfono (opcional)</label>
                <input name="telefono" type="text" value={newClient.telefono} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Teléfono" />
              </div>
              <div className="flex-1">
                <label className="block text-lg font-semibold text-dark-900 mb-2">Correo Electrónico (opcional)</label>
                <input name="correoElectronico" type="email" value={newClient.correoElectronico} onChange={handleClientInput} className="w-full px-4 py-3 rounded-xl border border-gold-300 bg-white/50 text-dark-900 font-semibold" placeholder="Correo electrónico" />
              </div>
            </div>
            <button type="submit" className="w-full py-4 px-8 rounded-2xl text-lg font-bold text-white bg-gradient-to-r from-gold-500 to-gold-700 hover:from-gold-600 hover:to-gold-800 focus:outline-none focus:ring-2 focus:ring-gold-400 shadow-lg transition-all duration-200">
              Registrar Cliente
            </button>
          </form>
          {/* List of registered clients */}
          {clients.length > 0 && (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-gold-100">
                    <th className="py-2 px-4">Nombre</th>
                    <th className="py-2 px-4">Tipo</th>
                    <th className="py-2 px-4">Número</th>
                    <th className="py-2 px-4">Ubicación</th>
                    <th className="py-2 px-4">Teléfono</th>
                    <th className="py-2 px-4">Correo</th>
                  </tr>
                </thead>
                <tbody>
                  {clients.map((c, idx) => (
                    <tr key={idx} className="border-b">
                      <td className="py-2 px-4">{c.nombre}</td>
                      <td className="py-2 px-4">{c.identificacionTipo}</td>
                      <td className="py-2 px-4">{c.identificacionNumero}</td>
                      <td className="py-2 px-4">{c.ubicacion}</td>
                      <td className="py-2 px-4">{c.telefono}</td>
                      <td className="py-2 px-4">{c.correoElectronico}</td>
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
