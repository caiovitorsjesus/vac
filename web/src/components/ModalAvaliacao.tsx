import { useState, type FormEvent } from 'react'
import { api } from '../api/cliente'
import type { Disciplina } from '../api/tipos'
import { numero } from '../formatar'
import { classeBotaoPrimario, classeBotaoSecundario, lerNumero } from '../formulario'
import { useEnvio } from '../hooks/useEnvio'
import { Campo, ErroFormulario } from './campos'
import { Modal } from './Modal'

/** Cadastro rápido de uma avaliação, com ou sem nota, aberto pelo botão do card. */
export function ModalAvaliacao({ disciplina, aoFechar, aoSalvar }: {
  disciplina: Disciplina | null
  aoFechar: () => void
  aoSalvar: () => void
}) {
  return (
    <Modal aberto={disciplina !== null} titulo={`Nova avaliação · ${disciplina?.nome ?? ''}`} aoFechar={aoFechar}>
      {disciplina && <Formulario key={disciplina.id} disciplina={disciplina} aoFechar={aoFechar} aoSalvar={aoSalvar} />}
    </Modal>
  )
}

function Formulario({ disciplina, aoFechar, aoSalvar }: { disciplina: Disciplina; aoFechar: () => void; aoSalvar: () => void }) {
  const [nome, setNome] = useState('')
  const [peso, setPeso] = useState('')
  const [nota, setNota] = useState('')
  const { erros, erroGeral, enviando, executar } = useEnvio(() => {
    aoSalvar()
    aoFechar()
  })

  function salvar(e: FormEvent) {
    e.preventDefault()
    executar(
      () => api.criarAvaliacao(disciplina.id, {
        nome,
        pesoPercentual: lerNumero(peso) as number,
        // nota em branco: a avaliação nasce pendente
        nota: lerNumero(nota) ?? undefined,
      }),
      'Avaliação cadastrada',
    )
  }

  return (
    <form onSubmit={salvar} noValidate className="space-y-4">
      <ErroFormulario mensagem={erroGeral} />
      <Campo id="avaliacao-nome" rotulo="Nome" value={nome} placeholder="P1"
        onChange={(e) => setNome(e.target.value)} erro={erros.nome} />
      <div className="grid gap-4 sm:grid-cols-2">
        <Campo id="avaliacao-peso" rotulo="Peso na média (%)" inputMode="decimal" value={peso} placeholder="40"
          onChange={(e) => setPeso(e.target.value)} erro={erros.pesoPercentual}
          ajuda="Os pesos de todas as avaliações somam 100%." />
        <Campo id="avaliacao-nota" rotulo="Nota (opcional)" inputMode="decimal" value={nota}
          placeholder={`0 a ${numero(disciplina.notaMaxima)}`}
          onChange={(e) => setNota(e.target.value)} erro={erros.nota} ajuda="Deixe em branco se ainda não saiu." />
      </div>
      <div className="flex justify-end gap-3 pt-2">
        <button type="button" onClick={aoFechar} className={classeBotaoSecundario}>Cancelar</button>
        <button type="submit" disabled={enviando} className={classeBotaoPrimario}>
          {enviando ? 'Cadastrando...' : 'Cadastrar avaliação'}
        </button>
      </div>
    </form>
  )
}
