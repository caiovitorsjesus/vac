import { Sprout } from 'lucide-react'
import { useEffect, useRef, type ReactNode } from 'react'
import { Link, useLocation } from 'react-router'

/** Estrutura comum a todas as telas: cabeçalho com o logo, conteúdo e rodapé com o aviso sobre as regras. */
export function Layout({ children }: { children: ReactNode }) {
  const conteudo = useRef<HTMLElement>(null)
  const { pathname } = useLocation()
  const primeiraTela = useRef(true)

  // ao trocar de tela, leva o foco ao conteúdo: sem isso, leitores de tela não percebem a navegação
  useEffect(() => {
    if (primeiraTela.current) {
      primeiraTela.current = false
      return
    }
    conteudo.current?.focus()
  }, [pathname])

  return (
    <div className="flex min-h-dvh flex-col">
      <a
        href="#conteudo"
        className="sr-only z-50 rounded-lg bg-primary px-4 py-2 font-semibold text-primary-foreground focus:not-sr-only focus:fixed focus:left-4 focus:top-4"
      >
        Pular para o conteúdo
      </a>

      <header className="border-b border-border bg-card">
        <div className="mx-auto flex h-16 max-w-6xl items-center px-5">
          <Link to="/" className="flex items-center gap-2 rounded-xl text-primary">
            <span className="grid size-9 place-items-center rounded-xl bg-primary text-primary-foreground">
              <Sprout className="size-5" aria-hidden />
            </span>
            <span className="text-2xl font-extrabold tracking-tight">vac</span>
          </Link>
        </div>
      </header>

      <main id="conteudo" ref={conteudo} tabIndex={-1} className="mx-auto w-full max-w-6xl flex-1 px-5 py-8 outline-none md:py-12">
        {children}
      </main>

      <footer className="border-t border-border py-6 text-center text-xs text-muted-foreground">
        As regras são configuradas por você e não substituem o regulamento da sua instituição.
      </footer>
    </div>
  )
}
