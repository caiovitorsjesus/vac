import { ArrowLeft, SearchX } from 'lucide-react'
import { Link } from 'react-router'
import { classeBotaoPrimario } from '../formulario'

/** Página para rotas inexistentes e para disciplinas que não existem (ou foram excluídas). */
export function NaoEncontrada({ titulo = 'Página não encontrada', texto = 'O endereço acessado não existe.' }: {
  titulo?: string
  texto?: string
}) {
  return (
    <div className="flex flex-col items-center rounded-2xl border border-border bg-card px-6 py-16 text-center">
      <div className="mb-6 grid size-20 place-items-center rounded-full bg-secondary">
        <SearchX className="size-9 text-primary" aria-hidden />
      </div>
      <h1 className="text-2xl font-bold">{titulo}</h1>
      <p className="mb-6 mt-2 max-w-sm text-muted-foreground">{texto}</p>
      <Link to="/" className={classeBotaoPrimario}>
        <ArrowLeft className="size-4" aria-hidden /> Voltar às disciplinas
      </Link>
    </div>
  )
}
