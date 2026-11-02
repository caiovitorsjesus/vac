import { Plus } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { api } from '../api/cliente'
import type { Disciplina } from '../api/tipos'
import { classeBotaoPrimario, classeBotaoSecundario, hoje, lerNumero } from '../formulario'
import { useEnvio } from '../hooks/useEnvio'
import { Campo, ErroFormulario } from './campos'
import { Modal } from './Modal'

/** Registro rápido de uma falta, aberto pelo botão do card. */
export function ModalFalta({ disciplina, aoFechar, aoSalvar }: {
  disciplina: Disciplina | null
  aoFechar: () => void
  aoSalvar: () => void
}) {
  return (
    <Modal aberto={disciplina !== null} titulo={`Registrar falta · ${disciplina?.nome ?? ''}`} aoFechar={aoFechar}>
      {disciplina && <Formulario key={disciplina.id} disciplina={disciplina} aoFechar={aoFechar} aoSalvar={aoSalvar} />}
    </Modal>
  )
}

function Formulario({ disciplina, aoFechar, aoSalvar }: { disciplina: Disciplina; aoFechar: () => void; aoSalvar: () => void }) {
  const [data, setData] = useState(hoje())
  const [horas, setHoras] = useState('')
  const [observacao, setObservacao] = useState('')
  const { formulario, erros, erroGeral, enviando, executar } = useEnvio(() => {
    aoSalvar()
    aoFechar()
  })

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
      <div className="flex justify-end gap-3 pt-2">
        <button type="button" onClick={aoFechar} className={classeBotaoSecundario}>Cancelar</button>
        <button type="submit" disabled={enviando} className={classeBotaoPrimario}>
          <Plus className="size-4" aria-hidden /> {enviando ? 'Registrando...' : 'Registrar falta'}
        </button>
      </div>
    </form>
  )
}
