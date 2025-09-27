import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './hooks/useAuth.tsx'
import LoginPage from './pages/LoginPage.tsx'
import RegisterPage from './pages/RegisterPage.tsx'
import DashboardPage from './pages/DashboardPage.tsx'
import TradingPage from './pages/TradingPage.tsx'
import TransactionsPage from './pages/TransactionsPage.tsx'

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
      <Route path="/trading" element={user ? <TradingPage /> : <Navigate to="/login" />} />
      <Route path="/transactions" element={user ? <TransactionsPage /> : <Navigate to="/login" />} />
    </Routes>
  )
}

export default App