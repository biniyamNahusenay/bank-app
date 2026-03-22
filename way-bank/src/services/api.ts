import axios from 'axios';
import { AuthResponse, Account, TransactionRequest, Transaction } from '../types';

const API_BASE_URL = '/api'; // Proxy will forward to localhost:8080/api

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Interceptor to add token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('banktoken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authService = {
  register: async (data: any) => {
    return api.post('/auth/register', data);
  },
  login: async (data: any) => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    if (response.data.accessToken) {
      localStorage.setItem('banktoken', response.data.accessToken);
    }
    return response.data;
  },
  logout: () => {
    localStorage.removeItem('banktoken');
  }
};

export const accountService = {
  createAccount: async (type: string = 'SAVINGS') => {
    return api.post<Account>(`/accounts?accountType=${type}`);
  },
  getMyAccount: async () => {
    return api.get<Account>('/accounts/me');
  },
  getAccountByNum: async (accountNum: string) => {
    return api.get<Account>(`/accounts/${accountNum}`);
  }
};

export interface TransactionHistoryApiItem {
  transactionId: string;
  sourceAccountNumber: string;
  destinationAccountNumber: string;
  amount: number;
  description: string;
  timestamp: string;
  status: string;
  transactionType: string;
}

export interface MiniStatementResponse {
  transactions: Transaction[];
  currentPage: number;
  pageSize: number;
  totalPages: number;
  totalTransactions: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export const transactionService = {
  transfer: async (data: TransactionRequest) => {
    return api.post('/transactions/transfer', data);
  },
  getHistory: async (
    accountNumber: string,
    filter = 'all',
    page = 0,
    size = 10
  ): Promise<{ data: MiniStatementResponse }> => {
    const res = await api.get<Omit<MiniStatementResponse, 'transactions'> & { transactions: TransactionHistoryApiItem[] }>(
      `/transactions/history/${accountNumber}?filter=${filter}&page=${page}&size=${size}`
    );
    const mappedTransactions = res.data.transactions.map((item): Transaction => ({
      id: item.transactionId,
      fromAccountNumber: item.sourceAccountNumber,
      toAccountNumber: item.destinationAccountNumber,
      amount: Number(item.amount),
      description: item.description || '',
      transactionType: (item.transactionType || 'UNKNOWN') as Transaction['transactionType'],
      timestamp: item.timestamp,
    }));

    return {
      data: {
        ...res.data,
        transactions: mappedTransactions,
      },
    };
  }
};

export default api;
