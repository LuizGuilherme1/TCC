import React, { createContext, useContext, useEffect, useState } from 'react'
import api from '../services/api'

type User = {
  id?: number
  nome?: string
  email?: string
}

type AuthContextType = {
  user: User | null
  login: (token: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      // optionally fetch user profile
      api.get('/usuarios/me').then(r => setUser(r.data)).catch(() => setUser(null))
    }
  }, [])

  const login = async (token: string) => {
    localStorage.setItem('token', token)
    try {
      const resp = await api.get('/usuarios/me')
      setUser(resp.data)
    } catch {
      setUser(null)
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    setUser(null)
  }

  return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

export default AuthContext
