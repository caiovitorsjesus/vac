import type { Disciplina } from '../api/tipos'
import { FormularioAvaliacao } from './FormularioAvaliacao'
import { Modal } from './Modal'

/** Cadastro rápido de uma avaliação, com ou sem nota, aberto pelo botão do card. */
export function ModalAvaliacao({ disciplina, aoFechar, aoSalvar }: {
  disciplina: Disciplina | null
  aoFechar: () => void
  aoSalvar: () => void
}) {
  return (
    <Modal aberto={disciplina !== null} titulo={`Nova avaliação · ${disciplina?.nome ?? ''}`} aoFechar={aoFechar}>
      {disciplina && (
        <FormularioAvaliacao
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
