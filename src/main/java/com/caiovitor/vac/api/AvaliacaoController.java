package com.caiovitor.vac.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caiovitor.vac.api.dto.AtualizarNotaRequest;
import com.caiovitor.vac.api.dto.AvaliacaoResponse;
import com.caiovitor.vac.api.dto.CriarAvaliacaoRequest;
import com.caiovitor.vac.application.AvaliacaoService;

import jakarta.validation.Valid;

/**
 * Endpoints das avaliações de uma disciplina: cadastro, listagem e lançamento de notas.
 */
@RestController
@RequestMapping("/api/disciplinas/{disciplinaId}/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    /** Cadastra uma avaliação. Responde 201, ou 409 se os pesos passarem de 100%. */
    // 201 sem Location: não existe GET de uma avaliação individual para onde apontar
    @PostMapping
    public ResponseEntity<AvaliacaoResponse> criar(@PathVariable Long disciplinaId,
                                                   @Valid @RequestBody CriarAvaliacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(disciplinaId, request));
    }

    /** Lista as avaliações da disciplina na ordem de cadastro. */
    @GetMapping
    public List<AvaliacaoResponse> listar(@PathVariable Long disciplinaId) {
        return service.listar(disciplinaId);
    }

    /** Registra ou corrige a nota de uma avaliação. Responde 404 se a avaliação não for desta disciplina. */
    @PatchMapping("/{avaliacaoId}/nota")
    public AvaliacaoResponse registrarNota(@PathVariable Long disciplinaId,
                                           @PathVariable Long avaliacaoId,
                                           @Valid @RequestBody AtualizarNotaRequest request) {
        return service.registrarNota(disciplinaId, avaliacaoId, request);
    }
}
