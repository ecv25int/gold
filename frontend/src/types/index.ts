export * from './auth';

export interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  accountBalance: number
  goldHoldings: number
  role: 'USER' | 'ADMIN'
  createdAt: string
  updatedAt: string
}

export interface GoldPrice {
  id: number
  buyPrice: number
  sellPrice: number
  currency: string
  unit: string
  timestamp: string
  isActive: boolean
}

export interface Transaction {
  id: number
  user: User
  type: 'BUY' | 'SELL'
  goldAmount: number
  pricePerOunce: number
  totalAmount: number
  status: 'PENDING' | 'COMPLETED' | 'CANCELLED' | 'FAILED'
  description?: string
  createdAt: string
  completedAt?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  firstName: string
  lastName: string
}

export interface AuthResponse {
  token: string
  type: string
  username: string
  authorities: string[]
}