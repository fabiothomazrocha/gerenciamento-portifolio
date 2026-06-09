package com.portfolio.gerenciamento.repository;

import com.portfolio.gerenciamento.entity.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {

    Optional<Membro> findByIdExterno(String idExterno);

    boolean existsByIdExterno(String idExterno);
}

