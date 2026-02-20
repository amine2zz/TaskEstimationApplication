import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link, useNavigate } from 'react-router-dom';
import {
  BarChart3,
  LayoutDashboard,
  Settings,
  User as UserIcon,
  LogOut,
  ShieldCheck,
  CreditCard,
  TrendingUp,
  ArrowRight,
  Database,
  Search,
  Plus,
  Sun,
  Moon,
  Trash2,
  Edit3,
  X,
  History,
  Activity,
  MessageCircle,
  Send,
  Loader2
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import axios from 'axios';
import './App.css';

const API_BASE = 'http://localhost:8081/api';
const AI_BASE_URL = 'http://localhost:8005';

// --- SMART AI CHAT COMPONENT ---
const SmartChat = ({ user, transactions }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { role: 'ai', content: `Hello ${user.name}! I'm Proxym AI. I've analyzed your ${user.balance.toLocaleString()} balance and recent activity. How can I help you?` }
  ]);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const chatBodyRef = React.useRef(null);

  // Auto-scroll to bottom whenever messages change
  useEffect(() => {
    if (chatBodyRef.current) {
      setTimeout(() => {
        chatBodyRef.current.scrollTo({
          top: chatBodyRef.current.scrollHeight,
          behavior: 'smooth'
        });
      }, 100);
    }
  }, [messages, isTyping, isOpen]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim()) return;

    const userMsg = input;
    setInput('');
    setMessages(prev => [...prev, { role: 'user', content: userMsg }]);
    setIsTyping(true);

    try {
      const context = {
        name: user.name,
        balance: user.balance,
        salary: user.monthlyIncome,
        age: user.age,
        risk_profile: user.riskProfile,
        num_products: transactions.length,
        transactions: transactions.slice(0, 5)
      };

      const res = await axios.post(`${AI_BASE_URL}/chat`, {
        message: userMsg,
        user_context: context
      });

      setMessages(prev => [...prev, { role: 'ai', content: res.data.response }]);
    } catch (err) {
      setMessages(prev => [...prev, { role: 'ai', content: "I'm having trouble connecting to my brain right now. Please try again later!" }]);
    } finally {
      setIsTyping(false);
    }
  };

  return (
    <div className="smart-chat-wrapper">
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 10, scale: 0.98 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 10, scale: 0.98 }}
            className="chat-window stat-item"
          >
            <div className="chat-header">
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <div className="status-dot"></div>
                <span style={{ fontWeight: '600', fontSize: '0.85rem' }}>AI Assistant</span>
              </div>
              <button className="close-chat-btn" onClick={() => setIsOpen(false)}><X size={14} /></button>
            </div>

            <div className="chat-body" ref={chatBodyRef}>
              {messages.map((msg, i) => (
                <div key={i} className={`chat-bubble ${msg.role}`}>
                  {msg.content}
                </div>
              ))}
              {isTyping && (
                <div className="chat-bubble ai typing">
                  <Loader2 className="spin" size={12} />
                </div>
              )}
            </div>

            <form className="chat-footer" onSubmit={handleSend}>
              <input
                placeholder="Ask anything..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
              />
              <button type="submit"><Send size={14} /></button>
            </form>
          </motion.div>
        )}
      </AnimatePresence>

      <button
        className="chat-toggle-btn"
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? <X size={20} /> : <MessageCircle size={20} />}
      </button>
    </div>
  );
};

// --- AUTH HOOK ---
const useAuth = () => {
  const [user, setUser] = useState(JSON.parse(localStorage.getItem('user')) || null);
  const login = (userData) => {
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
  };
  const logout = () => {
    localStorage.removeItem('user');
    setUser(null);
  };
  return { user, login, logout, isAdmin: user?.role === 'ADMIN' };
};

