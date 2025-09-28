import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './hooks/useAuth.tsx'
import LoginPage from './pages/LoginPage.tsx'
import RegisterPage from './pages/RegisterPage.tsx'
import DashboardPage from './pages/DashboardPage.tsx'
import OperationPage from './pages/OperationPage.tsx'
import ProvidersPage from './pages/ProvidersPage.tsx'
import InventoryPage from './pages/InventoryPage.tsx'
import TransactionsPage from './pages/TransactionsPage.tsx'
import ClientsPage from './pages/ClientsPage.tsx'

function App() {
  const { user, loading } = useAuth()

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-yellow-500"></div>
      </div>
    )
  }

  return (
    <Routes>
      <Route path="/login" element={!user ? <LoginPage /> : <Navigate to="/dashboard" />} />
      <Route path="/register" element={!user ? <RegisterPage /> : <Navigate to="/dashboard" />} />
      <Route path="/" element={user ? <Navigate to="/dashboard" /> : <Navigate to="/login" />} />
      <Route path="/dashboard" element={user ? <DashboardPage /> : <Navigate to="/login" />} />
      <Route path="/operation" element={user ? <OperationPage /> : <Navigate to="/login" />} />
      <Route path="/providers" element={user ? <ProvidersPage /> : <Navigate to="/login" />} />
      <Route path="/inventory" element={user ? <InventoryPage /> : <Navigate to="/login" />} />
      <Route path="/transactions" element={user ? <TransactionsPage /> : <Navigate to="/login" />} />
      <Route path="/clients" element={user ? <ClientsPage /> : <Navigate to="/login" />} />
    </Routes>
  )
}

export default App