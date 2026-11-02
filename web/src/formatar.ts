// Formatação para exibição no padrão brasileiro (vírgula decimal). Nenhum cálculo de regra acontece aqui.

const numeroBr = (casas: number) => new Intl.NumberFormat('pt-BR', { maximumFractionDigits: casas })

/** 6 → "6", 1.5 → "1,5", 15.0000 → "15". */
export const numero = (valor: number, casas = 2) => numeroBr(casas).format(valor)

/** 6 → "6 h". */
export const horas = (valor: number) => `${numero(valor)} h`

/** 106.67 → "106,7%". */
export const percentual = (valor: number) => `${numero(valor, 1)}%`

/** "2026-10-20" → "20/10/2026". O meio-dia evita que o fuso horário mude o dia. */
export const data = (iso: string) => new Date(`${iso}T12:00:00`).toLocaleDateString('pt-BR')

/** 1 → "1 hora", 6 → "6 horas". */
export const horasPorExtenso = (valor: number) => `${numero(valor)} ${valor === 1 ? 'hora' : 'horas'}`
