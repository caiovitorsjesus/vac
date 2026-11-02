import { Sprout } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link } from 'react-router'

/** Estrutura comum a todas as telas: cabeçalho com o logo, conteúdo e rodapé com o aviso sobre as regras. */
export function Layout({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b border-border bg-card">
        <div className="mx-auto flex h-16 max-w-6xl items-center px-5">
          <Link to="/" className="flex items-center gap-2 text-primary">
            <span className="grid size-9 place-items-center rounded-xl bg-primary text-primary-foreground">
              <Sprout className="size-5" aria-hidden />
            </span>
            <span className="text-2xl font-extrabold tracking-tight">vac</span>
          </Link>
        </div>
      </header>

      <main className="mx-auto w-full max-w-6xl flex-1 px-5 py-8 md:py-12">{children}</main>

      <footer className="border-t border-border py-6 text-center text-xs text-muted-foreground">
        As regras são configuradas por você e não substituem o regulamento da sua instituição.
      </footer>
    </div>
  )
}
