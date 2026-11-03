import { X } from 'lucide-react'
import { useEffect, useRef, type ReactNode } from 'react'

/**
 * Janela modal sobre o elemento nativo <dialog>, que já prende o foco dentro da janela,
 * fecha com Esc e escurece o fundo.
 */
export function Modal({ aberto, titulo, aoFechar, children }: {
  aberto: boolean
  titulo: string
  aoFechar: () => void
  children: ReactNode
}) {
  const dialogo = useRef<HTMLDialogElement>(null)

  useEffect(() => {
    if (aberto) dialogo.current?.showModal()
    else dialogo.current?.close()
  }, [aberto])

  return (
    <dialog
      ref={dialogo}
      onClose={aoFechar}
      aria-labelledby="titulo-modal"
      className="m-auto w-full max-w-lg rounded-2xl border border-border bg-card p-0 text-foreground shadow-card backdrop:bg-foreground/40"
    >
      {aberto && (
        <div className="p-6">
          <div className="mb-5 flex items-center justify-between gap-3">
            <h2 id="titulo-modal" className="text-xl font-bold">{titulo}</h2>
            <button type="button" onClick={aoFechar} aria-label="Fechar" className="rounded-lg p-1 text-muted-foreground hover:bg-muted">
              <X className="size-5" aria-hidden />
            </button>
          </div>
          {children}
        </div>
      )}
    </dialog>
  )
}
