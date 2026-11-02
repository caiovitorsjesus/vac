import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiErro, MENSAGEM_ERRO_INESPERADO, MENSAGEM_SEM_CONEXAO, requisicao } from './cliente'

// simula o fetch: cada teste define a resposta, sem backend rodando
function responder(status: number, corpo?: unknown) {
  const resposta = new Response(corpo === undefined ? null : JSON.stringify(corpo), { status })
  const fetchFalso = vi.fn().mockResolvedValue(resposta)
  vi.stubGlobal('fetch', fetchFalso)
  return fetchFalso
}

async function erroDe(promessa: Promise<unknown>): Promise<ApiErro> {
  try {
    await promessa
  } catch (e) {
    return e as ApiErro
  }
  throw new Error('a requisição deveria ter falhado')
}

afterEach(() => {
  vi.unstubAllGlobals()
})

describe('requisicao', () => {
  it('devolve o JSON da resposta de sucesso', async () => {
    responder(200, [{ id: 1, nome: 'Cálculo I' }])

    await expect(requisicao('/api/disciplinas')).resolves.toEqual([{ id: 1, nome: 'Cálculo I' }])
  })

  it('envia o corpo como JSON com o cabeçalho Content-Type', async () => {
    const fetchFalso = responder(201, { id: 1 })

    await requisicao('/api/disciplinas', { metodo: 'POST', corpo: { nome: 'Cálculo I' } })

    expect(fetchFalso).toHaveBeenCalledWith('/api/disciplinas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: '{"nome":"Cálculo I"}',
    })
  })

  it('devolve undefined no 204 (exclusão), sem tentar ler corpo', async () => {
    responder(204)

    await expect(requisicao('/api/disciplinas/1', { metodo: 'DELETE' })).resolves.toBeUndefined()
  })

  it('usa o detail do ProblemDetail como mensagem', async () => {
    responder(404, { status: 404, detail: 'Disciplina 999 não encontrada.' })

    const erro = await erroDe(requisicao('/api/disciplinas/999'))

    expect(erro).toBeInstanceOf(ApiErro)
    expect(erro.status).toBe(404)
    expect(erro.message).toBe('Disciplina 999 não encontrada.')
  })

  it('expõe os campos inválidos do 400 de validação', async () => {
    responder(400, { status: 400, detail: 'Dados inválidos.', campos: { nome: 'não deve estar em branco' } })

    const erro = await erroDe(requisicao('/api/disciplinas', { metodo: 'POST', corpo: {} }))

    expect(erro.status).toBe(400)
    expect(erro.campos).toEqual({ nome: 'não deve estar em branco' })
  })

  it('mantém a mensagem de conflito do 409', async () => {
    responder(409, { status: 409, detail: 'Os pesos já somam 100%. Restam 0% para novas avaliações.' })

    const erro = await erroDe(requisicao('/api/disciplinas/1/avaliacoes', { metodo: 'POST', corpo: {} }))

    expect(erro.status).toBe(409)
    expect(erro.message).toBe('Os pesos já somam 100%. Restam 0% para novas avaliações.')
  })

  it('usa mensagem genérica quando o erro não é um ProblemDetail', async () => {
    // é o que o proxy do Vite devolve com o backend desligado: 502 com corpo vazio
    responder(502)

    const erro = await erroDe(requisicao('/api/disciplinas'))

    expect(erro.status).toBe(502)
    expect(erro.message).toBe(MENSAGEM_ERRO_INESPERADO)
  })

  it('informa falta de conexão quando a requisição nem sai', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))

    const erro = await erroDe(requisicao('/api/disciplinas'))

    expect(erro.status).toBe(0)
    expect(erro.message).toBe(MENSAGEM_SEM_CONEXAO)
  })
})
