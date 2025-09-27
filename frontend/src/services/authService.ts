import api from './api';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  authorities: Array<{ authority: string }>;
}

export interface RegisterResponse {
  message: string;
  username: string;
}

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post('/auth/signin', data);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<RegisterResponse> => {
    const response = await api.post('/auth/signup', data);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};