import type { StatusFaltas } from '../api/tipos'
import { SeloStatus } from './SeloStatus'

const faixas: [StatusFaltas, string][] = [
  ['SEGURO', 'Menos de 70%'],
  ['ATENCAO', 'De 70% a menos de 90%'],
  ['CRITICO', 'De 90% a 100%'],
  ['REPROVADO_POR_FALTA', 'Mais de 100%'],
]

/** Explica as faixas dos selos, para o usuário não precisar adivinhar o que cada um significa. */
export function LegendaSelos() {
  return (
    <section className="mt-10 space-y-3" aria-labelledby="legenda-selos">
      <h2 id="legenda-selos" className="text-sm font-semibold">
        Entenda os selos <span className="font-normal text-muted-foreground">· percentual do limite de faltas usado</span>
      </h2>
      <ul className="flex flex-wrap gap-x-6 gap-y-2">
        {faixas.map(([status, faixa]) => (
          <li key={status} className="flex items-center gap-2 text-xs text-muted-foreground">
            <SeloStatus status={status} />
            {faixa}
          </li>
        ))}
      </ul>
    </section>
  )
}
