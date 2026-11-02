import type {
  Avaliacao,
  CriarAvaliacao,
  Disciplina,
  DisciplinaForm,
  Falta,
  RegistrarFalta,
  StatusDisciplina,
} from './tipos'

export const MENSAGEM_SEM_CONEXAO = 'Não foi possível conectar ao servidor. Verifique sua conexão.'
export const MENSAGEM_ERRO_INESPERADO = 'O servidor não respondeu como esperado. Tente novamente.'

/**
 * Erro de uma chamada à API, pronto para a tela exibir.
 * `status` 0 indica que a requisição nem chegou ao servidor (sem conexão).
 */
export class ApiErro extends Error {
  readonly status: number
  /** Campo do formulário → mensagem, quando o backend aponta campos inválidos (400 do @Valid). */
  readonly campos: Record<string, string>

  constructor(status: number, mensagem: string, campos: Record<string, string> = {}) {
    super(mensagem)
    this.name = 'ApiErro'
    this.status = status
    this.campos = campos
  }
}

/**
 * Faz a chamada, envia e recebe JSON e converte qualquer falha em {@link ApiErro}.
 * Os caminhos são relativos (/api/...): em desenvolvimento, o proxy do Vite os repassa ao backend.
 */
export async function requisicao<T>(caminho: string, opcoes: { metodo?: string; corpo?: unknown } = {}): Promise<T> {
  let resposta: Response
  try {
    resposta = await fetch(caminho, {
      method: opcoes.metodo ?? 'GET',
      headers: opcoes.corpo === undefined ? undefined : { 'Content-Type': 'application/json' },
      body: opcoes.corpo === undefined ? undefined : JSON.stringify(opcoes.corpo),
    })
  } catch {
    // o fetch só rejeita quando a requisição não sai (rede, servidor desligado sem proxy)
    throw new ApiErro(0, MENSAGEM_SEM_CONEXAO)
  }

  if (!resposta.ok) {
    throw await erroDaResposta(resposta)
  }
  if (resposta.status === 204) {
    return undefined as T
  }
  return (await resposta.json()) as T
}

async function erroDaResposta(resposta: Response): Promise<ApiErro> {
  try {
    const problema = (await resposta.json()) as { detail?: unknown; campos?: Record<string, string> }
    if (typeof problema.detail === 'string') {
      return new ApiErro(resposta.status, problema.detail, problema.campos ?? {})
    }
  } catch {
    // corpo vazio ou não-JSON: o proxy responde assim quando o backend está desligado
  }
  return new ApiErro(resposta.status, MENSAGEM_ERRO_INESPERADO)
}

const disciplinas = '/api/disciplinas'

/** Uma função por endpoint: as telas nunca escrevem URLs. */
export const api = {
  listarDisciplinas: () => requisicao<Disciplina[]>(disciplinas),
  buscarDisciplina: (id: number) => requisicao<Disciplina>(`${disciplinas}/${id}`),
  criarDisciplina: (dados: DisciplinaForm) => requisicao<Disciplina>(disciplinas, { metodo: 'POST', corpo: dados }),
  atualizarDisciplina: (id: number, dados: DisciplinaForm) =>
    requisicao<Disciplina>(`${disciplinas}/${id}`, { metodo: 'PUT', corpo: dados }),
  excluirDisciplina: (id: number) => requisicao<void>(`${disciplinas}/${id}`, { metodo: 'DELETE' }),
  status: (id: number) => requisicao<StatusDisciplina>(`${disciplinas}/${id}/status`),

  listarFaltas: (id: number) => requisicao<Falta[]>(`${disciplinas}/${id}/faltas`),
  registrarFalta: (id: number, dados: RegistrarFalta) =>
    requisicao<Falta>(`${disciplinas}/${id}/faltas`, { metodo: 'POST', corpo: dados }),

  listarAvaliacoes: (id: number) => requisicao<Avaliacao[]>(`${disciplinas}/${id}/avaliacoes`),
  criarAvaliacao: (id: number, dados: CriarAvaliacao) =>
    requisicao<Avaliacao>(`${disciplinas}/${id}/avaliacoes`, { metodo: 'POST', corpo: dados }),
  registrarNota: (id: number, avaliacaoId: number, nota: number) =>
    requisicao<Avaliacao>(`${disciplinas}/${id}/avaliacoes/${avaliacaoId}/nota`, { metodo: 'PATCH', corpo: { nota } }),
}
