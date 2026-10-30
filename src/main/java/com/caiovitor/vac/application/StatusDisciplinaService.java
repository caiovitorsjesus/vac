package com.caiovitor.vac.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caiovitor.vac.api.dto.ProjecaoNotasResponse;
import com.caiovitor.vac.api.dto.SituacaoFaltasResponse;
import com.caiovitor.vac.api.dto.StatusDisciplinaResponse;
import com.caiovitor.vac.application.exception.RecursoNaoEncontradoException;
import com.caiovitor.vac.domain.calculo.CalculadoraFaltas;
import com.caiovitor.vac.domain.calculo.CalculadoraNotas;
import com.caiovitor.vac.domain.calculo.ResultadoFaltas;
import com.caiovitor.vac.domain.exception.ConfiguracaoIncompletaException;
import com.caiovitor.vac.domain.model.Avaliacao;
import com.caiovitor.vac.domain.model.Disciplina;
import com.caiovitor.vac.domain.model.RegistroFalta;
import com.caiovitor.vac.infrastructure.persistence.AvaliacaoRepository;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;
import com.caiovitor.vac.infrastructure.persistence.RegistroFaltaEntity;
import com.caiovitor.vac.infrastructure.persistence.RegistroFaltaRepository;

/**
 * Monta o status de uma disciplina: carrega os dados salvos, converte para o domínio
 * e delega os cálculos a {@link CalculadoraFaltas} e {@link CalculadoraNotas}.
 *
 * <p>Este service não contém fórmulas: todas as regras de cálculo estão nas calculadoras.
 */
@Service
public class StatusDisciplinaService {

    private final DisciplinaService disciplinaService;
    private final RegistroFaltaRepository faltaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final CalculadoraFaltas calculadoraFaltas;
    private final CalculadoraNotas calculadoraNotas;

    public StatusDisciplinaService(DisciplinaService disciplinaService,
                                   RegistroFaltaRepository faltaRepository,
                                   AvaliacaoRepository avaliacaoRepository,
                                   CalculadoraFaltas calculadoraFaltas,
                                   CalculadoraNotas calculadoraNotas) {
        this.disciplinaService = disciplinaService;
        this.faltaRepository = faltaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.calculadoraFaltas = calculadoraFaltas;
        this.calculadoraNotas = calculadoraNotas;
    }

    /**
     * Calcula a situação de faltas e a projeção de notas da disciplina.
     *
     * <p>Se os pesos das avaliações ainda não somam 100%, a resposta traz {@code notas} nulo e
     * o motivo em {@code avisoNotas}, mas a parte de faltas continua sendo calculada.
     *
     * @param disciplinaId id da disciplina
     * @return o status consolidado
     * @throws RecursoNaoEncontradoException se a disciplina não existir
     */
    @Transactional(readOnly = true)
    public StatusDisciplinaResponse calcular(Long disciplinaId) {
        DisciplinaEntity entidade = disciplinaService.buscarEntidade(disciplinaId);
        Disciplina disciplina = entidade.paraDominio();

        List<RegistroFalta> faltas = faltaRepository.findByDisciplinaIdOrderByDataAsc(disciplinaId).stream()
                .map(RegistroFaltaEntity::paraDominio)
                .toList();
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByDisciplinaIdOrderByIdAsc(disciplinaId).stream()
                .map(avaliacao -> avaliacao.paraDominio(disciplina.getNotaMax()))
                .toList();

        ResultadoFaltas resultadoFaltas = calculadoraFaltas.calcular(disciplina, faltas);

        ProjecaoNotasResponse notas = null;
        String avisoNotas = null;
        try {
            notas = ProjecaoNotasResponse.de(calculadoraNotas.calcular(disciplina, avaliacoes));
        } catch (ConfiguracaoIncompletaException e) {
            // pesos incompletos não impedem o status: as faltas continuam úteis enquanto o aluno cadastra as provas
            avisoNotas = e.getMessage();
        }

        return new StatusDisciplinaResponse(
                entidade.getId(), disciplina.getNome(), SituacaoFaltasResponse.de(resultadoFaltas), notas, avisoNotas);
    }
}
