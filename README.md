# vac · Monitor de faltas e notas

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apachemaven)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5)
![Status](https://img.shields.io/badge/status-em%20constru%C3%A7%C3%A3o-yellow)

> Quantas horas ainda posso faltar? Quanto preciso tirar na próxima prova para passar?

O **vac** responde essas perguntas. O estudante cadastra suas disciplinas, faltas e avaliações, e o sistema calcula a situação de cada matéria de forma transparente, sem conta de cabeça nem planilha.

O vac é um organizador e simulador: as regras (limite de faltas, média mínima, nota máxima) são configuradas pelo próprio estudante e não substituem o regulamento da instituição.

---

## ✨ Funcionalidades

- **Frequência:** limite de faltas em horas, horas restantes, quanto do limite já foi usado e status de risco
- **Notas:** pontos já garantidos, peso restante e a nota necessária nas avaliações pendentes para atingir a média
- **Viabilidade:** distingue "ainda dá para passar" de "a nota necessária passa da nota máxima"
- **Validação no domínio:** dados inválidos (carga horária zero, nota acima da máxima, falta em data futura) são rejeitados na criação
- **API REST:** cadastro de disciplinas, faltas e avaliações, lançamento de notas e um status consolidado, com erros padronizados

## 📐 Regras de cálculo

### Faltas

```
limiteHoras         = cargaHorariaTotal × percentualMaximoFaltas / 100
horasRestantes      = max(limiteHoras − horasFaltadas, 0)
percentualConsumido = horasFaltadas × 100 / limiteHoras
```

| Limite consumido | Status |
|---|---|
| menos de 70% | 🟢 `SEGURO` |
| de 70% até menos de 90% | 🟡 `ATENCAO` |
| de 90% até 100% | 🟠 `CRITICO` |
| acima do limite | 🔴 `REPROVADO_POR_FALTA` |

Atingir exatamente o limite ainda é `CRITICO`; a reprovação só acontece quando ele é ultrapassado.

### Notas

```
pontosGarantidos = soma(nota × peso / 100)   das avaliações corrigidas
pesoRestante     = 100 − soma(peso)           das avaliações corrigidas
notaNecessaria   = (mediaMinima − pontosGarantidos) × 100 / pesoRestante
```

**Exemplo:** P1 vale 40% e a nota foi 5,0; P2 vale 60% e está pendente; a média mínima é 7,0.
→ 2,0 pontos garantidos e **8,34** necessários na P2.

Os pesos das avaliações de uma disciplina precisam somar 100%.

<details>
<summary><b>Minha instituição calcula por unidades. Funciona?</b></summary>

Sim, convertendo para pesos. Exemplo: duas unidades, cada uma com duas provas de 5 pontos, e média final = (U1 + U2) / 2.

- Cada prova vale 50% da unidade e cada unidade vale 50% da final → **cada prova tem peso 25%**
- A nota vai para a escala de 0 a 10 → tirar 3 de 5 equivale a **6,0**

| Avaliação | Peso | Nota |
|---|---|---|
| U1 · P1 (3 de 5) | 25% | 6,0 |
| U1 · P2 (4 de 5) | 25% | 8,0 |
| U2 · P1 (3 de 5) | 25% | 6,0 |
| U2 · P2 (3 de 5) | 25% | 6,0 |

Média final: **6,5**, igual a (7 + 6) / 2. Modelos de cálculo por instituição estão no roadmap.

</details>

## 🧠 Decisões de projeto

| Decisão | Por quê |
|---|---|
| `BigDecimal` em todos os valores | `double` não representa decimais com exatidão (0,1 + 0,2 ≠ 0,3) |
| Nota necessária arredondada **para cima** | é um mínimo: com 8,33 a média final seria 6,998 e o aluno não passaria |
| Status decidido pelo valor **exato** | 69,996% é `SEGURO`, mesmo sendo exibido como 70,00% |
| Horas como unidade de falta | encontros podem ter durações diferentes |
| Objetos de domínio imutáveis e validados no construtor | se o objeto existe, ele é válido, e as calculadoras não precisam revalidar |
| Domínio sem dependência de framework | as regras são testadas sem servidor nem banco |
| Entidades JPA separadas do domínio | o JPA exige construtor vazio e campos mutáveis; o domínio continua imutável, e o dado lido do banco é revalidado |
| Validação em duas camadas | os DTOs checam presença e formato; as regras de negócio ficam só no domínio |
| Erros no padrão `ProblemDetail` (RFC 9457) | respostas de erro consistentes: 400, 404 e 409, sem stack trace |

O raciocínio completo de cada etapa está nos [pull requests](https://github.com/caiovitorsjesus/vac/pulls?q=is%3Apr+is%3Amerged).

## 🛠️ Tecnologias

- **Java 21** (records, streams)
- **Spring Boot 4.1** (Spring MVC, Bean Validation)
- **Spring Data JPA** com **H2** em memória
- **Maven**
- **JUnit 5**

## ▶️ Como executar

Pré-requisitos: JDK 21+ e Maven 3.9+.

```bash
git clone https://github.com/caiovitorsjesus/vac.git
cd vac
mvn test              # roda os testes
mvn spring-boot:run   # sobe a API em http://localhost:8080
```

Os testes cobrem as validações do domínio e as calculadoras, incluindo os valores de fronteira de cada regra: limite exato de faltas, faixas de 70% e 90%, nota zero versus avaliação pendente e nota necessária igual à máxima.

O banco é H2 em memória: os dados são apagados ao reiniciar. Para ver as tabelas, acesse `http://localhost:8080/h2-console` com a JDBC URL `jdbc:h2:mem:vac`, usuário `sa` e senha em branco.

## 🔌 API

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/disciplinas` | cadastra uma disciplina |
| `GET` | `/api/disciplinas` | lista as disciplinas |
| `GET` | `/api/disciplinas/{id}` | consulta uma disciplina |
| `POST` | `/api/disciplinas/{id}/faltas` | registra uma falta |
| `GET` | `/api/disciplinas/{id}/faltas` | lista as faltas |
| `POST` | `/api/disciplinas/{id}/avaliacoes` | cadastra uma avaliação, com ou sem nota |
| `GET` | `/api/disciplinas/{id}/avaliacoes` | lista as avaliações |
| `PATCH` | `/api/disciplinas/{id}/avaliacoes/{avaliacaoId}/nota` | registra ou corrige uma nota |
| `GET` | `/api/disciplinas/{id}/status` | situação de faltas e projeção de notas |

Erros seguem o formato `ProblemDetail`: **400** para dado inválido (com a lista de `campos` quando o formato está errado), **404** para recurso inexistente e **409** quando os pesos passariam de 100%.

<details>
<summary><b>Exemplo completo com curl</b></summary>

```bash
H='Content-Type: application/json'; U=localhost:8080/api/disciplinas

curl -X POST $U -H "$H" -d '{"nome":"Cálculo I","cargaHorariaTotal":60,"percentualMaximoFaltas":25,"mediaMinima":7,"notaMaxima":10}'
curl -X POST $U/1/faltas -H "$H" -d '{"data":"2026-10-20","horas":9}'
curl -X POST $U/1/avaliacoes -H "$H" -d '{"nome":"P1","pesoPercentual":40,"nota":5}'
curl -X POST $U/1/avaliacoes -H "$H" -d '{"nome":"P2","pesoPercentual":60}'
curl $U/1/status
```

Resposta (resumida):

```json
{
  "disciplinaId": 1,
  "nome": "Cálculo I",
  "faltas": { "horasFaltadas": 9, "limiteHoras": 15, "horasRestantes": 6, "percentualConsumido": 60.00, "status": "SEGURO" },
  "notas": { "pontosGarantidos": 2, "pesoRestante": 60, "notaNecessaria": 8.34, "viavel": true },
  "avisoNotas": null
}
```

Enquanto os pesos das avaliações não somam 100%, `notas` vem nulo e `avisoNotas` explica o motivo; a parte de faltas continua disponível.

</details>

## 📁 Estrutura

```
src/main/java/com/caiovitor/vac
├── api/              controllers, DTOs e tratamento de erros (HTTP)
├── application/      services: orquestram o fluxo
├── domain/           regras de negócio, sem dependência de framework
│   ├── model/        Disciplina, RegistroFalta, Avaliacao
│   ├── calculo/      CalculadoraFaltas, CalculadoraNotas e seus resultados
│   └── exception/    exceções de domínio
└── infrastructure/   entidades JPA e repositórios
```

As dependências apontam para dentro: `domain` não conhece nenhuma outra camada.

## 🗺️ Roadmap

- [x] Modelo de domínio com validações
- [x] Cálculo de faltas
- [x] Projeção de notas
- [x] API REST com Spring Boot
- [ ] Persistência com PostgreSQL
- [ ] Testes de integração da API
- [ ] Interface web
- [ ] Modelos de cálculo por instituição (unidades, recuperação, exame final)

---

Feito por [Caio Vitor](https://github.com/caiovitorsjesus) como projeto de portfólio para praticar desenvolvimento full stack, do domínio ao deploy.

Desenvolvido com apoio de IA como par de programação. As decisões de arquitetura e de regras foram tomadas por mim, e cada etapa foi revisada e documentada.
