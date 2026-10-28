package com.caiovitor.vac.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caiovitor.vac.api.dto.AtualizarNotaRequest;
import com.caiovitor.vac.api.dto.AvaliacaoResponse;
import com.caiovitor.vac.api.dto.CriarAvaliacaoRequest;
import com.caiovitor.vac.application.exception.ConflitoException;
import com.caiovitor.vac.application.exception.RecursoNaoEncontradoException;
import com.caiovitor.vac.domain.exception.DominioInvalidoException;
import com.caiovitor.vac.domain.model.Avaliacao;
import com.caiovitor.vac.infrastructure.persistence.AvaliacaoEntity;
import com.caiovitor.vac.infrastructure.persistence.AvaliacaoRepository;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;

/**
 * Orquestra o cadastro de avaliações e o lançamento de notas de uma disciplina.
 *
 * <p>As regras de uma avaliação isolada (peso e nota) são validadas por {@link Avaliacao}.
 * Este service cuida da regra que depende das avaliações já salvas: a soma dos pesos
 * de uma disciplina não pode passar de 100%.
 */
@Service
public class AvaliacaoService {

    private static final BigDecimal CEM = new BigDecimal("100");

    private final AvaliacaoRepository repository;
    private final DisciplinaService disciplinaService;

    public AvaliacaoService(AvaliacaoRepository repository, DisciplinaService disciplinaService) {
        this.repository = repository;
        this.disciplinaService = disciplinaService;
    }

    /**
     * Cadastra uma avaliação, com ou sem nota.
     *
     * @param disciplinaId id da disciplina
     * @param request      nome, peso e nota opcional
     * @return a avaliação criada, com id
     * @throws RecursoNaoEncontradoException se a disciplina não existir
     * @throws DominioInvalidoException      se o peso ou a nota forem inválidos
     * @throws ConflitoException             se a soma dos pesos passar de 100%
     */
    // shortcut: soma e save não são atômicos entre requisições simultâneas; usar lock se houver multiusuário
    @Transactional
    public AvaliacaoResponse criar(Long disciplinaId, CriarAvaliacaoRequest request) {
        DisciplinaEntity disciplina = disciplinaService.buscarEntidade(disciplinaId);
        Avaliacao avaliacao = new Avaliacao(
                request.nome(), request.pesoPercentual(), request.nota(), disciplina.getNotaMaxima());

        BigDecimal pesoAtual = repository.findByDisciplinaIdOrderByIdAsc(disciplinaId).stream()
                .map(AvaliacaoEntity::getPesoPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (pesoAtual.add(avaliacao.getPesoPercentual()).compareTo(CEM) > 0) {
            throw new ConflitoException("Os pesos já somam " + formatar(pesoAtual) + "%. Restam "
                    + formatar(CEM.subtract(pesoAtual)) + "% para novas avaliações.");
        }

        AvaliacaoEntity salva = repository.save(AvaliacaoEntity.de(avaliacao, disciplina));
        return AvaliacaoResponse.de(salva);
    }

    /**
     * Lista as avaliações da disciplina na ordem de cadastro.
     *
     * @throws RecursoNaoEncontradoException se a disciplina não existir
     */
    public List<AvaliacaoResponse> listar(Long disciplinaId) {
        // garante o 404: sem isso, uma disciplina inexistente responderia [] como se não tivesse avaliações
        disciplinaService.buscarEntidade(disciplinaId);

        return repository.findByDisciplinaIdOrderByIdAsc(disciplinaId).stream()
                .map(AvaliacaoResponse::de)
                .toList();
    }

    /**
     * Registra ou corrige a nota de uma avaliação. A alteração é gravada ao fim da transação,
     * sem chamar {@code save}: o JPA detecta a mudança na entidade.
     *
     * @param disciplinaId id da disciplina
     * @param avaliacaoId  id da avaliação, que precisa pertencer a essa disciplina
     * @param request      a nova nota
     * @return a avaliação com a nota atualizada
     * @throws RecursoNaoEncontradoException se a disciplina não existir ou a avaliação não for dela
     * @throws DominioInvalidoException      se a nota for negativa ou passar da nota máxima
     */
    @Transactional
    public AvaliacaoResponse registrarNota(Long disciplinaId, Long avaliacaoId, AtualizarNotaRequest request) {
        DisciplinaEntity disciplina = disciplinaService.buscarEntidade(disciplinaId);
        AvaliacaoEntity avaliacao = repository.findByIdAndDisciplinaId(avaliacaoId, disciplinaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Avaliação " + avaliacaoId + " não encontrada na disciplina " + disciplinaId + "."));

        // valida a nova nota pelas regras do domínio; o objeto em si não é usado
        new Avaliacao(avaliacao.getNome(), avaliacao.getPesoPercentual(), request.nota(), disciplina.getNotaMaxima());

        avaliacao.registrarNota(request.nota());
        return AvaliacaoResponse.de(avaliacao);
    }

    // o banco devolve os pesos com 2 casas (60.00); na mensagem, "60" é mais legível
    private static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }
}
