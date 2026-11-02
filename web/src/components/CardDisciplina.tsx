import { CircleSlash, FilePenLine, Info, PartyPopper, Plus, SlidersHorizontal, Target } from 'lucide-react'
import type { Disciplina, ProjecaoNotas, SituacaoFaltas } from '../api/tipos'
import { horas, horasPorExtenso, numero } from '../formatar'
import { classeBotaoSecundario } from '../formulario'
import type { ItemPainel } from '../hooks/usePainel'
import { BarraFaltas } from './BarraFaltas'
import { estiloDoStatus } from './estiloStatus'
import { SeloStatus } from './SeloStatus'

/** Card de uma disciplina no painel. Só exibe o que o /status calculou; não há regra de cálculo aqui. */
export function CardDisciplina({ item, aoAbrirRegras, aoRegistrarFalta, aoNovaAvaliacao }: {
  item: ItemPainel
  aoAbrirRegras: () => void
  aoRegistrarFalta: () => void
  aoNovaAvaliacao: () => void
}) {
  const { disciplina, status } = item

  return (
    <article className="flex flex-col gap-5 rounded-2xl border border-border bg-card p-5 shadow-card md:p-6">
      <div className="flex items-start justify-between gap-3">
        <h2 className="text-lg font-semibold">{disciplina.nome}</h2>
        <button
          type="button"
          onClick={aoAbrirRegras}
          aria-label={`Regras de ${disciplina.nome}`}
          className="inline-flex shrink-0 items-center gap-1.5 rounded-lg px-2 py-1 text-sm text-muted-foreground hover:bg-muted hover:text-foreground"
        >
          <SlidersHorizontal className="size-4" aria-hidden /> Regras
        </button>
      </div>
      <ResumoFaltas faltas={status.faltas} />
      <ResumoNotas notas={status.notas} aviso={status.avisoNotas} disciplina={disciplina} />
      <div className="mt-auto grid grid-cols-2 gap-3">
        <button type="button" onClick={aoRegistrarFalta} className={classeBotaoSecundario}
          aria-label={`Registrar falta em ${disciplina.nome}`}>
          <Plus className="size-4" aria-hidden /> Registrar falta
        </button>
        <button type="button" onClick={aoNovaAvaliacao} className={classeBotaoSecundario}
          aria-label={`Nova avaliação em ${disciplina.nome}`}>
          <FilePenLine className="size-4" aria-hidden /> Avaliação
        </button>
      </div>
    </article>
  )
}

function ResumoFaltas({ faltas }: { faltas: SituacaoFaltas }) {
  const acimaDoLimite = faltas.status === 'REPROVADO_POR_FALTA'
  const valor = acimaDoLimite ? faltas.horasExcedentes : faltas.horasRestantes

  const frase = acimaDoLimite
    ? `Você ultrapassou o limite em ${horasPorExtenso(faltas.horasExcedentes)}.`
    : faltas.horasRestantes === 0
      ? 'Você não pode faltar mais.'
      : `Você ainda pode faltar ${horasPorExtenso(faltas.horasRestantes)}.`

  return (
    <div className="space-y-3">
      <div className="flex items-start justify-between gap-3">
        <p>
          <span className={`text-3xl font-semibold ${acimaDoLimite ? estiloDoStatus[faltas.status].destaque : ''}`}>
            {horas(valor)}
          </span>{' '}
          <span className="text-sm text-muted-foreground">{acimaDoLimite ? 'excedida' : 'restantes'}</span>
        </p>
        <SeloStatus status={faltas.status} />
      </div>
      <p className="text-sm text-muted-foreground">{frase}</p>
      <BarraFaltas faltas={faltas} />
    </div>
  )
}

function ResumoNotas({ notas, aviso, disciplina }: { notas: ProjecaoNotas | null; aviso: string | null; disciplina: Disciplina }) {
  if (notas === null) {
    return (
      <div className="flex gap-2.5 rounded-xl bg-muted p-4 text-sm">
        <Info className="mt-0.5 size-4 shrink-0 text-muted-foreground" aria-hidden />
        <p>
          {aviso} <span className="text-muted-foreground">Cadastre as avaliações até completar 100%.</span>
        </p>
      </div>
    )
  }

  return (
    <div className="space-y-3 rounded-xl bg-muted p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs text-muted-foreground">Pontos garantidos</p>
          <p>
            <span className="text-2xl font-semibold">{numero(notas.pontosGarantidos)}</span>{' '}
            <span className="text-sm text-muted-foreground">de {numero(disciplina.mediaMinima)}</span>
          </p>
        </div>
        <p className="text-right text-xs text-muted-foreground">
          Média mínima: {numero(disciplina.mediaMinima)}
          <br />
          {numero(notas.pesoConcluido)}% do peso avaliado
        </p>
      </div>
      <div className="border-t border-border pt-3">
        <MensagemNotas notas={notas} notaMaxima={disciplina.notaMaxima} />
      </div>
    </div>
  )
}

/** Escolhe a frase pelos dados já calculados pela API (nota necessária e viabilidade). */
function MensagemNotas({ notas, notaMaxima }: { notas: ProjecaoNotas; notaMaxima: number }) {
  if (notas.notaNecessaria === null) {
    return notas.viavel
      ? <Linha icone={<PartyPopper className="size-4 text-primary" aria-hidden />} texto="Média mínima atingida." />
      : <Linha icone={<CircleSlash className="size-4 text-over-foreground" aria-hidden />} texto="Avaliações concluídas sem atingir a média mínima." />
  }
  if (!notas.viavel) {
    return (
      <Linha
        icone={<CircleSlash className="size-4 text-over-foreground" aria-hidden />}
        texto={`Precisaria de ${numero(notas.notaNecessaria)} nas pendentes, acima da nota máxima ${numero(notaMaxima)}.`}
      />
    )
  }
  if (notas.notaNecessaria === 0) {
    return <Linha icone={<PartyPopper className="size-4 text-primary" aria-hidden />} texto="Média já garantida, mesmo tirando zero no que falta." />
  }
  return (
    <Linha
      icone={<Target className="size-4 text-primary" aria-hidden />}
      texto={<>Precisa de <strong>{numero(notas.notaNecessaria)}</strong> nas avaliações pendentes.</>}
    />
  )
}

function Linha({ icone, texto }: { icone: React.ReactNode; texto: React.ReactNode }) {
  return (
    <p className="flex items-center gap-2 text-sm font-medium">
      {icone}
      <span>{texto}</span>
    </p>
  )
}
