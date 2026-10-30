package com.caiovitor.vac.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso à tabela {@code registro_falta}. As consultas são geradas pelo Spring Data a partir do nome dos métodos.
 */
public interface RegistroFaltaRepository extends JpaRepository<RegistroFaltaEntity, Long> {

    /** Lista as faltas de uma disciplina, da mais antiga para a mais recente. */
    List<RegistroFaltaEntity> findByDisciplinaIdOrderByDataAsc(Long disciplinaId);
}
