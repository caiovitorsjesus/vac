package com.caiovitor.vac.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caiovitor.vac.api.dto.CriarDisciplinaRequest;
import com.caiovitor.vac.api.dto.DisciplinaResponse;
import com.caiovitor.vac.api.dto.StatusDisciplinaResponse;
import com.caiovitor.vac.application.DisciplinaService;
import com.caiovitor.vac.application.StatusDisciplinaService;

import jakarta.validation.Valid;

/**
 * Endpoints de disciplinas: cadastro, consulta e status consolidado.
 */
@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {
    private final DisciplinaService service;
    private final StatusDisciplinaService statusService;

    public DisciplinaController(DisciplinaService service, StatusDisciplinaService statusService) {
        this.service = service;
        this.statusService = statusService;
    }

    /** Cadastra uma disciplina. Responde 201 com o cabeçalho {@code Location} apontando para ela. */
    @PostMapping
    public ResponseEntity<DisciplinaResponse> criar(@Valid @RequestBody CriarDisciplinaRequest request){
        DisciplinaResponse criado = service.criar(request);
        URI location = URI.create("/api/disciplinas/" + criado.id());
        return ResponseEntity.created(location).body(criado);
    }

    /** Lista todas as disciplinas. */
    @GetMapping
    public List<DisciplinaResponse> listar(){
        return service.listar();
    }

    /** Consulta uma disciplina. Responde 404 se ela não existir. */
    @GetMapping("/{id}")
    public DisciplinaResponse buscar(@PathVariable Long id ){
        return service.buscar(id);
    }

    /** Situação de faltas e projeção de notas da disciplina. */
    @GetMapping("/{id}/status")
    public StatusDisciplinaResponse status(@PathVariable Long id) {
        return statusService.calcular(id);
    }
}
