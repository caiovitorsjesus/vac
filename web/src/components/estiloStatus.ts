import { AlertTriangle, CircleAlert, CircleCheck, CircleX, type LucideIcon } from 'lucide-react'
import type { StatusFaltas } from '../api/tipos'

type Estilo = { rotulo: string; selo: string; barra: string; destaque: string; Icone: LucideIcon }

/** Rótulo, cores e ícone de cada status. A cor nunca aparece sozinha: o texto e o ícone vão junto. */
export const estiloDoStatus: Record<StatusFaltas, Estilo> = {
  SEGURO: { rotulo: 'Seguro', selo: 'bg-safe text-safe-foreground', barra: 'bg-safe-bar', destaque: 'text-safe-foreground', Icone: CircleCheck },
  ATENCAO: { rotulo: 'Atenção', selo: 'bg-warn text-warn-foreground', barra: 'bg-warn-bar', destaque: 'text-warn-foreground', Icone: AlertTriangle },
  CRITICO: { rotulo: 'Crítico', selo: 'bg-crit text-crit-foreground', barra: 'bg-crit-bar', destaque: 'text-crit-foreground', Icone: CircleAlert },
  REPROVADO_POR_FALTA: { rotulo: 'Acima do limite', selo: 'bg-over text-over-foreground', barra: 'bg-over-bar', destaque: 'text-over-foreground', Icone: CircleX },
}
