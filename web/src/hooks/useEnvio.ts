import { useEffect, useRef, useState } from 'react'
import { ApiErro } from '../api/cliente'
import { useToast } from '../toast/contexto'

/**
 * Envio de formulário com o tratamento padrão: em caso de sucesso, mostra o toast e chama `aoConcluir`;
 * em caso de erro da API, expõe os erros por campo (400 do @Valid) ou a mensagem geral (400 do domínio, 404, 409).
 * Depois de uma falha, o foco vai para o primeiro campo inválido ou para a mensagem geral.
 */
export function useEnvio(aoConcluir: () => void) {
  const mostrarToast = useToast()
  const formulario = useRef<HTMLFormElement & HTMLDivElement>(null)
  const [erros, setErros] = useState<Record<string, string>>({})
  const [erroGeral, setErroGeral] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)
  const [falhas, setFalhas] = useState(0)

  useEffect(() => {
    if (falhas === 0) return
    formulario.current?.querySelector<HTMLElement>('[aria-invalid="true"], [role="alert"]')?.focus()
  }, [falhas])

  /** `depois` substitui `aoConcluir` quando uma ação tem outro destino (ex.: excluir volta ao painel). */
  async function executar(acao: () => Promise<unknown>, mensagemSucesso: string, depois = aoConcluir) {
    setEnviando(true)
    setErros({})
    setErroGeral(null)
    try {
      await acao()
      mostrarToast(mensagemSucesso)
      depois()
    } catch (e) {
      if (e instanceof ApiErro) {
        setErros(e.campos)
        // com erros por campo, eles aparecem embaixo de cada input; senão, a mensagem vai no topo
        setErroGeral(Object.keys(e.campos).length > 0 ? null : e.message)
      } else {
        setErroGeral('Erro inesperado. Tente novamente.')
      }
      setFalhas((n) => n + 1)
    } finally {
      setEnviando(false)
    }
  }

  return { formulario, erros, erroGeral, enviando, executar }
}
