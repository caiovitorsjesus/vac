package com.caiovitor.vac.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso à tabela {@code avaliacao}. As consultas são geradas pelo Spring Data a partir do nome dos métodos.
 */
public interface AvaliacaoRepository extends JpaRepository<AvaliacaoEntity, Long> {

    /**
     * Lista as avaliações de uma disciplina na ordem em que foram cadastradas.
     */
    List<AvaliacaoEntity> findByDisciplinaIdOrderByIdAsc(Long disciplinaId);

    /**
     * Busca uma avaliação garantindo que ela pertence à disciplina informada.
     * Retorna vazio tanto se a avaliação não existir quanto se for de outra disciplina.
     */
    Optional<AvaliacaoEntity> findByIdAndDisciplinaId(Long id, Long disciplinaId);
}
