package com.caiovitor.vac.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroFaltaRepository extends JpaRepository<RegistroFaltaEntity, Long> {

    List<RegistroFaltaEntity> findByDisciplinaIdOrderByDataAsc(Long disciplinaId);
}
