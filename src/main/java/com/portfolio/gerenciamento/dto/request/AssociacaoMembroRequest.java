package com.portfolio.gerenciamento.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssociacaoMembroRequest(
        @NotNull(message = "ID do membro é obrigatório")
        Long membroId
) {
}
