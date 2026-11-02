import { useState } from 'react'
import { ApiErro } from '../api/cliente'
import { useToast } from '../toast/contexto'

/**
 * Envio de formulário com o tratamento padrão: em caso de sucesso, mostra o toast e chama `aoConcluir`;
 * em caso de erro da API, expõe os erros por campo (400 do @Valid) ou a mensagem geral (400 do domínio, 404, 409).
 */
export function useEnvio(aoConcluir: () => void) {
  const mostrarToast = useToast()
  const [erros, setErros] = useState<Record<string, string>>({})
  const [erroGeral, setErroGeral] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function executar(acao: () => Promise<unknown>, mensagemSucesso: string): Promise<boolean> {
    setEnviando(true)
    setErros({})
    setErroGeral(null)
    try {
      await acao()
      mostrarToast(mensagemSucesso)
      aoConcluir()
      return true
    } catch (e) {
      if (e instanceof ApiErro) {
        setErros(e.campos)
        // com erros por campo, eles aparecem embaixo de cada input; senão, a mensagem vai no topo
        setErroGeral(Object.keys(e.campos).length > 0 ? null : e.message)
      } else {
        setErroGeral('Erro inesperado. Tente novamente.')
      }
      return false
    } finally {
      setEnviando(false)
    }
  }

  return { erros, erroGeral, enviando, executar }
}
