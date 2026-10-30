package com.caiovitor.vac.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.caiovitor.vac.api.dto.CriarDisciplinaRequest;
import com.caiovitor.vac.api.dto.DisciplinaResponse;
import com.caiovitor.vac.application.exception.RecursoNaoEncontradoException;
import com.caiovitor.vac.domain.model.Disciplina;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaRepository;

/**
 * Orquestra o cadastro e a consulta de disciplinas.
 *
 * <p>Também é o ponto único da regra "disciplina inexistente responde 404", reutilizada
 * pelos services de faltas, avaliações e status por meio de {@link #buscarEntidade(Long)}.
 */
@Service
public class DisciplinaService {

    private final DisciplinaRepository repository;

    public DisciplinaService(DisciplinaRepository repository) {
        this.repository = repository;
    }

    /**
     * Cadastra uma disciplina. O dado é validado pelo domínio antes de ser salvo.
     *
     * @param request dados da disciplina
     * @return a disciplina salva, com id
     * @throws com.caiovitor.vac.domain.exception.DominioInvalidoException se os dados violarem as regras de {@link Disciplina}
     */
    public DisciplinaResponse criar(CriarDisciplinaRequest request) {
        Disciplina disciplina = new Disciplina(
                request.nome(),
                request.cargaHorariaTotal(),
                request.percentualMaximoFaltas(),
                request.mediaMinima(),
                request.notaMaxima());

        DisciplinaEntity salva = repository.save(DisciplinaEntity.de(disciplina));
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

}
