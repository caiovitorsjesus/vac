package com.caiovitor.vac.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.caiovitor.vac.api.dto.FaltaResponse;
import com.caiovitor.vac.api.dto.RegistrarFaltaRequest;
import com.caiovitor.vac.domain.model.RegistroFalta;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;
import com.caiovitor.vac.infrastructure.persistence.RegistroFaltaEntity;
import com.caiovitor.vac.infrastructure.persistence.RegistroFaltaRepository;

/**
 * Orquestra o registro e a listagem de faltas de uma disciplina.
 *
 * <p>A falta é validada pelo domínio sem saber a qual disciplina pertence; a ligação acontece
 * aqui, ao converter para {@link RegistroFaltaEntity}.
 */
@Service
public class FaltaService {

    private final RegistroFaltaRepository repository;
    private final DisciplinaService disciplinaService;

    public FaltaService(RegistroFaltaRepository repository, DisciplinaService disciplinaService) {
        this.repository = repository;
        this.disciplinaService = disciplinaService;
    }

    /**
     * Registra uma falta para a disciplina informada.
     *
     * @param disciplinaId id da disciplina à qual a falta pertence
     * @param request      data, horas e observação opcional da ausência
     * @return a falta registrada, com id
     * @throws com.caiovitor.vac.application.exception.RecursoNaoEncontradoException se a disciplina não existir
     * @throws com.caiovitor.vac.domain.exception.DominioInvalidoException se a data for futura ou as horas não forem positivas
     */
    public FaltaResponse registrar(Long disciplinaId, RegistrarFaltaRequest request) {
        DisciplinaEntity disciplina = disciplinaService.buscarEntidade(disciplinaId);
        RegistroFalta falta = new RegistroFalta(request.data(), request.horas(), request.observacao());

        RegistroFaltaEntity salva = repository.save(RegistroFaltaEntity.de(falta, disciplina));
        return FaltaResponse.de(salva);
    }

    /**
     * Lista as faltas da disciplina, da mais antiga para a mais recente.
     *
     * @throws com.caiovitor.vac.application.exception.RecursoNaoEncontradoException se a disciplina não existir
     */
    public List<FaltaResponse> listar(Long disciplinaId) {
        // garante o 404: sem isso, uma disciplina inexistente responderia [] como se não tivesse faltas
        disciplinaService.buscarEntidade(disciplinaId);

        return repository.findByDisciplinaIdOrderByDataAsc(disciplinaId).stream()
                .map(FaltaResponse::de)
                .toList();
    }
}
