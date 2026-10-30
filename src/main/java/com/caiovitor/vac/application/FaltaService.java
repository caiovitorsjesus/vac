package com.caiovitor.vac.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.caiovitor.vac.api.dto.FaltaResponse;
import com.caiovitor.vac.api.dto.RegistrarFaltaRequest;
import com.caiovitor.vac.domain.model.RegistroFalta;
import com.caiovitor.vac.infrastructure.persistence.DisciplinaEntity;
import com.caiovitor.vac.infrastructure.persistence.RegistroFaltaEntity;
import com.caiovitor.vac.infrastructure.persistence.RegistroFaltaRepository;

@Service
public class FaltaService {

    private final RegistroFaltaRepository repository;
    private final DisciplinaService disciplinaService;

    public FaltaService(RegistroFaltaRepository repository, DisciplinaService disciplinaService) {
        this.repository = repository;
        this.disciplinaService = disciplinaService;
    }

    public FaltaResponse registrar(Long disciplinaId, RegistrarFaltaRequest request) {
        DisciplinaEntity disciplina = disciplinaService.buscarEntidade(disciplinaId);
        RegistroFalta falta = new RegistroFalta(request.data(), request.horas(), request.observacao());

        RegistroFaltaEntity salva = repository.save(RegistroFaltaEntity.de(falta, disciplina));
        return FaltaResponse.de(salva);
    }

    public List<FaltaResponse> listar(Long disciplinaId) {
        // garante o 404: sem isso, uma disciplina inexistente responderia [] como se não tivesse faltas
        disciplinaService.buscarEntidade(disciplinaId);

        return repository.findByDisciplinaIdOrderByDataAsc(disciplinaId).stream()
                .map(FaltaResponse::de)
                .toList();
    }
}
