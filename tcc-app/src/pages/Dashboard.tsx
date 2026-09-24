import React, { useEffect, useState } from 'react'
import { useAuth } from '../contexts/AuthContext'
import api from '../services/api'

type RespostaAvaliacao = { ordem: number; pergunta: string; pontuacao: number }
type ResultadoAvaliacao = { id: number; avaliador: string; pontuacaoTotal: number; media: number; observacao?: string; respostas: RespostaAvaliacao[] }
type Projeto = { id: number; titulo: string; descricao: string; ano: number; semestre: number; dataAvaliacao: string; situacao: string; alunos: string[]; avaliacoes?: ResultadoAvaliacao[] }
type Comentario = { id: number; texto: string; autor: string; criadoEm: string }
type Aluno = { id: number; nome: string; email: string; perfis: string[] }
type Pergunta = { id: number; ordem: number; pergunta: { titulo: string } }
type Avaliacao = { id: number; formulario: { id: number }; statusAvaliacao: { nome: string }; observacao?: string }

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
  const [perguntas, setPerguntas] = useState<Pergunta[]>([])
  const [avaliacao, setAvaliacao] = useState<Avaliacao | null>(null)
  const [notas, setNotas] = useState<Record<number, number>>({})
  const [observacao, setObservacao] = useState('')
  const [projetoEmAvaliacao, setProjetoEmAvaliacao] = useState<number | null>(null)
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
    api.get<Aluno[]>('/usuarios').then(response => setAlunos(response.data.filter(item => item.perfis.some(perfil => ['ALUNO', 'PROFESSOR'].includes(perfil))))).catch(() => setErro('Não foi possível carregar os usuários elegíveis'))
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

  const promoverUsuario = async (id: number, perfil: 'professor' | 'avaliador') => {
    try {
      await api.post(`/usuarios/${id}/promover-${perfil}`)
      if (perfil === 'avaliador') setAlunos(atuais => atuais.filter(aluno => aluno.id !== id))
      else setAlunos(atuais => atuais.map(aluno => aluno.id === id ? { ...aluno, perfis: [...new Set([...aluno.perfis, 'PROFESSOR'])] } : aluno))
      setMensagem(`Usuário promovido a ${perfil} com sucesso.`)
    } catch (err: any) { setErro(err?.response?.data?.message || `Não foi possível promover a ${perfil}`) }
  }

  const abrirAvaliacao = async (projetoId: number) => {
    try {
      setErro('')
      const formulario = await api.get<{ id: number }>('/avaliacoes/formulario-padrao')
      const perguntasResposta = await api.get<Pergunta[]>(`/avaliacoes/formularios/${formulario.data.id}/perguntas`)
      let atual: Avaliacao
      try { atual = (await api.get<Avaliacao>(`/avaliacoes/projetos/${projetoId}/minha`)).data }
      catch { atual = (await api.post<Avaliacao>('/avaliacoes', { formularioId: formulario.data.id, projetoIntegradorId: projetoId })).data }
      if (atual.statusAvaliacao.nome === 'PENDENTE') atual = (await api.post<Avaliacao>(`/avaliacoes/${atual.id}/iniciar`)).data
      setPerguntas(perguntasResposta.data); setAvaliacao(atual); setProjetoEmAvaliacao(projetoId)
    } catch (err: any) { setErro(err?.response?.data?.message || 'Não foi possível abrir a avaliação') }
  }

  const finalizarAvaliacao = async () => {
    if (!avaliacao || perguntas.some(pergunta => !notas[pergunta.id])) { setErro('Responda todas as perguntas antes de finalizar.'); return }
    try {
      await Promise.all(perguntas.map(pergunta => api.post(`/avaliacoes/${avaliacao.id}/respostas`, { formularioPerguntaId: pergunta.id, pontuacao: notas[pergunta.id] })))
      await api.post(`/avaliacoes/${avaliacao.id}/finalizar`, { observacao })
      setMensagem('Avaliação finalizada com sucesso.'); setAvaliacao(null); setProjetoEmAvaliacao(null); setNotas({}); setObservacao(''); carregarProjetos()
    } catch (err: any) { setErro(err?.response?.data?.message || 'Não foi possível finalizar a avaliação') }
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
    {projetos.length === 0 ? <p>Nenhum projeto cadastrado.</p> : <div className="space-y-5">{projetos.map(item => { const editavel = item.situacao === 'CADASTRADO'; return <article key={item.id} className="border p-4 rounded"><div className="flex justify-between gap-4"><div><h3 className="font-semibold text-lg">{item.titulo}</h3><p className="text-gray-600">{item.alunos.join(', ')}</p></div><div className="text-right"><span className="text-sm">{item.situacao}</span>{!podeGerenciar && editavel && <><button type="button" onClick={() => { setEditandoId(item.id); setProjeto({ titulo: item.titulo, descricao: item.descricao, ano: item.ano, semestre: item.semestre, dataAvaliacao: item.dataAvaliacao }) }} className="block text-blue-600 text-sm mt-2">Editar projeto</button><button type="button" onClick={() => enviarParaAvaliacao(item.id)} className="bg-green-600 text-white px-3 py-1 rounded text-sm mt-2">Enviar para avaliação</button></>}{podeGerenciar && item.situacao === 'EM_AVALIACAO' && <button type="button" onClick={() => abrirAvaliacao(item.id)} className="block bg-blue-600 text-white px-3 py-1 rounded text-sm mt-2">Avaliar projeto</button>}</div></div><p className="mt-3 whitespace-pre-wrap">{item.descricao}</p><p className="text-sm text-gray-600 mt-2">Avaliação: {item.dataAvaliacao} | {item.ano}/{item.semestre}</p><div className="mt-4 border-t pt-3"><h4 className="font-medium mb-2">Comentários</h4>{(comentarios[item.id] || []).map(comentario => <p key={comentario.id} className="mb-2"><strong>{comentario.autor}:</strong> {comentario.texto}</p>)}{editavel ? <div className="flex gap-2 mt-3"><input value={novoComentario[item.id] || ''} onChange={e => setNovoComentario({ ...novoComentario, [item.id]: e.target.value })} placeholder={podeGerenciar ? 'Mensagem para o grupo' : 'Responder ao professor'} className="border p-2 flex-1" /><button onClick={() => enviarComentario(item.id)} className="bg-blue-600 text-white px-3 rounded">Enviar</button></div> : <p className="text-sm text-gray-500 mt-3">Comentários encerrados após o envio para avaliação.</p>}</div>{projetoEmAvaliacao === item.id && avaliacao && <section className="mt-5 border-t pt-4"><h4 className="font-semibold mb-3">Formulário de avaliação</h4><div className="space-y-4">{perguntas.map(pergunta => <div key={pergunta.id}><p className="mb-2">{pergunta.ordem}. {pergunta.pergunta.titulo}</p><div className="flex gap-2">{[1, 2, 3, 4, 5].map(nota => <button type="button" key={nota} onClick={() => setNotas(atuais => ({ ...atuais, [pergunta.id]: nota }))} className={`border px-3 py-1 rounded ${notas[pergunta.id] === nota ? 'bg-blue-600 text-white' : ''}`}>{nota}</button>)}</div></div>)}</div><textarea value={observacao} onChange={e => setObservacao(e.target.value)} placeholder="Principais pontos que precisam ser melhorados / Sugestões do avaliador" className="border p-2 w-full mt-5" rows={5} /><button type="button" onClick={finalizarAvaliacao} className="bg-green-600 text-white px-4 py-2 rounded mt-3">Finalizar avaliação</button></section>}</article> })}</div>}
    {podeGerenciar && <section className="mt-8 border-t pt-6"><h2 className="text-xl mb-4">Gerenciar usuários</h2>{alunos.length === 0 ? <p>Nenhum usuário disponível para promoção.</p> : alunos.map(aluno => <div key={aluno.id} className="flex justify-between border p-3 rounded mb-2"><span>{aluno.nome} ({aluno.email})</span><div className="flex gap-2">{!aluno.perfis.includes('PROFESSOR') && <button onClick={() => promoverUsuario(aluno.id, 'professor')} className="bg-indigo-600 text-white px-3 py-1 rounded">Tornar professor</button>}<button onClick={() => promoverUsuario(aluno.id, 'avaliador')} className="bg-blue-600 text-white px-3 py-1 rounded">Tornar avaliador</button></div></div>)}</section>}
    {!podeGerenciar && projetos.some(item => item.avaliacoes?.length) && <section className="mt-8 border-t pt-6"><h2 className="text-xl mb-4">Resultados das avaliações</h2>{projetos.flatMap(item => (item.avaliacoes || []).map(resultado => <article key={`${item.id}-${resultado.id}`} className="border p-4 rounded mb-4"><h3 className="font-semibold">{item.titulo}</h3><p className="text-sm text-gray-600 mt-1">Avaliador: {resultado.avaliador} | Nota média: {resultado.media} | Total: {resultado.pontuacaoTotal}</p>{resultado.observacao && <p className="mt-2 whitespace-pre-wrap"><strong>Resposta do avaliador:</strong> {resultado.observacao}</p>}<div className="mt-3 space-y-2">{resultado.respostas.map(resposta => <p key={resposta.ordem} className="text-sm"><span className="font-medium">{resposta.ordem}. {resposta.pontuacao}/5</span> {resposta.pergunta}</p>)}</div></article>))}</section>}
  </div></div>
}
