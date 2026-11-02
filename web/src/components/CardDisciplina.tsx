import { ChevronRight, FilePenLine, Plus, SlidersHorizontal } from 'lucide-react'
import { Link } from 'react-router'
import { classeBotaoSecundario } from '../formulario'
import type { ItemPainel } from '../hooks/usePainel'
import { ResumoFaltas, ResumoNotas } from './ResumoDisciplina'

/** Card de uma disciplina no painel. Só exibe o que o /status calculou; não há regra de cálculo aqui. */
export function CardDisciplina({ item, aoAbrirRegras, aoRegistrarFalta, aoNovaAvaliacao }: {
  item: ItemPainel
  aoAbrirRegras: () => void
  aoRegistrarFalta: () => void
  aoNovaAvaliacao: () => void
}) {
  const { disciplina, status } = item

  return (
    <article className="flex flex-col gap-5 rounded-2xl border border-border bg-card p-5 shadow-card md:p-6">
      <div className="flex items-start justify-between gap-3">
        <h3 className="text-lg font-semibold">
          <Link to={`/disciplinas/${disciplina.id}`} className="group inline-flex items-center gap-1 rounded-md hover:text-primary">
            {disciplina.nome}
            <ChevronRight className="size-4 text-muted-foreground transition-transform group-hover:translate-x-0.5" aria-hidden />
          </Link>
        </h3>
        <button
          type="button"
          onClick={aoAbrirRegras}
          aria-label={`Regras de ${disciplina.nome}`}
          className="inline-flex min-h-9 shrink-0 items-center gap-1.5 rounded-lg px-2.5 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
        >
          <SlidersHorizontal className="size-4" aria-hidden /> Regras
        </button>
      </div>
      <ResumoFaltas faltas={status.faltas} />
      <ResumoNotas notas={status.notas} aviso={status.avisoNotas} disciplina={disciplina} />
      <div className="mt-auto grid grid-cols-2 gap-3">
        <button type="button" onClick={aoRegistrarFalta} className={classeBotaoSecundario}
          aria-label={`Registrar falta em ${disciplina.nome}`}>
          <Plus className="size-4" aria-hidden /> Registrar falta
        </button>
        <button type="button" onClick={aoNovaAvaliacao} className={classeBotaoSecundario}
          aria-label={`Nova avaliação em ${disciplina.nome}`}>
          <FilePenLine className="size-4" aria-hidden /> Avaliação
        </button>
      </div>
    </article>
  )
}
