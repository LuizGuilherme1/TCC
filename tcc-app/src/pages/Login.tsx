import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'

export default function Login() {
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const resp = await api.post('/auth/login', { email, senha })
      // Save token and redirect (simplified)
      localStorage.setItem('token', resp.data.token)
      navigate('/dashboard')
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Erro ao autenticar')
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <form onSubmit={handleSubmit} className="w-full max-w-md p-6 bg-white rounded shadow">
        <h2 className="text-2xl mb-4">Login</h2>
        {error && <div className="text-red-600 mb-2">{error}</div>}
        <div className="mb-3">
          <label className="block">Email</label>
          <input value={email} onChange={e => setEmail(e.target.value)} className="w-full border p-2" />
        </div>
        <div className="mb-3">
          <label className="block">Senha</label>
          <input type="password" value={senha} onChange={e => setSenha(e.target.value)} className="w-full border p-2" />
        </div>
        <button className="bg-blue-600 text-white px-4 py-2 rounded">Entrar</button>
      </form>
    </div>
  )
}
