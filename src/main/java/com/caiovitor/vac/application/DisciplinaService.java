package com.caiovitor.vac.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caiovitor.vac.api.dto.CriarDisciplinaRequest;
import com.caiovitor.vac.api.dto.DisciplinaResponse;
import com.caiovitor.vac.application.exception.ConflitoException;
import com.caiovitor.vac.application.exception.RecursoNaoEncontradoException;
import com.caiovitor.vac.domain.model.Disciplina;
import com.caiovitor.vac.infrastructure.persistence.AvaliacaoRepository;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaRepository;

/**
 * Orquestra o cadastro, a consulta e a edição de disciplinas.
 *
 * <p>Também é o ponto único da regra "disciplina inexistente responde 404", reutilizada
 * pelos services de faltas, avaliações e status por meio de {@link #buscarEntidade(Long)}.
 */
@Service
public class DisciplinaService {

    private final DisciplinaRepository repository;
    // o repositório, e não o AvaliacaoService: este já depende do DisciplinaService (evita dependência circular)
    private final AvaliacaoRepository avaliacaoRepository;

    public DisciplinaService(DisciplinaRepository repository, AvaliacaoRepository avaliacaoRepository) {
        this.repository = repository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    /**
     * Cadastra uma disciplina. O dado é validado pelo domínio antes de ser salvo.
     *
     * @param request dados da disciplina
     * @return a disciplina salva, com id
     * @throws com.caiovitor.vac.domain.exception.DominioInvalidoException se os dados violarem as regras de {@link Disciplina}
     */
    public DisciplinaResponse criar(CriarDisciplinaRequest request) {
        DisciplinaEntity salva = repository.save(DisciplinaEntity.de(paraDominio(request)));
        return DisciplinaResponse.de(salva);
    }

    /** Lista todas as disciplinas cadastradas. */
    public List<DisciplinaResponse> listar() {
        return repository.findAll().stream()
                .map(DisciplinaResponse::de)
                .toList();
    }

    /**
     * Consulta uma disciplina.
     *
     * @throws RecursoNaoEncontradoException se a disciplina não existir
     */
    public DisciplinaResponse buscar(Long id) {
        return DisciplinaResponse.de(buscarEntidade(id));
    }

    /**
     * Substitui as regras de uma disciplina. A alteração é gravada ao fim da transação.
     *
     * <p>Uma nova nota máxima não pode ficar abaixo de uma nota já lançada: isso deixaria
     * avaliações inválidas no banco.
     *
     * @param id      id da disciplina
     * @param request as novas regras, completas
     * @return a disciplina atualizada
     * @throws RecursoNaoEncontradoException se a disciplina não existir
     * @throws com.caiovitor.vac.domain.exception.DominioInvalidoException se as regras forem inválidas
     * @throws ConflitoException se alguma nota lançada passar da nova nota máxima
     */
    @Transactional
    public DisciplinaResponse atualizar(Long id, CriarDisciplinaRequest request) {
        DisciplinaEntity entidade = buscarEntidade(id);
        Disciplina disciplina = paraDominio(request);

        avaliacaoRepository.findByDisciplinaIdOrderByIdAsc(id).stream()
                .filter(avaliacao -> avaliacao.getNota() != null
                        && avaliacao.getNota().compareTo(disciplina.getNotaMax()) > 0)
                .findFirst()
                .ifPresent(avaliacao -> {
                    throw new ConflitoException("A avaliação " + avaliacao.getNome() + " tem nota "
                            + formatar(avaliacao.getNota()) + ", acima da nova nota máxima "
                            + formatar(disciplina.getNotaMax()) + ".");
                });

        entidade.atualizar(disciplina);
        return DisciplinaResponse.de(entidade);
    }

    /**
     * Busca a entidade da disciplina, para os services que precisam ligar registros a ela.
     *
     * @param id id da disciplina
     * @return a entidade encontrada
     * @throws RecursoNaoEncontradoException se a disciplina não existir
     */
    public DisciplinaEntity buscarEntidade(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina " + id + " não encontrada."));
    }

    private static Disciplina paraDominio(CriarDisciplinaRequest request) {
        return new Disciplina(
                request.nome(),
                request.cargaHorariaTotal(),
                request.percentualMaximoFaltas(),
                request.mediaMinima(),
                request.notaMaxima());
    }

    // notas vindas do banco têm 2 casas (8.00); na mensagem, "8" é mais legível
    private static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }
}
