import { ArrowLeft, RotateCw, SlidersHorizontal } from 'lucide-react'
import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { ModalDisciplina } from '../components/ModalDisciplina'
import { ResumoFaltas, ResumoNotas } from '../components/ResumoDisciplina'
import { SecaoAvaliacoes } from '../components/SecaoAvaliacoes'
import { SecaoFaltas } from '../components/SecaoFaltas'
import { SeloStatus } from '../components/SeloStatus'
import { numero } from '../formatar'
import { classeBotaoSecundario } from '../formulario'
import { useDetalhe } from '../hooks/useDetalhe'
import { NaoEncontrada } from './NaoEncontrada'

/** Tela de uma disciplina: resumo da situação, faltas e avaliações. */
export function Detalhe() {
  const id = Number(useParams().id)
  // /disciplinas/abc nem chega a consultar a API
  if (!Number.isInteger(id) || id <= 0) return <DisciplinaNaoEncontrada />
  return <ConteudoDetalhe id={id} />
}

function ConteudoDetalhe({ id }: { id: number }) {
  const { estado, recarregar } = useDetalhe(id)
  const [regrasAbertas, setRegrasAbertas] = useState(false)
  const navegar = useNavigate()

  if (estado.tipo === 'nao-encontrada') return <DisciplinaNaoEncontrada />

  return (
    <>
      <Link to="/" className="mb-6 inline-flex items-center gap-1.5 rounded-md text-sm font-medium text-primary hover:underline">
        <ArrowLeft className="size-4" aria-hidden /> Disciplinas
      </Link>

      {estado.tipo === 'carregando' && (
        <div aria-busy="true" aria-label="Carregando disciplina" className="space-y-6">
          <div className="h-56 rounded-2xl border border-border bg-card motion-safe:animate-pulse" />
          <div className="grid gap-6 lg:grid-cols-2">
            <div className="h-96 rounded-2xl border border-border bg-card motion-safe:animate-pulse" />
            <div className="h-96 rounded-2xl border border-border bg-card motion-safe:animate-pulse" />
          </div>
        </div>
      )}

      {estado.tipo === 'erro' && (
        <div role="alert" className="flex flex-col items-center gap-4 rounded-2xl border border-border bg-card py-12 text-center">
          <p className="text-muted-foreground">{estado.mensagem}</p>
          <button type="button" onClick={recarregar} className={classeBotaoSecundario}>
            <RotateCw className="size-4" aria-hidden /> Tentar novamente
          </button>
        </div>
      )}

      {estado.tipo === 'pronto' && (() => {
        const { disciplina, status, faltas, avaliacoes } = estado.dados
        return (
          <>
            <section aria-labelledby="titulo-disciplina" className="mb-6 rounded-2xl border border-border bg-card p-5 shadow-card md:p-6">
              <div className="mb-6 flex flex-wrap items-center gap-3">
                <h1 id="titulo-disciplina" className="text-3xl font-extrabold tracking-tight">{disciplina.nome}</h1>
                <SeloStatus status={status.faltas.status} />
                <button type="button" onClick={() => setRegrasAbertas(true)} className={`${classeBotaoSecundario} ml-auto`}>
                  <SlidersHorizontal className="size-4" aria-hidden /> Regras
                </button>
              </div>
              <div className="grid gap-6 md:grid-cols-2">
                <ResumoFaltas faltas={status.faltas} />
                <ResumoNotas notas={status.notas} aviso={status.avisoNotas} disciplina={disciplina} />
              </div>
              <p className="mt-5 text-xs text-muted-foreground tabular-nums">
                {numero(disciplina.cargaHorariaTotal)} h de carga · até {numero(disciplina.percentualMaximoFaltas)}% de faltas ·
                média mínima {numero(disciplina.mediaMinima)} · nota máxima {numero(disciplina.notaMaxima)}
              </p>
            </section>

            <div className="grid items-start gap-6 lg:grid-cols-2">
              <SecaoFaltas disciplina={disciplina} faltas={faltas} situacao={status.faltas} aoAlterar={recarregar} />
              <SecaoAvaliacoes disciplina={disciplina} avaliacoes={avaliacoes} aoAlterar={recarregar} />
            </div>

            <ModalDisciplina
              aberto={regrasAbertas}
              disciplina={disciplina}
              aoFechar={() => setRegrasAbertas(false)}
              aoSalvar={recarregar}
              aoExcluir={() => navegar('/')}
            />
          </>
        )
      })()}
    </>
  )
}

function DisciplinaNaoEncontrada() {
  return (
    <NaoEncontrada
      titulo="Disciplina não encontrada"
      texto="Ela pode ter sido excluída, ou o endereço está incorreto."
    />
  )
}
