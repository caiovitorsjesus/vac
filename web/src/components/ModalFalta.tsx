import type { Disciplina } from '../api/tipos'
import { FormularioFalta } from './FormularioFalta'
import { Modal } from './Modal'

/** Registro rápido de uma falta, aberto pelo botão do card. */
export function ModalFalta({ disciplina, aoFechar, aoSalvar }: {
  disciplina: Disciplina | null
  aoFechar: () => void
  aoSalvar: () => void
}) {
  return (
    <Modal aberto={disciplina !== null} titulo={`Registrar falta · ${disciplina?.nome ?? ''}`} aoFechar={aoFechar}>
      {disciplina && (
        <FormularioFalta
          key={disciplina.id}
          disciplina={disciplina}
          aoConcluir={() => {
            aoSalvar()
            aoFechar()
          }}
          aoCancelar={aoFechar}
        />
      )}
    </Modal>
  )
}
