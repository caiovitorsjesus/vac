import type { SituacaoFaltas } from '../api/tipos'
import { horas, percentual } from '../formatar'
import { estiloDoStatus } from './estiloStatus'

/** Barra do limite de faltas consumido, com o texto "9 h usadas de 15 h" e o percentual. */
export function BarraFaltas({ faltas }: { faltas: SituacaoFaltas }) {
  const { barra, destaque } = estiloDoStatus[faltas.status]
  // acima do limite, a barra fica cheia; o percentual real (ex.: 106,7%) aparece no texto
  const largura = Math.min(100, faltas.percentualConsumido)

  return (
    <div className="space-y-1.5">
      <div className="flex items-baseline justify-between text-xs">
        <span className="text-muted-foreground">
          {horas(faltas.horasFaltadas)} usadas de {horas(faltas.limiteHoras)}
        </span>
        <span className={`font-semibold ${destaque}`}>{percentual(faltas.percentualConsumido)}</span>
      </div>
      <div
        role="progressbar"
        aria-valuenow={Math.round(faltas.percentualConsumido)}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-label={`${percentual(faltas.percentualConsumido)} do limite de faltas usado`}
        className="h-2 w-full overflow-hidden rounded-full bg-muted"
      >
        <div className={`h-full rounded-full ${barra}`} style={{ width: `${largura}%` }} />
      </div>
    </div>
  )
}
