import { BookOpen, RotateCw } from 'lucide-react'
import { CardDisciplina } from '../components/CardDisciplina'
import { LegendaSelos } from '../components/LegendaSelos'
import { usePainel } from '../hooks/usePainel'

/** Tela inicial: um card por disciplina com a situação de faltas e notas. */
export function Painel() {
  const { estado, recarregar } = usePainel()

  return (
    <>
      <header className="mb-8">
        <h1 className="text-3xl font-extrabold tracking-tight md:text-4xl">Seu semestre em dia.</h1>
        <p className="mt-1 text-muted-foreground">Mais clareza nas faltas. Mais tranquilidade nas notas.</p>
      </header>

      {estado.tipo === 'carregando' && <Esqueleto />}

      {estado.tipo === 'erro' && (
        <div role="alert" className="flex flex-col items-center gap-4 rounded-2xl border border-border bg-card py-12 text-center">
          <p className="text-muted-foreground">{estado.mensagem}</p>
          <button
            type="button"
            onClick={recarregar}
            className="inline-flex items-center gap-2 rounded-xl border border-border px-4 py-2 text-sm font-semibold text-primary hover:bg-secondary"
          >
            <RotateCw className="size-4" aria-hidden /> Tentar novamente
          </button>
        </div>
      )}

      {estado.tipo === 'pronto' && estado.itens.length === 0 && (
        <div className="flex flex-col items-center rounded-2xl border border-border bg-card px-6 py-16 text-center">
          <div className="mb-6 grid size-24 place-items-center rounded-full bg-secondary">
            <BookOpen className="size-10 text-primary" aria-hidden />
          </div>
          <h2 className="text-xl font-bold">Nenhuma disciplina por aqui ainda</h2>
          <p className="mt-2 max-w-sm text-muted-foreground">
            Cadastre sua primeira matéria e o vac faz as contas de faltas e notas por você.
          </p>
        </div>
      )}

      {estado.tipo === 'pronto' && estado.itens.length > 0 && (
        <>
          <h2 className="mb-4 flex items-center gap-2 text-xl font-semibold">
            Suas disciplinas
            <span className="rounded-md bg-muted px-2 py-0.5 text-sm text-muted-foreground">{estado.itens.length}</span>
          </h2>
          <div className="grid gap-5 md:grid-cols-2">
            {estado.itens.map((item) => (
              <CardDisciplina key={item.disciplina.id} item={item} />
            ))}
          </div>
          <LegendaSelos />
        </>
      )}
    </>
  )
}

function Esqueleto() {
  return (
    <div className="grid gap-5 md:grid-cols-2" aria-busy="true" aria-label="Carregando disciplinas">
      {[0, 1].map((i) => (
        <div key={i} className="h-80 animate-pulse rounded-2xl border border-border bg-card" />
      ))}
    </div>
  )
}
