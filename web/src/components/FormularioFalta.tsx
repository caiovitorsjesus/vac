import { Plus } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { api } from '../api/cliente'
import type { Disciplina } from '../api/tipos'
import { classeBotaoPrimario, classeBotaoSecundario, hoje, lerNumero } from '../formulario'
import { useEnvio } from '../hooks/useEnvio'
import { Campo, ErroFormulario } from './campos'

/**
 * Formulário de falta, usado no modal do card e na seção de faltas do detalhe.
 * Sem `aoCancelar`, o botão Cancelar não aparece (formulário fixo na tela).
 */
export function FormularioFalta({ disciplina, aoConcluir, aoCancelar }: {
  disciplina: Disciplina
  aoConcluir: () => void
  aoCancelar?: () => void
}) {
  const [data, setData] = useState(hoje())
  const [horas, setHoras] = useState('')
  const [observacao, setObservacao] = useState('')
  const { formulario, erros, erroGeral, enviando, executar } = useEnvio(aoConcluir)

  function salvar(e: FormEvent) {
    e.preventDefault()
    executar(
      () => api.registrarFalta(disciplina.id, {
        data,
        horas: lerNumero(horas) as number,
        observacao: observacao || undefined,
      }),
      'Falta registrada',
    )
  }

  return (
    <form ref={formulario} onSubmit={salvar} noValidate className="space-y-4">
      <ErroFormulario mensagem={erroGeral} />
      <div className="grid gap-4 sm:grid-cols-2">
        <Campo id="falta-data" rotulo="Data" type="date" max={hoje()} value={data}
          onChange={(e) => setData(e.target.value)} erro={erros.data} />
        <Campo id="falta-horas" rotulo="Horas" inputMode="decimal" value={horas} placeholder="1,5"
          onChange={(e) => setHoras(e.target.value)} erro={erros.horas} ajuda="Aceita frações, como 1,5." />
      </div>
      <Campo id="falta-observacao" rotulo="Observação (opcional)" value={observacao} placeholder="Consulta médica"
        onChange={(e) => setObservacao(e.target.value)} erro={erros.observacao} />
      <div className="flex justify-end gap-3">
        {aoCancelar && <button type="button" onClick={aoCancelar} className={classeBotaoSecundario}>Cancelar</button>}
        <button type="submit" disabled={enviando} className={classeBotaoPrimario}>
          <Plus className="size-4" aria-hidden /> {enviando ? 'Registrando...' : 'Registrar falta'}
        </button>
      </div>
    </form>
  )
}
