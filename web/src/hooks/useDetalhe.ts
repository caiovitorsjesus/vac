import { useCallback, useEffect, useState } from 'react'
import { api, ApiErro } from '../api/cliente'
import type { Avaliacao, Disciplina, Falta, StatusDisciplina } from '../api/tipos'

export type DadosDetalhe = {
  disciplina: Disciplina
  status: StatusDisciplina
  faltas: Falta[]
  avaliacoes: Avaliacao[]
}

type EstadoDetalhe =
  | { tipo: 'carregando' }
  | { tipo: 'nao-encontrada' }
  | { tipo: 'erro'; mensagem: string }
  | { tipo: 'pronto'; dados: DadosDetalhe }

/** Busca tudo o que a tela de detalhe mostra, em paralelo. Não altera estado: só devolve o resultado. */
async function carregarDetalhe(id: number): Promise<EstadoDetalhe> {
  try {
    const [disciplina, status, faltas, avaliacoes] = await Promise.all([
      api.buscarDisciplina(id),
      api.status(id),
      api.listarFaltas(id),
      api.listarAvaliacoes(id),
    ])
    return { tipo: 'pronto', dados: { disciplina, status, faltas, avaliacoes } }
  } catch (e) {
    if (e instanceof ApiErro && e.status === 404) return { tipo: 'nao-encontrada' }
    return { tipo: 'erro', mensagem: e instanceof ApiErro ? e.message : 'Erro inesperado ao carregar a disciplina.' }
  }
}

/** Estado da tela de detalhe. `recarregar` busca tudo de novo depois de cada alteração. */
export function useDetalhe(id: number) {
  const [estado, setEstado] = useState<EstadoDetalhe>({ tipo: 'carregando' })

  useEffect(() => {
    let ativo = true
    carregarDetalhe(id).then((novo) => {
      // ignora a resposta se a tela mudou de disciplina ou saiu antes de ela chegar
      if (ativo) setEstado(novo)
    })
    return () => {
      ativo = false
    }
  }, [id])

  const recarregar = useCallback(async () => {
    setEstado(await carregarDetalhe(id))
  }, [id])

  return { estado, recarregar }
}
