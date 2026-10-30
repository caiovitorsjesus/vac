package com.caiovitor.vac.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caiovitor.vac.api.dto.FaltaResponse;
import com.caiovitor.vac.api.dto.RegistrarFaltaRequest;
import com.caiovitor.vac.application.FaltaService;

import jakarta.validation.Valid;

/**
 * Endpoints das faltas de uma disciplina: registro e listagem.
 */
@RestController
@RequestMapping("/api/disciplinas/{disciplinaId}/faltas")
public class FaltaController {

    private final FaltaService service;

    public FaltaController(FaltaService service) {
        this.service = service;
    }

    /** Registra uma falta. Responde 404 se a disciplina não existir. */
    // 201 sem Location: não existe GET de uma falta individual para onde apontar
    @PostMapping
    public ResponseEntity<FaltaResponse> registrar(@PathVariable Long disciplinaId,
                                                   @Valid @RequestBody RegistrarFaltaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(disciplinaId, request));
    }

    /** Lista as faltas da disciplina, da mais antiga para a mais recente. */
    @GetMapping
    public List<FaltaResponse> listar(@PathVariable Long disciplinaId) {
        return service.listar(disciplinaId);
    }
}
