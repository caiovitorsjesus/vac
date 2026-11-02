import { Trash2 } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { api } from '../api/cliente'
import type { Disciplina, DisciplinaForm } from '../api/tipos'
import { classeBotaoPrimario, classeBotaoSecundario, lerNumero, paraCampo } from '../formulario'
import { useEnvio } from '../hooks/useEnvio'
import { Campo, ErroFormulario } from './campos'
import { Modal } from './Modal'

type Campos = Record<keyof DisciplinaForm, string>

const vazio: Campos = { nome: '', cargaHorariaTotal: '', percentualMaximoFaltas: '', mediaMinima: '', notaMaxima: '10' }

const deDisciplina = (d: Disciplina): Campos => ({
  nome: d.nome,
  cargaHorariaTotal: paraCampo(d.cargaHorariaTotal),
  percentualMaximoFaltas: paraCampo(d.percentualMaximoFaltas),
  mediaMinima: paraCampo(d.mediaMinima),
  notaMaxima: paraCampo(d.notaMaxima),
})

/**
 * Cadastro (sem `disciplina`) ou edição das regras (com `disciplina`, incluindo a exclusão).
 * As regras são validadas pela API; o formulário exibe os erros que ela devolve.
 */
export function ModalDisciplina({ aberto, disciplina, aoFechar, aoSalvar }: {
  aberto: boolean
  disciplina?: Disciplina
  aoFechar: () => void
  /** Chamado depois de criar, editar ou excluir com sucesso, para a tela recarregar. */
  aoSalvar: () => void
}) {
  return (
    <Modal aberto={aberto} titulo={disciplina ? `Regras de ${disciplina.nome}` : 'Nova disciplina'} aoFechar={aoFechar}>
      {/* a key recria o formulário (e limpa o estado) a cada abertura ou troca de disciplina */}
      <Formulario key={`${aberto}-${disciplina?.id}`} disciplina={disciplina} aoFechar={aoFechar} aoSalvar={aoSalvar} />
    </Modal>
  )
}

function Formulario({ disciplina, aoFechar, aoSalvar }: {
  disciplina?: Disciplina
  aoFechar: () => void
  aoSalvar: () => void
}) {
  const [valores, setValores] = useState<Campos>(disciplina ? deDisciplina(disciplina) : vazio)
  const [confirmandoExclusao, setConfirmandoExclusao] = useState(false)
  const { formulario, erros, erroGeral, enviando, executar } = useEnvio(() => {
    aoSalvar()
    aoFechar()
  })

  const alterar = (campo: keyof Campos) => (e: { target: { value: string } }) =>
    setValores((v) => ({ ...v, [campo]: e.target.value }))

  function salvar(e: FormEvent) {
    e.preventDefault()
    // números inválidos ou vazios vão como null: a API responde qual campo está errado
    const dados = {
      nome: valores.nome,
      cargaHorariaTotal: lerNumero(valores.cargaHorariaTotal),
      percentualMaximoFaltas: lerNumero(valores.percentualMaximoFaltas),
      mediaMinima: lerNumero(valores.mediaMinima),
      notaMaxima: lerNumero(valores.notaMaxima),
    } as DisciplinaForm
    executar(
      () => (disciplina ? api.atualizarDisciplina(disciplina.id, dados) : api.criarDisciplina(dados)),
      disciplina ? 'Regras atualizadas' : 'Disciplina cadastrada',
    )
  }

  if (confirmandoExclusao && disciplina) {
    return (
      <div ref={formulario} className="space-y-5">
        <p>
          Excluir <strong>{disciplina.nome}</strong>? As faltas e avaliações dela também serão apagadas.
          Essa ação não pode ser desfeita.
        </p>
        <ErroFormulario mensagem={erroGeral} />
        <div className="flex justify-end gap-3">
          <button type="button" onClick={() => setConfirmandoExclusao(false)} className={classeBotaoSecundario}>Cancelar</button>
          <button
            type="button"
            disabled={enviando}
            onClick={() => executar(() => api.excluirDisciplina(disciplina.id), 'Disciplina excluída')}
            className="inline-flex items-center gap-2 rounded-xl bg-over-bar px-4 py-2.5 text-sm font-semibold text-card hover:opacity-90 disabled:opacity-60"
          >
            <Trash2 className="size-4" aria-hidden /> Excluir
          </button>
        </div>
      </div>
    )
  }

  return (
    <form ref={formulario} onSubmit={salvar} noValidate className="space-y-4">
      <ErroFormulario mensagem={erroGeral} />
      <Campo id="nome" rotulo="Nome" value={valores.nome} onChange={alterar('nome')} erro={erros.nome} placeholder="Cálculo I" />
      <div className="grid gap-4 sm:grid-cols-2">
        <Campo id="carga" rotulo="Carga horária (h)" inputMode="decimal" value={valores.cargaHorariaTotal}
          onChange={alterar('cargaHorariaTotal')} erro={erros.cargaHorariaTotal} placeholder="60" />
        <Campo id="percentual" rotulo="Faltas permitidas (%)" inputMode="decimal" value={valores.percentualMaximoFaltas}
          onChange={alterar('percentualMaximoFaltas')} erro={erros.percentualMaximoFaltas} placeholder="25"
          ajuda="Confira no regulamento da sua instituição." />
        <Campo id="media" rotulo="Média mínima" inputMode="decimal" value={valores.mediaMinima}
          onChange={alterar('mediaMinima')} erro={erros.mediaMinima} placeholder="7" />
        <Campo id="nota-maxima" rotulo="Nota máxima" inputMode="decimal" value={valores.notaMaxima}
          onChange={alterar('notaMaxima')} erro={erros.notaMaxima} placeholder="10" />
      </div>
      <div className="flex items-center gap-3 pt-2">
        {disciplina && (
          <button type="button" onClick={() => setConfirmandoExclusao(true)}
            className="inline-flex items-center gap-1.5 text-sm font-medium text-over-foreground hover:underline">
            <Trash2 className="size-4" aria-hidden /> Excluir disciplina
          </button>
        )}
        <div className="ml-auto flex gap-3">
          <button type="button" onClick={aoFechar} className={classeBotaoSecundario}>Cancelar</button>
          <button type="submit" disabled={enviando} className={classeBotaoPrimario}>
            {enviando ? 'Salvando...' : disciplina ? 'Salvar regras' : 'Cadastrar'}
          </button>
        </div>
      </div>
    </form>
  )
}
