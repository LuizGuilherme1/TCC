import React from 'react'
import { useAuth } from '../contexts/AuthContext'

export default function Dashboard() {
  const { user, logout } = useAuth()
  return (
    <div className="min-h-screen p-6">
      <div className="max-w-4xl mx-auto bg-white p-6 rounded shadow">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl">Dashboard</h1>
          <div>
            <span className="mr-4">{user?.nome || user?.email}</span>
            <button onClick={logout} className="bg-red-500 text-white px-3 py-1 rounded">Sair</button>
          </div>
        </div>

        <p>Bem-vindo ao sistema de avaliação de Projetos Integradores.</p>

      </div>
    </div>
  )
}
