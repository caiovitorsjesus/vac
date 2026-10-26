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
import com.caiovitor.vac.application.DisciplinaService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/disciplinas")
public class DisciplinaController {
    private final DisciplinaService service;

    public DisciplinaController(DisciplinaService service) {
        this.service = service;
    }

    @PostMapping 
    public ResponseEntity<DisciplinaResponse> criar(@Valid @RequestBody CriarDisciplinaRequest request){
        DisciplinaResponse criado = service.criar(request);
        URI location = URI.create("/api/disciplinas/" + criado.id());
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<DisciplinaResponse> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public DisciplinaResponse buscar(@PathVariable Long id ){
        return service.buscar(id);
    }
}
