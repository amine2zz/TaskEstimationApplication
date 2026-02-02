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
  X
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import axios from 'axios';
import './App.css';

// --- AUTH STATE SIMULATION ---
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

// --- COMPONENTS ---

const LoginPage = ({ onLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (email === 'admin@proxym.com' && password === 'admin') {
      onLogin({ id: 99, name: 'Admin User', role: 'ADMIN', email });
      navigate('/admin');
    } else {
      onLogin({ id: 1, name: 'John Doe', role: 'USER', email });
      navigate('/dashboard');
    }
  };

  return (
    <div className="auth-container">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="glass-card login-card"
      >
        <div className="logo-section">
          <div className="logo-blob"></div>
          <h1>Proxym</h1>
          <p>Intelligence Platform</p>
        </div>
        <form onSubmit={handleSubmit}>
          <input
            className="input-field"
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            className="input-field"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '10px' }}>
            Sign In
          </button>
        </form>
        <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
          <p>Hint: admin@proxym.com / admin</p>
        </div>
      </motion.div>
    </div>
  );
};

const Dashboard = ({ user, onLogout, isDarkMode, toggleTheme }) => {
  const [recs, setRecs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get('http://localhost:8081/api/recommendations/1')
      .then(res => {
        setRecs(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  return (
    <div className="main-layout">
      {/* Sidebar */}
      <aside className="sidebar glass-card">
        <div className="sidebar-header">
          <div className="logo-dot"></div>
          <span>Proxym AI</span>
        </div>
        <nav>
          <Link to="/dashboard" className="nav-link active"><LayoutDashboard size={20} /> Dashboard</Link>
          <Link to="/investments" className="nav-link"><TrendingUp size={20} /> Investments</Link>
          <Link to="/cards" className="nav-link"><CreditCard size={20} /> My Cards</Link>
          <div className="sidebar-divider"></div>
          <Link to="/settings" className="nav-link"><Settings size={20} /> Settings</Link>
        </nav>
        <button className="logout-btn" onClick={onLogout}><LogOut size={18} /> Logout</button>
      </aside>

      {/* Content */}
      <main className="content-area">
        <header className="content-header">
          <div>
            <h2>Welcome back, {user?.name}</h2>
            <p className="subtitle">Here's your financial status overview.</p>
          </div>
          <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
            <button className="user-pill glass-card" onClick={toggleTheme} style={{ cursor: 'pointer', border: 'none' }}>
              {isDarkMode ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <div className="user-pill glass-card">
              <UserIcon size={18} />
              <span>{user?.name}</span>
            </div>
          </div>
        </header>

        <div className="stats-grid">
          <div className="glass-card stat-item">
            <span className="label">Total Balance</span>
            <h3>$45,280.00</h3>
            <span className="trend positive">+2.4%</span>
          </div>
          <div className="glass-card stat-item">
            <span className="label">Monthly Spend</span>
            <h3>$3,120.50</h3>
            <span className="trend negative">-1.2%</span>
          </div>
          <div className="glass-card stat-item">
            <span className="label">AI Confidence</span>
            <h3>98%</h3>
            <span className="trend positive">High</span>
          </div>
        </div>

        <section className="recommendations-section">
          <h3>Smart Recommendations</h3>
          {loading ? (
            <div className="loader">Analyzing spending <span>...</span></div>
          ) : (
            <div className="recs-grid">
              {recs.map(p => (
                <motion.div
                  whileHover={{ scale: 1.02 }}
                  key={p.id}
                  className="glass-card product-card"
                >
                  <div className="card-header">
                    <span className={`badge ${p.type.toLowerCase()}`}>{p.type}</span>
                    <BarChart3 size={18} color="var(--primary)" />
                  </div>
                  <h4>{p.name}</h4>
                  <p>{p.description}</p>
                  <div className="card-footer">
                    <div className="rate">
                      <span className="rate-value">{p.interestRate}%</span>
                      <span className="rate-label">APY</span>
                    </div>
                    <button className="btn-icon"><ArrowRight size={18} /></button>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

const AdminPanel = ({ onLogout, isDarkMode, toggleTheme }) => {
  const [activeTable, setActiveTable] = useState('products');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({});

  const API_BASE = 'http://localhost:8081/api';

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

  useEffect(() => {
    fetchData();
  }, [activeTable]);

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this item?')) return;
    const endpoint = activeTable === 'products' ? 'products' : activeTable === 'users' ? 'users' : 'transactions';
    try {
      await axios.delete(`${API_BASE}/${endpoint}/${id}`);
      fetchData();
    } catch (err) {
      alert('Error deleting item');
    }
  };

  const handleOpenModal = (item = null) => {
    setEditingItem(item);
    if (item) {
      setFormData(item);
    } else {
      // Default empty form
      if (activeTable === 'products') {
        setFormData({ name: '', type: 'SAVINGS', description: '', interestRate: 0, minimumEntry: 0 });
      } else if (activeTable === 'users') {
        setFormData({ name: '', email: '', age: 18, monthlyIncome: 0, riskProfile: 'Medium', financialGoals: 'Savings' });
      }
    }
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const endpoint = activeTable === 'products' ? 'products' : activeTable === 'users' ? 'users' : 'transactions';
    try {
      if (editingItem) {
        await axios.put(`${API_BASE}/${endpoint}/${editingItem.id}`, formData);
      } else {
        await axios.post(`${API_BASE}/${endpoint}`, formData);
      }
      setShowModal(false);
      fetchData();
    } catch (err) {
      alert('Error saving data');
    }
  };

  const renderRows = () => {
    if (loading) return <tr><td colSpan="5" style={{ textAlign: 'center', padding: '40px' }}>Loading...</td></tr>;
    return data.map(item => (
      <tr key={item.id}>
        <td>#{item.id}</td>
        <td>{item.name || item.category || 'N/A'}</td>
        <td>{item.type || item.email || item.description || 'N/A'}</td>
        <td>{item.interestRate !== undefined ? `${item.interestRate}%` : item.amount ? `$${item.amount}` : item.riskProfile || 'N/A'}</td>
        <td>
          <button className="edit-btn" onClick={() => handleOpenModal(item)}><Edit3 size={16} /></button>
          {activeTable !== 'logs' && (
            <button className="delete-btn" onClick={() => handleDelete(item.id)}><Trash2 size={16} /></button>
          )}
        </td>
      </tr>
    ));
  };

  return (
    <div className="admin-layout">
      {/* Sidebar */}
      <nav className="admin-sidebar glass-card">
        <div className="admin-logo">
          <ShieldCheck size={24} color="var(--primary)" />
          <span>Admin Console</span>
        </div>
        <div className="nav-items">
          <button onClick={() => setActiveTable('products')} className={activeTable === 'products' ? 'active' : ''}>
            <Database size={18} /> Products
          </button>
          <button onClick={() => setActiveTable('users')} className={activeTable === 'users' ? 'active' : ''}>
            <UserIcon size={18} /> Users
          </button>
          <button onClick={() => setActiveTable('logs')} className={activeTable === 'logs' ? 'active' : ''}>
            <BarChart3 size={18} /> Audit Logs
          </button>
        </div>

        <div style={{ marginTop: 'auto', display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <button className="nav-link" onClick={toggleTheme}>
            {isDarkMode ? <Sun size={18} /> : <Moon size={18} />}
            {isDarkMode ? 'Light Mode' : 'Dark Mode'}
          </button>
          <button className="logout-btn" onClick={onLogout}><LogOut size={18} /> Logout</button>
        </div>
      </nav>

      <main className="admin-main">
        <header className="admin-header">
          <div className="search-bar glass-card">
            <Search size={18} color="var(--text-secondary)" />
            <input type="text" placeholder="Search entries..." />
          </div>
          {activeTable !== 'logs' && (
            <button className="btn-primary" onClick={() => handleOpenModal()}><Plus size={18} /> Add Entry</button>
          )}
        </header>

        <div className="table-container glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
            <h3 style={{ margin: 0 }}>Table: {activeTable.toUpperCase()}</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{data.length} entries found</span>
          </div>
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Primary Field</th>
                <th>Details</th>
                <th>Value / Profile</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {renderRows()}
            </tbody>
          </table>
        </div>
      </main>

      {/* CRUD MODAL */}
      <AnimatePresence>
        {showModal && (
          <div className="modal-overlay">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 20 }}
              className="glass-card modal-content"
            >
              <div className="modal-header">
                <h3>{editingItem ? 'Edit Entry' : 'Add New Entry'}</h3>
                <button className="close-btn" onClick={() => setShowModal(false)}><X size={20} /></button>
              </div>
              <form onSubmit={handleSave}>
                {activeTable === 'products' ? (
                  <>
                    <input className="input-field" placeholder="Product Name" value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} required />
                    <select className="input-field" value={formData.type || 'SAVINGS'} onChange={e => setFormData({ ...formData, type: e.target.value })}>
                      <option value="SAVINGS">SAVINGS</option>
                      <option value="INVESTMENT">INVESTMENT</option>
                      <option value="LOAN">LOAN</option>
                      <option value="INSURANCE">INSURANCE</option>
                    </select>
                    <textarea className="input-field" placeholder="Description" value={formData.description || ''} onChange={e => setFormData({ ...formData, description: e.target.value })} required />
                    <input className="input-field" type="number" step="0.1" placeholder="Interest Rate %" value={formData.interestRate || ''} onChange={e => setFormData({ ...formData, interestRate: e.target.value })} required />
                    <input className="input-field" type="number" placeholder="Min Entry" value={formData.minimumEntry || ''} onChange={e => setFormData({ ...formData, minimumEntry: e.target.value })} required />
                  </>
                ) : (
                  <>
                    <input className="input-field" placeholder="Full Name" value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} required />
                    <input className="input-field" type="email" placeholder="Email" value={formData.email || ''} onChange={e => setFormData({ ...formData, email: e.target.value })} required />
                    <input className="input-field" type="number" placeholder="Age" value={formData.age || ''} onChange={e => setFormData({ ...formData, age: e.target.value })} required />
                    <input className="input-field" type="number" placeholder="Monthly Income" value={formData.monthlyIncome || ''} onChange={e => setFormData({ ...formData, monthlyIncome: e.target.value })} required />
                    <select className="input-field" value={formData.riskProfile || 'Medium'} onChange={e => setFormData({ ...formData, riskProfile: e.target.value })}>
                      <option value="Low">Low</option>
                      <option value="Medium">Medium</option>
                      <option value="High">High</option>
                    </select>
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
  const [isDarkMode, setIsDarkMode] = useState(true);

  useEffect(() => {
    if (isDarkMode) {
      document.body.classList.remove('light-theme');
    } else {
      document.body.classList.add('light-theme');
    }
  }, [isDarkMode]);

  const toggleTheme = () => setIsDarkMode(!isDarkMode);

  return (
    <Router>
      <Routes>
        <Route path="/login" element={!user ? <LoginPage onLogin={login} /> : <Navigate to={isAdmin ? "/admin" : "/dashboard"} />} />

        <Route path="/dashboard" element={
          user && !isAdmin ? <Dashboard user={user} onLogout={logout} isDarkMode={isDarkMode} toggleTheme={toggleTheme} /> : <Navigate to="/login" />
        } />

        <Route path="/admin" element={
          user && isAdmin ? <AdminPanel onLogout={logout} isDarkMode={isDarkMode} toggleTheme={toggleTheme} /> : <Navigate to="/login" />
        } />

        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
};

export default App;