// --- AUTH PAGE (LOGIN & SIGNUP) ---
const AuthPage = ({ onLogin }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [age, setAge] = useState(25);
  const [income, setIncome] = useState(3000);
  const [goals, setGoals] = useState('Savings');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (isLogin) {
        const res = await axios.post(`${API_BASE}/auth/login`, { email, password });
        onLogin(res.data);
        navigate(res.data.role === 'ADMIN' ? '/admin' : '/dashboard');
      } else {
        await axios.post(`${API_BASE}/auth/signup`, {
          name,
          email,
          password,
          age: parseInt(age),
          monthlyIncome: parseFloat(income),
          financialGoals: goals
        });
        setIsLogin(true);
        alert('Signup successful! Please login.');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="login-card stat-item fade-in">
        <div className="logo-section">
          <div className="logo-blob">
            <Lock size={20} />
          </div>
          <h2 style={{ fontSize: '1.2rem', fontWeight: '700' }}>Proxym Intelligence</h2>
          <p className="subtitle" style={{ fontSize: '0.85rem' }}>Secure portal access</p>
        </div>

        <div className="auth-toggle">
          <button className={isLogin ? 'active' : ''} onClick={() => setIsLogin(true)}>Sign In</button>
          <button className={!isLogin ? 'active' : ''} onClick={() => setIsLogin(false)}>Sign Up</button>
        </div>

        <form onSubmit={handleSubmit}>
          {!isLogin && (
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
              <input className="input-field" placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} required />
              <input className="input-field" type="number" placeholder="Age" value={age} onChange={(e) => setAge(e.target.value)} required />
              <input className="input-field" type="number" placeholder="Income" value={income} onChange={(e) => setIncome(e.target.value)} required />
              <select className="input-field" value={goals} onChange={e => setGoals(e.target.value)}>
                <option value="Savings">Savings</option>
                <option value="Investment">Investment</option>
                <option value="Loan">Loan</option>
              </select>
            </div>
          )}
          <input className="input-field" type="email" placeholder="Email Address" value={email} onChange={(e) => setEmail(e.target.value)} required />
          <input className="input-field" type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          {error && <p className="auth-error">{error}</p>}
          <button type="submit" className="btn-primary" style={{ width: '100%', padding: '10px' }} disabled={loading}>
            {loading ? 'Authenticating...' : (isLogin ? 'Sign In' : 'Create Account')}
          </button>
        </form>

        {isLogin && (
          <p style={{ marginTop: '16px', fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
            Admin: admin@proxym.com / admin
          </p>
        )}
      </div>
    </div>
  );
};

// --- USER DASHBOARD ---
const Dashboard = ({ user, onLogout, theme, setTheme }) => {
  const [recs, setRecs] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddTx, setShowAddTx] = useState(false);
  const [txData, setTxData] = useState({ amount: '', category: 'Food', description: '' });
  const [activeView, setActiveView] = useState('dashboard');
  const [currentUser, setCurrentUser] = useState(user);

  const fetchData = async () => {
    try {
      const [recRes, txRes, userRes] = await Promise.all([
        axios.get(`${API_BASE}/recommendations/${user.id}`),
        axios.get(`${API_BASE}/transactions/user/${user.id}`),
        axios.get(`${API_BASE}/users/${user.id}`)
      ]);
      setRecs(recRes.data);
      setTransactions(txRes.data.sort((a, b) => b.id - a.id));
      setCurrentUser(userRes.data);
    } catch (err) {
      console.error('Data fetch error', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [user.id]);

  const handleAddTx = async (e) => {
    e.preventDefault();
    try {
      const amount = parseFloat(txData.amount);
      const res = await axios.post(`${API_BASE}/transactions`, {
        ...txData,
        amount: amount,
        user: { id: user.id }
      });

      // Update balance locally and on server
      const newBalance = (currentUser.balance || 0) - amount;
      await axios.put(`${API_BASE}/users/${user.id}`, {
        ...currentUser,
        balance: newBalance
      });

      setShowAddTx(false);
      setTxData({ amount: '', category: 'Food', description: '' });
      fetchData();
    } catch (err) {
      alert('Failed to add transaction');
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`${API_BASE}/users/${user.id}`, currentUser);
      alert('Profile updated!');
      fetchData();
    } catch (err) {
      alert('Update failed');
    }
  };

  return (
    <div className="main-layout">
      <aside className="sidebar glass-card">
        <div className="sidebar-header">
          <div className="logo-dot"></div>
          <span>Proxym AI</span>
        </div>
        <nav>
          <button className={`nav-link ${activeView === 'dashboard' ? 'active' : ''}`} onClick={() => setActiveView('dashboard')}>
            <LayoutDashboard size={20} /> Dashboard
          </button>
          <button className={`nav-link ${activeView === 'transactions' ? 'active' : ''}`} onClick={() => setActiveView('transactions')}>
            <History size={20} /> Transactions
          </button>
          <button className={`nav-link ${activeView === 'insights' ? 'active' : ''}`} onClick={() => setActiveView('insights')}>
            <TrendingUp size={20} /> Insights
          </button>
          <button className={`nav-link ${activeView === 'settings' ? 'active' : ''}`} onClick={() => setActiveView('settings')}>
            <Settings size={20} /> Settings
          </button>
        </nav>
        <button className="logout-btn" onClick={onLogout}><LogOut size={18} /> Logout</button>
      </aside>

      <main className="content-area">
        <header className="content-header">
          <div>
            <h2>{activeView === 'dashboard' ? `Summary` : activeView.charAt(0).toUpperCase() + activeView.slice(1)}</h2>
            <p className="subtitle">{activeView === 'dashboard' ? `Welcome back, ${currentUser.name}` : `Manage your ${activeView}`}</p>
          </div>
          <div className="user-pill">
            <UserIcon size={14} />
            <span>{currentUser.email}</span>
          </div>
        </header>

        {activeView === 'dashboard' && (
          <>
            <div className="stats-grid">
              <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} className="glass-card bank-card primary">
                <div className="bank-card-chip"></div>
                <div>
                  <span className="label">Total Balance</span>
                  <div className="bank-card-balance">${currentUser.balance?.toLocaleString()}</div>
                  <div className="bank-card-number">**** **** **** 4242</div>
                </div>
                <span className="trend positive">Active Account</span>
              </motion.div>

              <div className="glass-card stat-item">
                <span className="label">Monthly Income</span>
                <h3>${currentUser.monthlyIncome?.toLocaleString()}</h3>
                <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginTop: '10px' }}>
                  <span className="trend positive" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <TrendingUp size={14} /> +8.2%
                  </span>
                  <span style={{ fontSize: '0.75rem', opacity: 0.6 }}>vs last month</span>
                </div>
              </div>

              <div className="glass-card stat-item">
                <span className="label">Financial Health</span>
                <div style={{ position: 'relative', marginTop: '15px', height: '60px' }}>
                  <div style={{ fontSize: '1.8rem', fontWeight: '800' }}>85%</div>
                  <div style={{ height: '6px', background: 'var(--glass-border)', borderRadius: '10px', marginTop: '8px', overflow: 'hidden' }}>
                    <motion.div
                      initial={{ width: 0 }}
                      animate={{ width: '85%' }}
                      style={{ height: '100%', background: 'var(--primary-color)', borderRadius: '10px' }}
                    />
                  </div>
                  <span style={{ fontSize: '0.75rem', opacity: 0.6 }}>{currentUser.riskProfile} Risk Profile</span>
                </div>
              </div>
            </div>

            <div className="dashboard-grid">
              <section className="recommendations-section">
                <div className="section-header">
                  <h3>AI Recommendations</h3>
                </div>
                {loading ? (
                  <div className="loader">Analyzing...</div>
                ) : (
                  <div className="recs-grid">
                    {recs.map(p => (
                      <div key={p.id} className="product-card">
                        <span className={`badge ${p.type.toLowerCase()}`}>{p.type}</span>
                        <h4>{p.name}</h4>
                        <p>{p.description}</p>
                        <div className="card-footer" style={{ marginTop: '12px' }}>
                          <span className="rate-value" style={{ fontSize: '1rem' }}>{p.interestRate}% <small>APY</small></span>
                          <button className="btn-icon" style={{ padding: '6px' }}><ArrowRight size={16} /></button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </section>

              <section className="transactions-section">
                <div className="section-header">
                  <h3>Activity</h3>
                  <button className="btn-small" onClick={() => setShowAddTx(true)}><Plus size={14} /></button>
                </div>
                <div className="tx-list">
                  {transactions.slice(0, 5).map((tx, index) => (
                    <div key={tx.id} className="tx-item" style={{ '--i': index }}>
                      <div className="tx-icon"><CreditCard size={16} /></div>
                      <div className="tx-info">
                        <span className="tx-desc">{tx.description}</span>
                        <span className="tx-cat">{tx.category}</span>
                      </div>
                      <span className="tx-amount" style={{ color: tx.category === 'Investment' ? 'var(--primary-color)' : 'inherit' }}>
                        ${tx.amount}
                      </span>
                    </div>
                  ))}
                </div>
              </section>
            </div>
          </>
        )}

        {activeView === 'transactions' && (
          <div className="glass-card" style={{ padding: '24px' }}>
            <div className="section-header">
              <h3>Full Transaction History</h3>
              <button className="btn-primary" onClick={() => setShowAddTx(true)}><Plus size={16} /> Add New</button>
            </div>
            <table className="admin-table">
              <thead>
                <tr><th>Date</th><th>Description</th><th>Category</th><th>Amount</th></tr>
              </thead>
              <tbody>
                {transactions.map(tx => (
                  <tr key={tx.id}>
                    <td>{new Date(tx.date).toLocaleDateString()}</td>
                    <td>{tx.description}</td>
                    <td><span className="badge" style={{ background: 'var(--glass)' }}>{tx.category}</span></td>
                    <td style={{ fontWeight: 'bold' }}>${tx.amount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {activeView === 'insights' && (
          <div className="stats-grid">
            <div className="glass-card" style={{ padding: '24px', gridColumn: 'span 2' }}>
              <h3>Spending by Category</h3>
              <div style={{ marginTop: '20px', display: 'flex', flexDirection: 'column', gap: '15px' }}>
                {['Food', 'Rent', 'Investment', 'Entertainment'].map(cat => {
                  const items = transactions.filter(t => t.category === cat);
                  const total = items.reduce((sum, t) => sum + t.amount, 0);
                  const max = Math.max(...transactions.map(t => t.amount), 1000); // just for scale
                  return (
                    <div key={cat}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                        <span>{cat}</span>
                        <span>${total}</span>
                      </div>
                      <div style={{ height: '8px', background: 'var(--glass)', borderRadius: '4px', overflow: 'hidden' }}>
                        <div style={{ height: '100%', width: `${Math.min((total / 2000) * 100, 100)}%`, background: 'var(--primary)' }}></div>
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
            <div className="glass-card" style={{ padding: '24px' }}>
              <h3>Health Score</h3>
              <div style={{ height: '200px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <div style={{ width: '120px', height: '120px', borderRadius: '50%', border: '8px solid var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2rem', fontWeight: 'bold' }}>
                  85
                </div>
              </div>
              <p style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>You saved 12% more than last month!</p>
            </div>
          </div>
        )}

        {activeView === 'settings' && (
          <div className="fade-in" style={{ maxWidth: '900px' }}>
            <div className="stat-item" style={{ marginBottom: '24px' }}>
              <h3 style={{ fontSize: '1.2rem', marginBottom: '16px' }}>Interface Theme</h3>
              <div className="theme-grid">
                {[
                  { id: 'dark', name: 'Charcoal', color: 'dark' },
                  { id: 'light', name: 'Daylight', color: 'light' },
                  { id: 'gold', name: 'Executive', color: 'gold' },
                  { id: 'ocean', name: 'Cyber', color: 'ocean' },
                  { id: 'emerald', name: 'Forest', color: 'emerald' }
                ].map(t => (
                  <div
                    key={t.id}
                    className={`theme-option ${theme === t.id ? 'active' : ''}`}
                    onClick={() => setTheme(t.id)}
                    style={{ padding: '8px 12px' }}
                  >
                    <div className={`theme-circle ${t.color}`} style={{ width: '24px', height: '24px' }}></div>
                    <span className="theme-name" style={{ fontSize: '0.75rem' }}>{t.name}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="stat-item">
              <h3 style={{ fontSize: '1.2rem', marginBottom: '20px' }}>Account Settings</h3>
              <form onSubmit={handleUpdateProfile} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="input-group">
                  <label style={{ display: 'block', marginBottom: '6px', fontSize: '0.8rem', fontWeight: '500', opacity: 0.7 }}>Full Name</label>
                  <input className="input-field" value={currentUser.name} onChange={e => setCurrentUser({ ...currentUser, name: e.target.value })} />
                </div>
                <div className="input-group">
                  <label style={{ display: 'block', marginBottom: '6px', fontSize: '0.8rem', fontWeight: '500', opacity: 0.7 }}>Email Address</label>
                  <input className="input-field" value={currentUser.email} disabled />
                </div>
                <div className="input-group">
                  <label style={{ display: 'block', marginBottom: '6px', fontSize: '0.8rem', fontWeight: '500', opacity: 0.7 }}>Monthly Income ($)</label>
                  <input className="input-field" type="number" value={currentUser.monthlyIncome} onChange={e => setCurrentUser({ ...currentUser, monthlyIncome: e.target.value })} />
                </div>
                <div className="input-group">
                  <label style={{ display: 'block', marginBottom: '6px', fontSize: '0.8rem', fontWeight: '500', opacity: 0.7 }}>Risk Profile</label>
                  <select className="input-field" value={currentUser.riskProfile} onChange={e => setCurrentUser({ ...currentUser, riskProfile: e.target.value })}>
                    <option value="Low">Low Risk</option>
                    <option value="Medium">Medium Risk</option>
                    <option value="High">High Risk</option>
                  </select>
                </div>
                <div style={{ gridColumn: 'span 2', marginTop: '8px' }}>
                  <button type="submit" className="btn-primary" style={{ padding: '8px 24px', fontSize: '0.9rem' }}>Update Profile</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>

      {/* ADD TRANSACTION MODAL */}
      <AnimatePresence>
        {showAddTx && (
          <div className="modal-overlay">
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 20 }} className="glass-card modal-content" style={{ width: '400px' }}>
              <h3>New Transaction</h3>
              <form onSubmit={handleAddTx} style={{ marginTop: '20px' }}>
                <input className="input-field" type="number" placeholder="Amount" value={txData.amount} onChange={e => setTxData({ ...txData, amount: e.target.value })} required />
                <select className="input-field" value={txData.category} onChange={e => setTxData({ ...txData, category: e.target.value })}>
                  <option value="Food">Food</option>
                  <option value="Rent">Rent</option>
                  <option value="Investment">Investment</option>
                  <option value="Subscription">Subscription</option>
                  <option value="Entertainment">Entertainment</option>
                </select>
                <input className="input-field" placeholder="Description" value={txData.description} onChange={e => setTxData({ ...txData, description: e.target.value })} required />
                <div className="modal-footer">
                  <button type="button" className="btn-secondary" onClick={() => setShowAddTx(false)}>Cancel</button>
                  <button type="submit" className="btn-primary">Add</button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
      <SmartChat user={currentUser} transactions={transactions} />
    </div>
  );
};

// --- ADMIN PANEL ---
const AdminPanel = ({ onLogout, theme, setTheme }) => {
  const [activeTable, setActiveTable] = useState('products');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({});

  const fetchData = async () => {
    setLoading(true);
    const endpoint = activeTable === 'products' ? 'products' : activeTable === 'users' ? 'users' : 'transactions';
    try {
      const res = await axios.get(`${API_BASE}/${endpoint}`);
      setData(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const [searchQuery, setSearchQuery] = useState('');

  const filteredData = data.filter(item => {
    const searchStr = searchQuery.toLowerCase();
    return (
      (item.name?.toLowerCase().includes(searchStr)) ||
      (item.email?.toLowerCase().includes(searchStr)) ||
      (item.description?.toLowerCase().includes(searchStr)) ||
      (item.category?.toLowerCase().includes(searchStr)) ||
      (item.id?.toString().includes(searchStr))
    );
  });

  useEffect(() => {
    fetchData();
  }, [activeTable]);

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this entry? This action cannot be undone.')) return;
    const endpoint = activeTable === 'products' ? 'products' : activeTable === 'users' ? 'users' : 'transactions';
    try {
      await axios.delete(`${API_BASE}/${endpoint}/${id}`);
      fetchData();
    } catch (err) {
      alert('Delete failed: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleEdit = (item) => {
    setEditingItem(item);
    setFormData(item);
    setShowModal(true);
  };

  const handleAddNew = () => {
    setEditingItem(null);
    if (activeTable === 'products') {
      setFormData({ name: '', description: '', type: 'Savings', interestRate: 5, minimumEntry: 100 });
    } else if (activeTable === 'users') {
      setFormData({ name: '', email: '', password: 'password123', role: 'USER', balance: 0, age: 25, riskProfile: 'Medium', financialGoals: 'Savings' });
    } else {
      setFormData({ amount: 0, category: 'Food', description: '', date: new Date().toISOString() });
    }
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const endpoint = activeTable === 'products' ? 'products' : activeTable === 'users' ? 'users' : 'transactions';
    try {
      let payload = { ...formData };
      if (!editingItem && activeTable === 'logs') {
        // Find first user if none selected or just use a default for admin-created tx
        payload.user = { id: 1 };
      }

      if (editingItem) {
        await axios.put(`${API_BASE}/${endpoint}/${editingItem.id}`, payload);
      } else {
        await axios.post(`${API_BASE}/${endpoint}`, payload);
      }
      setShowModal(false);
      fetchData();
    } catch (err) {
      alert('Save failed: ' + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div className="admin-layout">
      <nav className="admin-sidebar glass-card">
        <div className="admin-logo"><ShieldCheck size={24} color="var(--primary)" /> <span>Admin</span></div>
        <div className="nav-items">
          <button onClick={() => setActiveTable('products')} className={`nav-link ${activeTable === 'products' ? 'active' : ''}`}><Database size={18} /> Products</button>
          <button onClick={() => setActiveTable('users')} className={`nav-link ${activeTable === 'users' ? 'active' : ''}`}><UserIcon size={18} /> Users</button>
          <button onClick={() => setActiveTable('logs')} className={`nav-link ${activeTable === 'logs' ? 'active' : ''}`}><BarChart3 size={18} /> Logs</button>
        </div>
        <div style={{ marginTop: 'auto' }}>
          <button className="logout-btn" onClick={onLogout}><LogOut size={18} /> Logout</button>
        </div>
      </nav>

      <main className="admin-main">
        <header className="admin-header">
          <div className="search-bar glass-card">
            <Search size={18} />
            <input
              placeholder={`Search ${activeTable}...`}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
            <h3>Management: {activeTable === 'logs' ? 'TRANSACTIONS' : activeTable.toUpperCase()}</h3>
            <button className="btn-primary" onClick={handleAddNew} style={{ padding: '8px 16px', fontSize: '0.85rem' }}>
              <Plus size={16} /> Add {activeTable === 'products' ? 'Product' : activeTable === 'users' ? 'User' : 'Transaction'}
            </button>
          </div>
        </header>
        <div className="table-container glass-card">
          <table className="admin-table">
            <thead>
              <tr><th>ID</th><th>Primary Field</th><th>Details</th><th>Value</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan="5" className="loader">Loading...</td></tr>
              ) : filteredData.length === 0 ? (
                <tr><td colSpan="5" style={{ textAlign: 'center', padding: '40px', opacity: 0.5 }}>No matching records found.</td></tr>
              ) : filteredData.map(item => (
                <tr key={item.id}>
                  <td>#{item.id}</td>
                  <td>{item.name || item.category || 'N/A'}</td>
                  <td>{item.type || item.email || item.description || 'No details'}</td>
                  <td>
                    {item.interestRate !== undefined ? `${item.interestRate}%` :
                      item.amount ? `$${item.amount}` :
                        item.riskProfile || 'N/A'}
                  </td>
                  <td className="actions-cell">
                    <button className="edit-btn" onClick={() => handleEdit(item)}><Edit3 size={16} /></button>
                    <button className="delete-btn" onClick={() => handleDelete(item.id)}><Trash2 size={16} /></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>

      {/* ADMIN EDIT/ADD MODAL */}
      <AnimatePresence>
        {showModal && (
          <div className="modal-overlay">
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 20 }} className="glass-card modal-content" style={{ width: '450px' }}>
              <h3>{editingItem ? 'Edit' : 'Add New'} {activeTable === 'logs' ? 'Transaction' : activeTable.slice(0, -1)}</h3>
              <form onSubmit={handleSave} style={{ marginTop: '20px' }}>
                {activeTable === 'products' && (
                  <>
                    <input className="input-field" placeholder="Product Name" value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} required />
                    <textarea className="input-field" placeholder="Description" style={{ minHeight: '100px', resize: 'vertical' }} value={formData.description || ''} onChange={e => setFormData({ ...formData, description: e.target.value })} required />
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                      <select className="input-field" value={formData.type || 'Savings'} onChange={e => setFormData({ ...formData, type: e.target.value })}>
                        <option value="Savings">Savings</option>
                        <option value="Investment">Investment</option>
                        <option value="Loan">Loan</option>
                      </select>
                      <input className="input-field" type="number" step="0.1" placeholder="Interest Rate %" value={formData.interestRate || ''} onChange={e => setFormData({ ...formData, interestRate: parseFloat(e.target.value) })} required />
                      <input className="input-field" type="number" placeholder="Min. Entry ($)" value={formData.minimumEntry || ''} onChange={e => setFormData({ ...formData, minimumEntry: parseFloat(e.target.value) })} required />
                    </div>
                  </>
                )}

                {activeTable === 'users' && (
                  <>
                    <input className="input-field" placeholder="Full Name" value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} required />
                    <input className="input-field" type="email" placeholder="Email" value={formData.email || ''} onChange={e => setFormData({ ...formData, email: e.target.value })} required />
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                      <input className="input-field" type="number" placeholder="Balance ($)" value={formData.balance || 0} onChange={e => setFormData({ ...formData, balance: parseFloat(e.target.value) })} />
                      <input className="input-field" type="number" placeholder="Income ($)" value={formData.monthlyIncome || 0} onChange={e => setFormData({ ...formData, monthlyIncome: parseFloat(e.target.value) })} />
                      <select className="input-field" value={formData.role || 'USER'} onChange={e => setFormData({ ...formData, role: e.target.value })}>
                        <option value="USER">User</option>
                        <option value="ADMIN">Admin</option>
                      </select>
                      <select className="input-field" value={formData.riskProfile || 'Medium'} onChange={e => setFormData({ ...formData, riskProfile: e.target.value })}>
                        <option value="Low">Low Risk</option>
                        <option value="Medium">Medium Risk</option>
                        <option value="High">High Risk</option>
                      </select>
                    </div>
                    <input className="input-field" placeholder="New Password (leave blank to keep current)" type="password" value={formData.password || ''} onChange={e => setFormData({ ...formData, password: e.target.value })} />
                  </>
                )}

                {activeTable === 'logs' && (
                  <>
                    <input className="input-field" type="number" placeholder="Amount ($)" value={formData.amount || ''} onChange={e => setFormData({ ...formData, amount: parseFloat(e.target.value) })} required />
                    <select className="input-field" value={formData.category || 'Food'} onChange={e => setFormData({ ...formData, category: e.target.value })}>
                      <option value="Food">Food</option>
                      <option value="Rent">Rent</option>
                      <option value="Investment">Investment</option>
                      <option value="Subscription">Subscription</option>
                      <option value="Entertainment">Entertainment</option>
                    </select>
                    <input className="input-field" placeholder="Description" value={formData.description || ''} onChange={e => setFormData({ ...formData, description: e.target.value })} required />
                  </>
                )}

                <div className="modal-footer">
                  <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                  <button type="submit" className="btn-primary">Save Changes</button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
};

// --- APP WRAPPER ---
const App = () => {
  const { user, login, logout, isAdmin } = useAuth();
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'dark');

  useEffect(() => {
    // Remove all possible theme classes
    document.body.classList.remove('dark-theme', 'light-theme', 'gold-theme', 'ocean-theme', 'emerald-theme');
    // Add current theme class (if not default dark)
    if (theme !== 'dark') {
      document.body.classList.add(`${theme}-theme`);
    }
    localStorage.setItem('theme', theme);
  }, [theme]);

  const toggleTheme = (newTheme) => {
    setTheme(newTheme);
  };

  return (
    <Router>
      <Routes>
        <Route path="/login" element={!user ? <AuthPage onLogin={login} /> : <Navigate to={isAdmin ? "/admin" : "/dashboard"} />} />
        <Route path="/dashboard" element={user && !isAdmin ? <Dashboard user={user} onLogout={logout} theme={theme} setTheme={setTheme} /> : <Navigate to="/login" />} />
        <Route path="/admin" element={user && isAdmin ? <AdminPanel onLogout={logout} theme={theme} setTheme={setTheme} /> : <Navigate to="/login" />} />
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
};

export default App;
