package com.portfolio.gerenciamento.dto.request;

import com.portfolio.gerenciamento.enums.StatusProjeto;
import jakarta.validation.constraints.NotNull;

public record StatusTransicaoRequest(
        @NotNull(message = "Novo status é obrigatório")
        StatusProjeto novoStatus

) {}
