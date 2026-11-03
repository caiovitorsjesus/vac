// Espelham os DTOs da API (pacote api.dto do backend). Valores decimais chegam como number.

export type Disciplina = {
  id: number
  nome: string
  cargaHorariaTotal: number
  percentualMaximoFaltas: number
  mediaMinima: number
  notaMaxima: number
}

/** Corpo de criação e de edição: a edição (PUT) também envia as regras completas. */
export type DisciplinaForm = Omit<Disciplina, 'id'>

export type Falta = {
  id: number
  /** Formato ISO: "2026-10-20". */
  data: string
  horas: number
  observacao: string | null
}

export type RegistrarFalta = {
  data: string
  horas: number
  observacao?: string
}

export type Avaliacao = {
  id: number
  nome: string
  pesoPercentual: number
  /** Nula enquanto a avaliação está pendente. */
  nota: number | null
}

export type CriarAvaliacao = {
  nome: string
  pesoPercentual: number
  nota?: number
}

export type StatusFaltas = 'SEGURO' | 'ATENCAO' | 'CRITICO' | 'REPROVADO_POR_FALTA'

export type SituacaoFaltas = {
  horasFaltadas: number
  limiteHoras: number
  horasRestantes: number
  horasExcedentes: number
  percentualConsumido: number
  status: StatusFaltas
}

export type ProjecaoNotas = {
  pontosGarantidos: number
  pesoConcluido: number
  pesoRestante: number
  /** Nula quando todas as avaliações já têm nota. */
  notaNecessaria: number | null
  viavel: boolean
}

export type StatusDisciplina = {
  disciplinaId: number
  nome: string
  faltas: SituacaoFaltas
  /** Nula enquanto os pesos das avaliações não somam 100%; o motivo vem em avisoNotas. */
  notas: ProjecaoNotas | null
  avisoNotas: string | null
}
