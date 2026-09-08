import React, { useEffect, useState } from 'react'
import { useAuth } from '../contexts/AuthContext'
import api from '../services/api'

type Projeto = { id: number; titulo: string; descricao: string; ano: number; semestre: number; dataAvaliacao: string; situacao: string; alunos: string[] }
type Comentario = { id: number; texto: string; autor: string; criadoEm: string }
type Aluno = { id: number; nome: string; email: string; perfis: string[] }

export default function Dashboard() {
  const { user, logout } = useAuth()
  const [projetos, setProjetos] = useState<Projeto[]>([])
  const [comentarios, setComentarios] = useState<Record<number, Comentario[]>>({})
  const [novoComentario, setNovoComentario] = useState<Record<number, string>>({})
  const [alunos, setAlunos] = useState<Aluno[]>([])
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [projeto, setProjeto] = useState({ titulo: '', descricao: '', ano: new Date().getFullYear(), semestre: 1, dataAvaliacao: '' })
  const podeGerenciar = Boolean(user?.perfis?.some(perfil => ['AVALIADOR', 'ADMINISTRADOR'].includes(perfil)))

  const carregarProjetos = async () => {
    try {
      const resposta = await api.get<Projeto[]>('/projetos')
      setProjetos(resposta.data)
      const respostas = await Promise.all(resposta.data.map(item => api.get<Comentario[]>(`/projetos/${item.id}/comentarios`)))
      setComentarios(Object.fromEntries(resposta.data.map((item, index) => [item.id, respostas[index].data])))
    } catch (err: any) { setErro(err?.response?.data?.message || 'Não foi possível carregar os projetos') }
  }

  useEffect(() => { carregarProjetos() }, [])
  useEffect(() => {
    if (!podeGerenciar) return
    api.get<Aluno[]>('/usuarios').then(response => setAlunos(response.data.filter(item => item.perfis.includes('ALUNO') && !item.perfis.includes('AVALIADOR')))).catch(() => setErro('Não foi possível carregar os alunos'))
  }, [podeGerenciar])

  const criarProjeto = async (event: React.FormEvent) => {
    event.preventDefault(); setErro('')
    try {
      if (editandoId) { await api.put(`/projetos/${editandoId}`, projeto); setMensagem('Projeto atualizado com sucesso.'); setEditandoId(null) }
      else { await api.post('/projetos', projeto); setMensagem('Projeto criado com sucesso.') }
      setProjeto({ titulo: '', descricao: '', ano: new Date().getFullYear(), semestre: 1, dataAvaliacao: '' }); carregarProjetos()
    }
    catch (err: any) { setErro(err?.response?.data?.message || 'Não foi possível criar o projeto') }
  }

  const enviarComentario = async (projetoId: number) => {
    const texto = novoComentario[projetoId]?.trim(); if (!texto) return
    try { const resposta = await api.post<Comentario>(`/projetos/${projetoId}/comentarios`, { texto }); setComentarios(atuais => ({ ...atuais, [projetoId]: [...(atuais[projetoId] || []), resposta.data] })); setNovoComentario(atuais => ({ ...atuais, [projetoId]: '' })) }
    catch (err: any) { setErro(err?.response?.data?.message || 'Não foi possível enviar o comentário') }
  }

  const enviarParaAvaliacao = async (projetoId: number) => {
    try { await api.post(`/projetos/${projetoId}/enviar`); setMensagem('Projeto enviado para avaliação.'); carregarProjetos() }
    catch (err: any) { setErro(err?.response?.data?.message || 'Não foi possível enviar o projeto para avaliação') }
  }

  const promoverAluno = async (id: number) => {
    try { await api.post(`/usuarios/${id}/promover-professor`); setAlunos(atuais => atuais.filter(aluno => aluno.id !== id)) }
    catch (err: any) { setErro(err?.response?.data?.message || 'Não foi possível promover o aluno') }
  }

  return <div className="min-h-screen p-6"><div className="max-w-5xl mx-auto bg-white p-6 rounded shadow">
    <div className="flex justify-between items-center mb-6"><h1 className="text-2xl">Dashboard</h1><div><span className="mr-4">{user?.nome || user?.email}</span><button onClick={logout} className="bg-red-500 text-white px-3 py-1 rounded">Sair</button></div></div>
    {erro && <div className="text-red-600 mb-4">{erro}</div>}{mensagem && <div className="text-green-600 mb-4">{mensagem}</div>}
    {!podeGerenciar && <section className="border-b pb-6 mb-6"><h2 className="text-xl mb-4">{editandoId ? 'Atualizar projeto integrador' : 'Criar projeto integrador'}</h2><form onSubmit={criarProjeto} className="grid gap-3 md:grid-cols-2">
      <input required placeholder="Título do projeto" value={projeto.titulo} onChange={e => setProjeto({ ...projeto, titulo: e.target.value })} className="border p-2" /><input required type="date" value={projeto.dataAvaliacao} onChange={e => setProjeto({ ...projeto, dataAvaliacao: e.target.value })} className="border p-2" />
      <input required type="number" min="1900" max="9999" value={projeto.ano} onChange={e => setProjeto({ ...projeto, ano: Number(e.target.value) })} className="border p-2" /><select value={projeto.semestre} onChange={e => setProjeto({ ...projeto, semestre: Number(e.target.value) })} className="border p-2"><option value={1}>1º semestre</option><option value={2}>2º semestre</option></select>
      <textarea required placeholder="Descreva o projeto e o que o grupo produziu" value={projeto.descricao} onChange={e => setProjeto({ ...projeto, descricao: e.target.value })} className="border p-2 md:col-span-2" rows={4} /><div className="flex gap-2"><button className="bg-blue-600 text-white px-4 py-2 rounded md:w-fit">{editandoId ? 'Atualizar projeto' : 'Salvar projeto'}</button>{editandoId && <button type="button" onClick={() => { setEditandoId(null); setProjeto({ titulo: '', descricao: '', ano: new Date().getFullYear(), semestre: 1, dataAvaliacao: '' }) }} className="border px-4 py-2 rounded">Cancelar</button>}</div>
    </form></section>}
    <h2 className="text-xl mb-4">{podeGerenciar ? 'Projetos dos alunos' : 'Meus projetos'}</h2>
    {projetos.length === 0 ? <p>Nenhum projeto cadastrado.</p> : <div className="space-y-5">{projetos.map(item => { const editavel = item.situacao === 'CADASTRADO'; return <article key={item.id} className="border p-4 rounded"><div className="flex justify-between gap-4"><div><h3 className="font-semibold text-lg">{item.titulo}</h3><p className="text-gray-600">{item.alunos.join(', ')}</p></div><div className="text-right"><span className="text-sm">{item.situacao}</span>{!podeGerenciar && editavel && <><button type="button" onClick={() => { setEditandoId(item.id); setProjeto({ titulo: item.titulo, descricao: item.descricao, ano: item.ano, semestre: item.semestre, dataAvaliacao: item.dataAvaliacao }) }} className="block text-blue-600 text-sm mt-2">Editar projeto</button><button type="button" onClick={() => enviarParaAvaliacao(item.id)} className="bg-green-600 text-white px-3 py-1 rounded text-sm mt-2">Enviar para avaliação</button></>}</div></div><p className="mt-3 whitespace-pre-wrap">{item.descricao}</p><p className="text-sm text-gray-600 mt-2">Avaliação: {item.dataAvaliacao} | {item.ano}/{item.semestre}</p><div className="mt-4 border-t pt-3"><h4 className="font-medium mb-2">Comentários</h4>{(comentarios[item.id] || []).map(comentario => <p key={comentario.id} className="mb-2"><strong>{comentario.autor}:</strong> {comentario.texto}</p>)}{editavel ? <div className="flex gap-2 mt-3"><input value={novoComentario[item.id] || ''} onChange={e => setNovoComentario({ ...novoComentario, [item.id]: e.target.value })} placeholder={podeGerenciar ? 'Mensagem para o grupo' : 'Responder ao professor'} className="border p-2 flex-1" /><button onClick={() => enviarComentario(item.id)} className="bg-blue-600 text-white px-3 rounded">Enviar</button></div> : <p className="text-sm text-gray-500 mt-3">Comentários encerrados após o envio para avaliação.</p>}</div></article> })}</div>}
    {podeGerenciar && <section className="mt-8 border-t pt-6"><h2 className="text-xl mb-4">Gerenciar alunos</h2>{alunos.length === 0 ? <p>Nenhum aluno disponível para promoção.</p> : alunos.map(aluno => <div key={aluno.id} className="flex justify-between border p-3 rounded mb-2"><span>{aluno.nome} ({aluno.email})</span><button onClick={() => promoverAluno(aluno.id)} className="bg-blue-600 text-white px-3 py-1 rounded">Tornar professor</button></div>)}</section>}
  </div></div>
}
