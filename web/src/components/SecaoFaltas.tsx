import { CalendarX } from 'lucide-react'
import { useState } from 'react'
import type { Disciplina, Falta, SituacaoFaltas } from '../api/tipos'
import { data, horas } from '../formatar'
import { FormularioFalta } from './FormularioFalta'

/** Seção de faltas do detalhe: formulário fixo, lista por data e os totais calculados pela API. */
export function SecaoFaltas({ disciplina, faltas, situacao, aoAlterar }: {
  disciplina: Disciplina
  faltas: Falta[]
  situacao: SituacaoFaltas
  aoAlterar: () => void
}) {
  // muda a key do formulário depois de registrar: ele volta vazio para a próxima falta
  const [versao, setVersao] = useState(0)

  return (
    <section aria-labelledby="titulo-faltas" className="rounded-2xl border border-border bg-card p-5 shadow-card md:p-6">
      <h2 id="titulo-faltas" className="mb-4 text-xl font-bold">Faltas</h2>

      <div className="rounded-xl bg-muted p-4">
        <FormularioFalta
          key={versao}
          disciplina={disciplina}
          aoConcluir={() => {
            setVersao((v) => v + 1)
            aoAlterar()
          }}
        />
      </div>

      {faltas.length === 0 ? (
        <p className="flex flex-col items-center gap-2 py-8 text-center text-sm text-muted-foreground">
          <CalendarX className="size-6" aria-hidden />
          Nenhuma falta registrada. Continue assim!
        </p>
      ) : (
        <ul className="mt-4 divide-y divide-border">
          {faltas.map((falta) => (
            <li key={falta.id} className="flex items-center gap-3 py-3">
              <span className="w-24 shrink-0 text-sm font-medium tabular-nums">{data(falta.data)}</span>
              <span className="min-w-0 flex-1 text-sm text-muted-foreground [overflow-wrap:anywhere]">
                {falta.observacao ?? <><span aria-hidden>—</span><span className="sr-only">sem observação</span></>}
              </span>
              <span className="shrink-0 font-semibold tabular-nums">{horas(falta.horas)}</span>
            </li>
          ))}
        </ul>
      )}

      <dl className="mt-4 grid grid-cols-3 gap-2 rounded-xl border border-border p-3 text-center">
        <Total rotulo="Faltadas" valor={horas(situacao.horasFaltadas)} />
        <Total rotulo="Limite" valor={horas(situacao.limiteHoras)} />
        <Total rotulo="Restantes" valor={horas(situacao.horasRestantes)} />
      </dl>
    </section>
  )
}

function Total({ rotulo, valor }: { rotulo: string; valor: string }) {
  return (
    <div>
      <dt className="text-xs text-muted-foreground">{rotulo}</dt>
      <dd className="font-bold tabular-nums">{valor}</dd>
    </div>
  )
}
