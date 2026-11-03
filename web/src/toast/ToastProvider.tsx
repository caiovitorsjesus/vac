import { CircleCheck } from 'lucide-react'
import { useCallback, useRef, useState, type ReactNode } from 'react'
import { ToastContexto } from './contexto'

const DURACAO_MS = 3500

/** Exibe uma mensagem no canto da tela por alguns segundos. Uma nova mensagem substitui a anterior. */
export function ToastProvider({ children }: { children: ReactNode }) {
  const [mensagem, setMensagem] = useState<string | null>(null)
  const temporizador = useRef<ReturnType<typeof setTimeout>>(undefined)

  const mostrar = useCallback((texto: string) => {
    clearTimeout(temporizador.current)
    setMensagem(texto)
    temporizador.current = setTimeout(() => setMensagem(null), DURACAO_MS)
  }, [])

  return (
    <ToastContexto.Provider value={mostrar}>
      {children}
      {/* role="status": leitores de tela anunciam a mensagem sem roubar o foco */}
      <div role="status" aria-live="polite" className="fixed inset-x-0 bottom-6 z-50 flex justify-center px-4">
        {mensagem && (
          <p className="flex items-center gap-2 rounded-xl bg-foreground px-4 py-3 text-sm font-medium text-card shadow-card">
            <CircleCheck className="size-4 text-safe-bar" aria-hidden />
            {mensagem}
          </p>
        )}
      </div>
    </ToastContexto.Provider>
  )
}
