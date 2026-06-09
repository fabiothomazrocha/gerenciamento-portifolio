package com.portfolio.gerenciamento.repository;

import com.portfolio.gerenciamento.entity.Projeto;
import com.portfolio.gerenciamento.enums.StatusProjeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long>,
        JpaSpecificationExecutor<Projeto> {

    Page<Projeto> findByStatus(StatusProjeto status, Pageable pageable);
}
