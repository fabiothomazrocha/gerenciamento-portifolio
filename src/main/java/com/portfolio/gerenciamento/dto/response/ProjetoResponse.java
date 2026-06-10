package com.portfolio.gerenciamento.dto.response;

import com.portfolio.gerenciamento.enums.ClassificacaoRisco;
import com.portfolio.gerenciamento.enums.StatusProjeto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record ProjetoResponse(
        Long id,
        String nome,
        LocalDate dataInicio,
        LocalDate previsaoTermino,
        LocalDate dataRealTermino,
        BigDecimal orcamentoTotal,
        String descricao,
        StatusProjeto status,
        String statusDescricao,
        ClassificacaoRisco classificacaoRisco,
        String classificacaoRiscoDescricao,
        MembroResponse gerente,
        Set<MembroResponse> membros,
        int totalMembros,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
