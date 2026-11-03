import { useCallback, useEffect, useState } from 'react'
import { api, ApiErro } from '../api/cliente'
import type { Disciplina, StatusDisciplina } from '../api/tipos'

export type ItemPainel = { disciplina: Disciplina; status: StatusDisciplina }

type EstadoPainel =
  | { tipo: 'carregando' }
  | { tipo: 'erro'; mensagem: string }
  | { tipo: 'pronto'; itens: ItemPainel[] }

/** Busca as disciplinas e o status de cada uma. Não altera estado: só devolve o resultado. */
async function carregarPainel(): Promise<EstadoPainel> {
  try {
    const disciplinas = await api.listarDisciplinas()
    // um /status por disciplina, todos em paralelo: o tempo total é o da chamada mais lenta, não a soma
    const status = await Promise.all(disciplinas.map((d) => api.status(d.id)))
    return { tipo: 'pronto', itens: disciplinas.map((disciplina, i) => ({ disciplina, status: status[i] })) }
  } catch (e) {
    return { tipo: 'erro', mensagem: e instanceof ApiErro ? e.message : 'Erro inesperado ao carregar o painel.' }
  }
}

/**
 * Estado do painel. `recarregar` busca tudo de novo; as telas chamam depois de cada alteração.
 */
export function usePainel() {
  const [estado, setEstado] = useState<EstadoPainel>({ tipo: 'carregando' })

  useEffect(() => {
    let ativo = true
    carregarPainel().then((novo) => {
      // ignora a resposta se o componente já saiu da tela (ou o efeito rodou de novo)
      if (ativo) setEstado(novo)
    })
    return () => {
      ativo = false
    }
  }, [])

  const recarregar = useCallback(async () => {
    setEstado(await carregarPainel())
  }, [])

  return { estado, recarregar }
}
