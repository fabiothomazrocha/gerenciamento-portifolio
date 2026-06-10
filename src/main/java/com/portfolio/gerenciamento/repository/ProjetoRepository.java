package com.portfolio.gerenciamento.repository;

import com.portfolio.gerenciamento.entity.Projeto;
import com.portfolio.gerenciamento.enums.StatusProjeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long>,
        JpaSpecificationExecutor<Projeto> {

    Page<Projeto> findByStatus(StatusProjeto status, Pageable pageable);

    @Query("""
        SELECT p FROM Projeto p
        WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:status IS NULL OR p.status = :status)
          AND (:gerenteId IS NULL OR p.gerente.id = :gerenteId)
    """)
    Page<Projeto> findByFiltros(
            @Param("nome") String nome,
            @Param("status") StatusProjeto status,
            @Param("gerenteId") Long gerenteId,
            Pageable pageable
    );

    @Query("SELECT p FROM Projeto p WHERE p.status = com.portfolio.gerenciamento.enums.StatusProjeto.ENCERRADO")
    List<Projeto> findEncerrados();

    @Query("""
        SELECT COUNT(p) FROM Projeto p
        JOIN p.membros m
        WHERE m.id = :membroId
          AND p.status NOT IN (
            com.portfolio.gerenciamento.enums.StatusProjeto.ENCERRADO,
            com.portfolio.gerenciamento.enums.StatusProjeto.CANCELADO
          )
    """)
    long countProjetosAtivosDoMembro(@Param("membroId") Long membroId);
}
