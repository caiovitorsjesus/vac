-- Excluir uma disciplina passa a excluir também suas faltas e avaliações.
-- O PostgreSQL não altera uma restrição existente: ela é removida e recriada com ON DELETE CASCADE.
-- Os nomes são os gerados automaticamente para as chaves estrangeiras criadas na V1.

ALTER TABLE registro_falta DROP CONSTRAINT registro_falta_disciplina_id_fkey;
ALTER TABLE registro_falta
    ADD CONSTRAINT registro_falta_disciplina_id_fkey
    FOREIGN KEY (disciplina_id) REFERENCES disciplina (id) ON DELETE CASCADE;

ALTER TABLE avaliacao DROP CONSTRAINT avaliacao_disciplina_id_fkey;
ALTER TABLE avaliacao
    ADD CONSTRAINT avaliacao_disciplina_id_fkey
    FOREIGN KEY (disciplina_id) REFERENCES disciplina (id) ON DELETE CASCADE;
