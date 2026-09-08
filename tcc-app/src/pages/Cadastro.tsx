import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'

export default function Cadastro() {
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    try {
      await api.post('/usuarios', { nome, email, senha })
      setSuccess('Cadastro realizado com sucesso. Redirecionando para o login...')
      setTimeout(() => navigate('/login'), 1200)
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Erro ao realizar cadastro')
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <form onSubmit={handleSubmit} className="w-full max-w-md p-6 bg-white rounded shadow">
        <h2 className="text-2xl mb-4">Cadastro</h2>
        {error && <div className="text-red-600 mb-2">{error}</div>}
        {success && <div className="text-green-600 mb-2">{success}</div>}
        <div className="mb-3">
          <label className="block" htmlFor="nome">Nome</label>
          <input id="nome" required value={nome} onChange={e => setNome(e.target.value)} className="w-full border p-2" />
        </div>
        <div className="mb-3">
          <label className="block" htmlFor="email">Email</label>
          <input id="email" type="email" required value={email} onChange={e => setEmail(e.target.value)} className="w-full border p-2" />
        </div>
        <div className="mb-3">
          <label className="block" htmlFor="senha">Senha</label>
          <input id="senha" type="password" required value={senha} onChange={e => setSenha(e.target.value)} className="w-full border p-2" />
        </div>
        <div className="flex items-center gap-3">
          <button className="bg-blue-600 text-white px-4 py-2 rounded">Cadastrar</button>
          <button type="button" onClick={() => navigate('/login')} className="border border-blue-600 text-blue-600 px-4 py-2 rounded">
            Voltar
          </button>
        </div>
      </form>
    </div>
  )
}