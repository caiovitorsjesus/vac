package com.caiovitor.vac.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.caiovitor.vac.api.dto.CriarDisciplinaRequest;
import com.caiovitor.vac.api.dto.DisciplinaResponse;
import com.caiovitor.vac.application.exception.RecursoNaoEncontradoException;
import com.caiovitor.vac.domain.model.Disciplina;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaRepository;

@Service
public class DisciplinaService {

    private final DisciplinaRepository repository;

    public DisciplinaService(DisciplinaRepository repository) {
        this.repository = repository;
    }

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

    public List<DisciplinaResponse> listar() {
        return repository.findAll().stream()
                .map(DisciplinaResponse::de)
                .toList();
    }

    public DisciplinaResponse buscar(Long id) {
        return DisciplinaResponse.de(buscarEntidade(id));
    }

    public DisciplinaEntity buscarEntidade(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina " + id + " não encontrada."));
    }

}
