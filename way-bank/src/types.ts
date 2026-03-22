export interface User {
  username: string;
  email: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface Account {
  accountNumber: string;
  balance: number;
  accountType: string;
}

export interface TransactionRequest {
  fromAccountNumber: string;
  toAccountNumber: string;
  amount: number;
  description: string;
}

export interface Transaction {
  id: string;
  amount: number;
  description: string;
  transactionType: 'DEBIT' | 'CREDIT' | 'UNKNOWN';
  timestamp: string;
  fromAccountNumber: string;
  toAccountNumber: string;
}
