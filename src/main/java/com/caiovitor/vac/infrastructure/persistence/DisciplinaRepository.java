package com.caiovitor.vac.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso à tabela {@code disciplina}. A implementação é gerada pelo Spring Data.
 */
public interface DisciplinaRepository extends JpaRepository<DisciplinaEntity, Long> {
    
}
