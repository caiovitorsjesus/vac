import { BookOpen, Plus, RotateCw } from 'lucide-react'
import { useState } from 'react'
import type { Disciplina } from '../api/tipos'
import { CardDisciplina } from '../components/CardDisciplina'
import { LegendaSelos } from '../components/LegendaSelos'
import { ModalAvaliacao } from '../components/ModalAvaliacao'
import { ModalDisciplina } from '../components/ModalDisciplina'
import { ModalFalta } from '../components/ModalFalta'
import { classeBotaoPrimario, classeBotaoSecundario } from '../formulario'
import { usePainel } from '../hooks/usePainel'

/** Qual modal está aberto, e para qual disciplina. */
type ModalAberto =
  | { tipo: 'nova' }
  | { tipo: 'regras' | 'falta' | 'avaliacao'; disciplina: Disciplina }
  | null

/** Tela inicial: um card por disciplina com a situação de faltas e notas. */
export function Painel() {
  const { estado, recarregar } = usePainel()
  const [modal, setModal] = useState<ModalAberto>(null)

  const botaoNova = (
    <button type="button" onClick={() => setModal({ tipo: 'nova' })} className={classeBotaoPrimario}>
      <Plus className="size-4" aria-hidden /> Nova disciplina
    </button>
  )

  return (
    <>
      <header className="mb-8 flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight md:text-4xl">Seu semestre em dia.</h1>
          <p className="mt-1 text-muted-foreground">Mais clareza nas faltas. Mais tranquilidade nas notas.</p>
        </div>
        {botaoNova}
      </header>

      {estado.tipo === 'carregando' && <Esqueleto />}

      {estado.tipo === 'erro' && (
        <div role="alert" className="flex flex-col items-center gap-4 rounded-2xl border border-border bg-card py-12 text-center">
          <p className="text-muted-foreground">{estado.mensagem}</p>
          <button type="button" onClick={recarregar} className={classeBotaoSecundario}>
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
          <p className="mb-6 mt-2 max-w-sm text-muted-foreground">
            Cadastre sua primeira matéria e o vac faz as contas de faltas e notas por você.
          </p>
          {botaoNova}
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
              <CardDisciplina
                key={item.disciplina.id}
                item={item}
                aoAbrirRegras={() => setModal({ tipo: 'regras', disciplina: item.disciplina })}
                aoRegistrarFalta={() => setModal({ tipo: 'falta', disciplina: item.disciplina })}
                aoNovaAvaliacao={() => setModal({ tipo: 'avaliacao', disciplina: item.disciplina })}
              />
            ))}
          </div>
          <LegendaSelos />
        </>
      )}

      <ModalDisciplina
        aberto={modal?.tipo === 'nova' || modal?.tipo === 'regras'}
        disciplina={modal?.tipo === 'regras' ? modal.disciplina : undefined}
        aoFechar={() => setModal(null)}
        aoSalvar={recarregar}
      />
      <ModalFalta
        disciplina={modal?.tipo === 'falta' ? modal.disciplina : null}
        aoFechar={() => setModal(null)}
        aoSalvar={recarregar}
      />
      <ModalAvaliacao
        disciplina={modal?.tipo === 'avaliacao' ? modal.disciplina : null}
        aoFechar={() => setModal(null)}
        aoSalvar={recarregar}
      />
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
