package com.portfolio.gerenciamento.dto.response;

import com.portfolio.gerenciamento.enums.StatusProjeto;

import java.math.BigDecimal;
import java.util.Map;

public record RelatorioPortfolioResponse(
        Map<StatusProjeto, Long> projetosPorStatus,
        Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus,
        Double mediaDuracaoProjetosEncerradosDias,
        Long totalMembrosUnicosAlocados
) {}
