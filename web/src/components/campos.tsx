import type { InputHTMLAttributes, ReactNode } from 'react'

/** Campo de formulário com rótulo, texto de ajuda e mensagem de erro ligados ao input (acessível). */
export function Campo({ id, rotulo, erro, ajuda, ...input }: {
  id: string
  rotulo: string
  erro?: string
  ajuda?: ReactNode
} & InputHTMLAttributes<HTMLInputElement>) {
  const idDescricao = erro ? `${id}-erro` : ajuda ? `${id}-ajuda` : undefined
  return (
    <div className="space-y-1.5">
      <label htmlFor={id} className="text-sm font-medium">{rotulo}</label>
      <input
        id={id}
        aria-invalid={erro ? true : undefined}
        aria-describedby={idDescricao}
        className="min-h-11 w-full rounded-xl border border-border bg-card px-3 py-2 tabular-nums outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 aria-invalid:border-over-bar"
        {...input}
      />
      {erro
        ? <p id={`${id}-erro`} className="text-xs text-over-foreground">{erro}</p>
        : ajuda && <p id={`${id}-ajuda`} className="text-xs text-muted-foreground">{ajuda}</p>}
    </div>
  )
}

/** Mensagem de erro geral do formulário (o detail da API). */
export function ErroFormulario({ mensagem }: { mensagem: string | null }) {
  if (!mensagem) return null
  // tabIndex -1: recebe o foco depois de um envio com erro, sem entrar na ordem do Tab
  return <p role="alert" tabIndex={-1} className="rounded-xl bg-over px-3 py-2 text-sm text-over-foreground">{mensagem}</p>
}
