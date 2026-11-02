import type { StatusFaltas } from '../api/tipos'
import { estiloDoStatus } from './estiloStatus'

/** Selo do status de faltas: ícone, texto e cor juntos. */
export function SeloStatus({ status }: { status: StatusFaltas }) {
  const { rotulo, selo, Icone } = estiloDoStatus[status]
  return (
    <span className={`inline-flex shrink-0 items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-semibold ${selo}`}>
      <Icone className="size-3.5" aria-hidden />
      {rotulo}
    </span>
  )
}
