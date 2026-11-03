import { Check, ClipboardList, Pencil, X } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { api } from '../api/cliente'
import type { Avaliacao, Disciplina } from '../api/tipos'
import { numero } from '../formatar'
import { classeBotaoPrimario, classeBotaoSecundario, lerNumero, paraCampo } from '../formulario'
import { useEnvio } from '../hooks/useEnvio'
import { FormularioAvaliacao } from './FormularioAvaliacao'

/** Seção de avaliações do detalhe: quanto do peso já foi cadastrado, formulário e lista com lançamento de nota. */
export function SecaoAvaliacoes({ disciplina, avaliacoes, aoAlterar }: {
  disciplina: Disciplina
  avaliacoes: Avaliacao[]
  aoAlterar: () => void
}) {
  const [versao, setVersao] = useState(0)
  // só exibição do que já foi cadastrado; a regra de não passar de 100% é validada pela API (409)
  const pesoCadastrado = avaliacoes.reduce((soma, a) => soma + a.pesoPercentual, 0)
  const completo = pesoCadastrado >= 100

  return (
    <section aria-labelledby="titulo-avaliacoes" className="rounded-2xl border border-border bg-card p-5 shadow-card md:p-6">
      <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
        <h2 id="titulo-avaliacoes" className="text-xl font-bold">Avaliações</h2>
        <span className={`rounded-full px-2.5 py-1 text-xs font-semibold tabular-nums ${completo ? 'bg-secondary text-primary' : 'bg-muted text-muted-foreground'}`}>
          {numero(pesoCadastrado)}% de 100% cadastrados
        </span>
      </div>
      <div
        role="progressbar"
        aria-valuenow={Math.round(pesoCadastrado)}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-label={`${numero(pesoCadastrado)}% do peso total cadastrado`}
        className="mb-4 h-2 overflow-hidden rounded-full bg-muted"
      >
        <div className="h-full rounded-full bg-primary" style={{ width: `${Math.min(100, pesoCadastrado)}%` }} />
      </div>

      {!completo && (
        <div className="rounded-xl bg-muted p-4">
          <FormularioAvaliacao
            key={versao}
            disciplina={disciplina}
            aoConcluir={() => {
              setVersao((v) => v + 1)
              aoAlterar()
            }}
          />
        </div>
      )}

      {avaliacoes.length === 0 ? (
        <p className="flex flex-col items-center gap-2 py-8 text-center text-sm text-muted-foreground">
          <ClipboardList className="size-6" aria-hidden />
          Nenhuma avaliação cadastrada. Cadastre as provas e trabalhos com seus pesos.
        </p>
      ) : (
        <ul className="mt-4 divide-y divide-border">
          {avaliacoes.map((avaliacao) => (
            <LinhaAvaliacao key={avaliacao.id} disciplina={disciplina} avaliacao={avaliacao} aoAlterar={aoAlterar} />
          ))}
        </ul>
      )}
    </section>
  )
}

/** Uma avaliação da lista. "Lançar nota" / "Corrigir" abrem a edição na própria linha. */
function LinhaAvaliacao({ disciplina, avaliacao, aoAlterar }: {
  disciplina: Disciplina
  avaliacao: Avaliacao
  aoAlterar: () => void
}) {
  const [editando, setEditando] = useState(false)
  const [nota, setNota] = useState('')
  const { formulario, erros, erroGeral, enviando, executar } = useEnvio(() => {
    setEditando(false)
    aoAlterar()
  })
  const pendente = avaliacao.nota === null
  const idCampo = `nota-${avaliacao.id}`
  const erro = erros.nota ?? erroGeral

  function abrir() {
    setNota(pendente ? '' : paraCampo(avaliacao.nota as number))
    setEditando(true)
  }

  function salvar(e: FormEvent) {
    e.preventDefault()
    executar(
      () => api.registrarNota(disciplina.id, avaliacao.id, lerNumero(nota) as number),
      pendente ? 'Nota lançada' : 'Nota corrigida',
    )
  }

  return (
    <li className="py-3">
      <div className="flex flex-wrap items-center gap-3">
        <span className="font-semibold">{avaliacao.nome}</span>
        <span className="text-sm text-muted-foreground tabular-nums">peso {numero(avaliacao.pesoPercentual)}%</span>
        <span className="ml-auto flex items-center gap-2">
          {pendente
            ? <span className="rounded-full bg-muted px-2.5 py-1 text-xs font-semibold text-muted-foreground">Pendente</span>
            : <span className="text-lg font-bold tabular-nums">{numero(avaliacao.nota as number)}</span>}
          {!editando && (
            <button type="button" onClick={abrir} aria-label={`${pendente ? 'Lançar nota' : 'Corrigir nota'} de ${avaliacao.nome}`}
              className="inline-flex min-h-9 items-center gap-1.5 rounded-lg px-2.5 text-sm font-medium text-primary transition-colors hover:bg-secondary">
              <Pencil className="size-4" aria-hidden /> {pendente ? 'Lançar nota' : 'Corrigir'}
            </button>
          )}
        </span>
      </div>

      {editando && (
        <form
          ref={formulario}
          onSubmit={salvar}
          noValidate
          onKeyDown={(e) => e.key === 'Escape' && setEditando(false)}
          className="mt-3 rounded-xl bg-muted p-3"
        >
          <label htmlFor={idCampo} className="text-sm font-medium">
            Nota de {avaliacao.nome} <span className="font-normal text-muted-foreground">(0 a {numero(disciplina.notaMaxima)})</span>
          </label>
          <div className="mt-1.5 flex gap-2">
            <input
              id={idCampo}
              inputMode="decimal"
              autoFocus
              value={nota}
              onChange={(e) => setNota(e.target.value)}
              aria-invalid={erro ? true : undefined}
              aria-describedby={erro ? `${idCampo}-erro` : undefined}
              className="min-h-11 w-28 rounded-xl border border-border bg-card px-3 tabular-nums outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 aria-invalid:border-over-bar"
            />
            <button type="submit" disabled={enviando} className={classeBotaoPrimario}>
              <Check className="size-4" aria-hidden /> {enviando ? 'Salvando...' : 'Salvar'}
            </button>
            <button type="button" onClick={() => setEditando(false)} className={classeBotaoSecundario} aria-label="Cancelar edição da nota">
              <X className="size-4" aria-hidden />
            </button>
          </div>
          {erro && <p id={`${idCampo}-erro`} role="alert" className="mt-1.5 text-xs text-over-foreground">{erro}</p>}
        </form>
      )}
    </li>
  )
}
