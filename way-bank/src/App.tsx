import React, { useState, useEffect, Component, ReactNode } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { 
  Wallet, 
  Send, 
  History, 
  User as UserIcon, 
  Plus, 
  LogOut, 
  ArrowRightLeft,
  ChevronRight,
  CreditCard,
  Bell,
  Search
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { authService, accountService, transactionService } from './services/api';
import { Account, Transaction } from './types';
import { cn } from './lib/utils';

type HistoryFilter = 'all' | 'debited' | 'credited';

// --- Components ---

const Button = ({ className, variant = 'primary', ...props }: React.ButtonHTMLAttributes<HTMLButtonElement> & { variant?: 'primary' | 'secondary' | 'outline' | 'ghost' }) => {
  const variants = {
    primary: 'bg-black text-white hover:bg-zinc-800',
    secondary: 'bg-zinc-100 text-black hover:bg-zinc-200',
    outline: 'border border-zinc-200 text-black hover:bg-zinc-50',
    ghost: 'text-zinc-500 hover:text-black hover:bg-zinc-50'
  };
  return (
    <button 
      className={cn('px-4 py-2 rounded-xl font-medium transition-all active:scale-95 disabled:opacity-50', variants[variant], className)} 
      {...props} 
    />
  );
};

const Input = ({ className, ...props }: React.InputHTMLAttributes<HTMLInputElement>) => (
  <input 
    className={cn('w-full px-4 py-3 bg-zinc-50 border border-zinc-100 rounded-xl focus:outline-none focus:ring-2 focus:ring-black/5 focus:border-black transition-all', className)} 
    {...props} 
  />
);

const Card = ({ children, className }: { children: React.ReactNode, className?: string }) => (
  <div className={cn('bg-white p-6 rounded-3xl shadow-sm border border-zinc-100', className)}>
    {children}
  </div>
);

// --- Pages ---

const LoginPage = ({ onToggle }: { onToggle: () => void }) => {
  const { login } = useAuth();
  const [formData, setFormData] = useState({ usernameOrEmail: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const res = await authService.login(formData);
      localStorage.setItem('bankuser', formData.usernameOrEmail);
      login(res.accessToken);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-center px-6 bg-white">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-md w-full mx-auto space-y-8"
      >
        <div className="text-center">
          <div className="w-16 h-16 bg-black rounded-2xl flex items-center justify-center mx-auto mb-6">
            <Wallet className="text-white w-8 h-8" />
          </div>
          <h1 className="text-3xl font-bold tracking-tight">Welcome back</h1>
          <p className="text-zinc-500 mt-2 text-sm">Enter your details to access your account</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Username or Email</label>
            <Input 
              placeholder="e.g. bini" 
              value={formData.usernameOrEmail}
              onChange={e => setFormData({ ...formData, usernameOrEmail: e.target.value })}
              required
            />
          </div>
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Password</label>
            <Input 
              type="password" 
              placeholder="••••••••" 
              value={formData.password}
              onChange={e => setFormData({ ...formData, password: e.target.value })}
              required
            />
          </div>
          {error && <p className="text-red-500 text-xs text-center">{error}</p>}
          <Button type="submit" className="w-full py-4 text-lg" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </Button>
        </form>

        <p className="text-center text-sm text-zinc-500">
          Don't have an account?{' '}
          <button onClick={onToggle} className="text-black font-semibold hover:underline">Create one</button>
        </p>
      </motion.div>
    </div>
  );
};

const RegisterPage = ({ onToggle }: { onToggle: () => void }) => {
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await authService.register(formData);
      localStorage.setItem('bankuser', formData.username);
      setSuccess(true);
      setTimeout(onToggle, 2000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-center px-6 bg-white">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-md w-full mx-auto space-y-8"
      >
        <div className="text-center">
          <div className="w-16 h-16 bg-zinc-100 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <Plus className="text-black w-8 h-8" />
          </div>
          <h1 className="text-3xl font-bold tracking-tight">Create account</h1>
          <p className="text-zinc-500 mt-2 text-sm">Join Way Bank today</p>
        </div>

        {success ? (
          <div className="bg-emerald-50 text-emerald-600 p-4 rounded-xl text-center">
            Registration successful! Redirecting to login...
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Username</label>
              <Input 
                placeholder="e.g. lema" 
                value={formData.username}
                onChange={e => setFormData({ ...formData, username: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Email</label>
              <Input 
                type="email"
                placeholder="lema@bank.com" 
                value={formData.email}
                onChange={e => setFormData({ ...formData, email: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Password</label>
              <Input 
                type="password" 
                placeholder="••••••••" 
                value={formData.password}
                onChange={e => setFormData({ ...formData, password: e.target.value })}
                required
              />
            </div>
            {error && <p className="text-red-500 text-xs text-center">{error}</p>}
            <Button type="submit" className="w-full py-4 text-lg" disabled={loading}>
              {loading ? 'Creating...' : 'Create Account'}
            </Button>
          </form>
        )}

        <p className="text-center text-sm text-zinc-500">
          Already have an account?{' '}
          <button onClick={onToggle} className="text-black font-semibold hover:underline">Sign in</button>
        </p>
      </motion.div>
    </div>
  );
};

const Dashboard = () => {
  const { logout } = useAuth();
  const [account, setAccount] = useState<Account | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'home' | 'transfer' | 'history' | 'profile'>('home');
  const [username, setUsername] = useState('User');
  
  const [error, setError] = useState<string | null>(null);
  const [historyFilter, setHistoryFilter] = useState<HistoryFilter>('all');
  const [historyPage, setHistoryPage] = useState(0);
  const [historyHasNext, setHistoryHasNext] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(false);
  
  // Transfer state
  const [transferData, setTransferData] = useState({ toAccountNumber: '', amount: '', description: '' });
  const [transferLoading, setTransferLoading] = useState(false);
  const [transferStatus, setTransferStatus] = useState<{ type: 'success' | 'error', message: string } | null>(null);

  const loadTransactions = async (
    accountNumber: string,
    filter: HistoryFilter = historyFilter,
    page = 0,
    append = false,
  ) => {
    setHistoryLoading(true);
    try {
      const transRes = await transactionService.getHistory(accountNumber, filter, page, 10);
      setTransactions(prev => append ? [...prev, ...transRes.data.transactions] : transRes.data.transactions);
      setHistoryPage(transRes.data.currentPage);
      setHistoryHasNext(transRes.data.hasNext);
    } catch (e) {
      console.log('History not available');
      if (!append) {
        setTransactions([]);
      }
      setHistoryHasNext(false);
    } finally {
      setHistoryLoading(false);
    }
  };

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const accRes = await accountService.getMyAccount();
      setAccount(accRes.data);
      await loadTransactions(accRes.data.accountNumber, historyFilter, 0, false);
    } catch (err: any) {
      console.error("Error fetching dashboard data", err);
      // If it's a 404, it might just mean no account created yet
      if (err.response?.status === 404) {
        setAccount(null);
      } else {
        const detail = typeof err.response?.data === 'object' 
          ? JSON.stringify(err.response.data, null, 2) 
          : (err.response?.data || err.message || 'Failed to connect to server');
        setError(detail);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const savedUser = localStorage.getItem('bankuser');
    if (savedUser) setUsername(savedUser);
    fetchData();
  }, []);

  const handleCreateAccount = async () => {
    try {
      await accountService.createAccount();
      fetchData();
    } catch (err) {
      alert("Failed to create account");
    }
  };

  const handleTransfer = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!account) return;
    
    setTransferLoading(true);
    setTransferStatus(null);
    try {
      await transactionService.transfer({
        fromAccountNumber: account.accountNumber,
        toAccountNumber: transferData.toAccountNumber,
        amount: parseFloat(transferData.amount),
        description: transferData.description
      });
      setTransferStatus({ type: 'success', message: 'Transfer successful!' });
      setTransferData({ toAccountNumber: '', amount: '', description: '' });
      fetchData();
    } catch (err: any) {
      setTransferStatus({ type: 'error', message: err.response?.data?.message || 'Transfer failed' });
    } finally {
      setTransferLoading(false);
    }
  };

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center bg-zinc-50">
      <div className="w-8 h-8 border-4 border-black border-t-transparent rounded-full animate-spin" />
    </div>
  );

  return (
    <div className="min-h-screen bg-zinc-50 pb-24">
      {/* Header */}
      <header className="bg-white px-6 py-4 flex items-center justify-between sticky top-0 z-10 border-b border-zinc-100">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-black rounded-lg flex items-center justify-center">
            <Wallet className="text-white w-5 h-5" />
          </div>
          <span className="font-bold text-lg">Way</span>
        </div>
        <div className="flex items-center gap-4">
          <button className="p-2 text-zinc-400 hover:text-black transition-colors">
            <Bell className="w-6 h-6" />
          </button>
          <button onClick={logout} className="p-2 text-zinc-400 hover:text-red-500 transition-colors">
            <LogOut className="w-6 h-6" />
          </button>
        </div>
      </header>

      <main className="px-6 py-8 max-w-lg mx-auto space-y-8">
        {error && (
          <div className="bg-red-50 border border-red-100 p-4 rounded-2xl text-red-600 text-sm flex flex-col items-center gap-3">
            <p className="font-medium">Connection Error</p>
            <p className="text-xs opacity-80">{error}</p>
            <Button variant="outline" className="py-2 text-xs" onClick={fetchData}>Retry Connection</Button>
          </div>
        )}
        <AnimatePresence mode="wait">
          {activeTab === 'home' && (
            <motion.div 
              key="home"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 20 }}
              className="space-y-6"
            >
              {/* Profile Greeting */}
              <div>
                <h2 className="text-2xl font-bold">Hello, {username || 'User'}</h2>
                <p className="text-zinc-500 text-sm">Welcome back to your wallet</p>
              </div>

              {/* Account Card */}
              {account ? (
                <Card className="bg-black text-white relative overflow-hidden">
                  <div className="absolute top-0 right-0 p-4 opacity-10">
                    <Wallet className="w-32 h-32" />
                  </div>
                  <div className="relative z-10 space-y-8">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-zinc-400 text-xs font-semibold uppercase tracking-widest">Total Balance</p>
                        <h3 className="text-4xl font-bold mt-1">${account.balance?.toLocaleString() || '0'}</h3>
                      </div>
                      <CreditCard className="text-zinc-400" />
                    </div>
                    <div className="flex justify-between items-end">
                      <div>
                        <p className="text-zinc-400 text-[10px] font-semibold uppercase tracking-widest">Account Number</p>
                        <p className="font-mono text-sm tracking-widest">{account.accountNumber}</p>
                      </div>
                      <div className="text-right">
                        <p className="text-zinc-400 text-[10px] font-semibold uppercase tracking-widest">Type</p>
                        <p className="text-sm font-medium">{account.accountType}</p>
                      </div>
                    </div>
                  </div>
                </Card>
              ) : (
                <Card className="flex flex-col items-center justify-center py-12 border-dashed border-2">
                  <Plus className="text-zinc-300 w-12 h-12 mb-4" />
                  <p className="text-zinc-500 text-sm mb-4">No account found</p>
                  <Button onClick={handleCreateAccount}>Create Savings Account</Button>
                </Card>
              )}

              {/* Quick Actions */}
              <div className="grid grid-cols-4 gap-4">
                {[
                  { icon: Send, label: 'Send', tab: 'transfer' },
                  { icon: Plus, label: 'Add', action: () => {} },
                  { icon: History, label: 'History', tab: 'history' },
                  { icon: Search, label: 'Search', action: () => {} }
                ].map((action, i) => (
                  <button 
                    key={i}
                    onClick={() => action.tab ? setActiveTab(action.tab as any) : action.action?.()}
                    className="flex flex-col items-center gap-2 group"
                  >
                    <div className="w-14 h-14 bg-white rounded-2xl flex items-center justify-center shadow-sm border border-zinc-100 group-hover:bg-zinc-50 transition-colors">
                      <action.icon className="w-6 h-6" />
                    </div>
                    <span className="text-xs font-medium text-zinc-500">{action.label}</span>
                  </button>
                ))}
              </div>

              {/* Recent Transactions */}
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="font-bold text-lg">Recent Activity</h3>
                  <button onClick={() => setActiveTab('history')} className="text-sm font-semibold text-zinc-400 hover:text-black">See all</button>
                </div>
                <div className="space-y-3">
                  {Array.isArray(transactions) && transactions.length > 0 ? transactions.slice(0, 5).map((t, i) => (
                    <div key={i} className="bg-white p-4 rounded-2xl flex items-center justify-between border border-zinc-100">
                      <div className="flex items-center gap-4">
                        <div className={cn(
                          "w-10 h-10 rounded-xl flex items-center justify-center",
                          t.transactionType === 'DEBIT' ? "bg-red-50 text-red-600" : "bg-emerald-50 text-emerald-600"
                        )}>
                          <ArrowRightLeft className="w-5 h-5" />
                        </div>
                        <div>
                          <p className="font-semibold text-sm">{t.description || 'Transfer'}</p>
                          <p className="text-zinc-400 text-[10px]">{t.timestamp ? new Date(t.timestamp).toLocaleDateString() : 'N/A'}</p>
                        </div>
                      </div>
                      <p className={cn(
                        "font-bold",
                        t.fromAccountNumber === account?.accountNumber ? "text-red-500" : "text-emerald-500"
                      )}>
                        {t.fromAccountNumber === account?.accountNumber ? '-' : '+'}${t.amount}
                      </p>
                    </div>
                  )) : (
                    <div className="text-center py-8 text-zinc-400 text-sm italic">
                      No recent transactions
                    </div>
                  )}
                </div>
              </div>
            </motion.div>
          )}

          {activeTab === 'transfer' && (
            <motion.div 
              key="transfer"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              className="space-y-6"
            >
              <div className="flex items-center gap-4 mb-8">
                <button onClick={() => setActiveTab('home')} className="p-2 hover:bg-zinc-100 rounded-full">
                  <ChevronRight className="w-6 h-6 rotate-180" />
                </button>
                <h2 className="text-2xl font-bold">Send Money</h2>
              </div>

              <form onSubmit={handleTransfer} className="space-y-6">
                <Card className="space-y-4">
                  <div className="space-y-2">
                    <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Recipient Account Number</label>
                    <Input 
                      placeholder="e.g. ACC123456" 
                      value={transferData.toAccountNumber}
                      onChange={e => setTransferData({ ...transferData, toAccountNumber: e.target.value })}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Amount ($)</label>
                    <Input 
                      type="number"
                      step="0.01"
                      placeholder="0.00" 
                      value={transferData.amount}
                      onChange={e => setTransferData({ ...transferData, amount: e.target.value })}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <label className="text-xs font-semibold uppercase tracking-wider text-zinc-400 ml-1">Description (Optional)</label>
                    <Input 
                      placeholder="What's this for?" 
                      value={transferData.description}
                      onChange={e => setTransferData({ ...transferData, description: e.target.value })}
                    />
                  </div>
                </Card>

                {transferStatus && (
                  <div className={cn(
                    "p-4 rounded-xl text-center text-sm",
                    transferStatus.type === 'success' ? "bg-emerald-50 text-emerald-600" : "bg-red-50 text-red-600"
                  )}>
                    {transferStatus.message}
                  </div>
                )}

                <Button type="submit" className="w-full py-4 text-lg" disabled={transferLoading}>
                  {transferLoading ? 'Processing...' : 'Confirm Transfer'}
                </Button>
              </form>
            </motion.div>
          )}

          {activeTab === 'history' && (
            <motion.div 
              key="history"
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="space-y-6"
            >
              <div className="flex items-center gap-4 mb-8">
                <button onClick={() => setActiveTab('home')} className="p-2 hover:bg-zinc-100 rounded-full">
                  <ChevronRight className="w-6 h-6 rotate-180" />
                </button>
                <h2 className="text-2xl font-bold">Transaction History</h2>
              </div>

              <div className="flex gap-2">
                {(['all', 'debited', 'credited'] as HistoryFilter[]).map((filter) => (
                  <Button
                    key={filter}
                    variant={historyFilter === filter ? 'primary' : 'outline'}
                    className="capitalize"
                    onClick={async () => {
                      if (!account) return;
                      setHistoryFilter(filter);
                      await loadTransactions(account.accountNumber, filter, 0, false);
                    }}
                  >
                    {filter}
                  </Button>
                ))}
              </div>

              <div className="space-y-3">
                {Array.isArray(transactions) && transactions.length > 0 ? transactions.map((t, i) => (
                  <div key={i} className="bg-white p-4 rounded-2xl flex items-center justify-between border border-zinc-100">
                    <div className="flex items-center gap-4">
                      <div className={cn(
                        "w-10 h-10 rounded-xl flex items-center justify-center",
                        t.transactionType === 'DEBIT' ? "bg-red-50 text-red-600" : "bg-emerald-50 text-emerald-600"
                      )}>
                        <ArrowRightLeft className="w-5 h-5" />
                      </div>
                      <div>
                        <p className="font-semibold text-sm">{t.description || 'Transfer'}</p>
                        <p className="text-zinc-400 text-[10px]">{t.timestamp ? new Date(t.timestamp).toLocaleString() : 'N/A'}</p>
                        <p className="text-[9px] text-zinc-300 font-mono">ID: {t.id}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={cn(
                        "font-bold",
                        t.fromAccountNumber === account?.accountNumber ? "text-red-500" : "text-emerald-500"
                      )}>
                        {t.fromAccountNumber === account?.accountNumber ? '-' : '+'}${t.amount}
                      </p>
                    </div>
                  </div>
                )) : (
                  <div className="text-center py-20">
                    <History className="w-16 h-16 text-zinc-200 mx-auto mb-4" />
                    <p className="text-zinc-500">No transactions yet</p>
                  </div>
                )}
              </div>

              {account && historyHasNext && (
                <Button
                  variant="outline"
                  className="w-full py-3"
                  onClick={() => loadTransactions(account.accountNumber, historyFilter, historyPage + 1, true)}
                  disabled={historyLoading}
                >
                  {historyLoading ? 'Loading...' : 'Load More'}
                </Button>
              )}
            </motion.div>
          )}

          {activeTab === 'profile' && (
            <motion.div 
              key="profile"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="space-y-6"
            >
              <h2 className="text-2xl font-bold mb-8">Profile Settings</h2>
              
              <Card className="space-y-6">
                <div className="flex items-center gap-4">
                  <div className="w-16 h-16 bg-zinc-100 rounded-full flex items-center justify-center">
                    <UserIcon className="w-8 h-8 text-zinc-400" />
                  </div>
                  <div>
                    <h3 className="font-bold text-lg">{username}</h3>
                    <p className="text-zinc-500 text-sm">Logged in user</p>
                  </div>
                </div>

                <div className="pt-6 border-t border-zinc-100 space-y-4">
                  {[
                    { icon: UserIcon, label: 'Personal Information' },
                    { icon: CreditCard, label: 'Linked Cards' },
                    { icon: Bell, label: 'Notifications' },
                    { icon: Wallet, label: 'Security' }
                  ].map((item, i) => (
                    <button key={i} className="w-full flex items-center justify-between p-2 hover:bg-zinc-50 rounded-xl transition-colors">
                      <div className="flex items-center gap-3">
                        <item.icon className="w-5 h-5 text-zinc-400" />
                        <span className="font-medium">{item.label}</span>
                      </div>
                      <ChevronRight className="w-4 h-4 text-zinc-300" />
                    </button>
                  ))}
                </div>
              </Card>

              <Button variant="outline" className="w-full py-4 text-red-500 border-red-100 hover:bg-red-50" onClick={logout}>
                Sign Out
              </Button>
            </motion.div>
          )}
        </AnimatePresence>
      </main>

      {/* Bottom Navigation */}
      <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-zinc-100 px-6 py-4 flex justify-between items-center z-20 max-w-lg mx-auto rounded-t-[32px] shadow-[0_-8px_30px_rgb(0,0,0,0.04)]">
        {[
          { icon: Wallet, id: 'home' },
          { icon: Send, id: 'transfer' },
          { icon: History, id: 'history' },
          { icon: UserIcon, id: 'profile' }
        ].map((item) => (
          <button 
            key={item.id}
            onClick={() => setActiveTab(item.id as any)}
            className={cn(
              "p-3 rounded-2xl transition-all relative",
              activeTab === item.id ? "bg-black text-white" : "text-zinc-400 hover:text-black"
            )}
          >
            <item.icon className="w-6 h-6" />
            {activeTab === item.id && (
              <motion.div 
                layoutId="nav-pill"
                className="absolute inset-0 bg-black rounded-2xl -z-10"
                transition={{ type: 'spring', bounce: 0.2, duration: 0.6 }}
              />
            )}
          </button>
        ))}
      </nav>
    </div>
  );
};

const AppContent = () => {
  const { isAuthenticated } = useAuth();
  const [showRegister, setShowRegister] = useState(false);

  if (!isAuthenticated) {
    return showRegister ? (
      <RegisterPage onToggle={() => setShowRegister(false)} />
    ) : (
      <LoginPage onToggle={() => setShowRegister(true)} />
    );
  }

  return <Dashboard />;
};

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
