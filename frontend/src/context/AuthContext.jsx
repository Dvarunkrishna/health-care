import React, { createContext, useCallback, useMemo, useState } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

const STORAGE_TOKEN_KEY = 'hc_token';
const STORAGE_USER_KEY = 'hc_user';

const normalizeUser = (payload) => {
  if (!payload) return null;
  // AuthResponse shape: { token, email, role, fullName, userId }
  if (payload.userId !== undefined || payload.fullName !== undefined || payload.role !== undefined) {
    return {
      id: payload.userId ?? payload.id ?? null,
      fullName: payload.fullName ?? payload.name ?? payload.email,
      email: payload.email ?? null,
      role: payload.role ?? null,
      roles: payload.role ? [payload.role] : payload.roles,
    };
  }
  // Generic user object possibly nested
  if (payload.user) return normalizeUser(payload.user);
  if (payload.data) return normalizeUser(payload.data);
  return payload;
};

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem(STORAGE_TOKEN_KEY));
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem(STORAGE_USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  });

  const persist = useCallback((authData) => {
    const payload = authData?.data ?? authData ?? {};
    const newToken = payload.token || payload.accessToken || payload.jwt;
    const newUser = normalizeUser(payload);
    if (newToken) {
      localStorage.setItem(STORAGE_TOKEN_KEY, newToken);
      setToken(newToken);
    }
    if (newUser) {
      localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(newUser));
      setUser(newUser);
    }
    return { token: newToken, user: newUser };
  }, []);

  const login = useCallback(async (credentials) => {
    const { data } = await api.post('/api/auth/login', credentials);
    return persist(data);
  }, [persist]);

  const register = useCallback(async (payload) => {
    const { data } = await api.post('/api/auth/register', payload);
    return persist(data);
  }, [persist]);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_TOKEN_KEY);
    localStorage.removeItem(STORAGE_USER_KEY);
    setToken(null);
    setUser(null);
  }, []);

  const updateUser = useCallback((nextUser) => {
    const merged = { ...user, ...nextUser };
    localStorage.setItem(STORAGE_USER_KEY, JSON.stringify(merged));
    setUser(merged);
  }, [user]);

  const hasRole = useCallback(
    (role) => {
      if (!user) return false;
      const roles = Array.isArray(user.roles) ? user.roles : [user.role].filter(Boolean);
      return roles.some((r) => String(r).toUpperCase() === String(role).toUpperCase() || String(r).toUpperCase() === `ROLE_${String(role).toUpperCase()}`);
    },
    [user]
  );

  const roleHelpers = useMemo(() => ({
    isAdmin: hasRole('ADMIN'),
    isDoctor: hasRole('DOCTOR'),
    isPatient: hasRole('PATIENT') || hasRole('USER'),
  }), [hasRole]);

  const value = useMemo(
    () => ({
      user,
      token,
      isAuthenticated: Boolean(token && user),
      login,
      register,
      logout,
      updateUser,
      hasRole,
      ...roleHelpers,
    }),
    [user, token, login, register, logout, updateUser, hasRole, roleHelpers]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuthContext = () => {
  const ctx = React.useContext(AuthContext);
  if (!ctx) throw new Error('useAuthContext must be used within AuthProvider');
  return ctx;
};

export default AuthContext;
