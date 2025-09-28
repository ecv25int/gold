import api from './api';

export interface Client {
  firstName: string;
  lastName: string;
  cedula: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
}

const API_URL = '/clients';

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export const fetchClients = async (page = 0, size = 20): Promise<Page<Client>> => {
  const res = await api.get<Page<Client>>(`${API_URL}?page=${page}&size=${size}`);
  return res.data;
};

export const addClient = async (client: Client): Promise<Client> => {
  const res = await api.post<Client>(API_URL, client);
  return res.data;
};
