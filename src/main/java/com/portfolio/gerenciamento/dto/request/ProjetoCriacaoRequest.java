package com.portfolio.gerenciamento.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjetoCriacaoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String nome,

        @NotNull(message = "Data de início é obrigatória")
        LocalDate dataInicio,

        @NotNull(message = "Previsão de término é obrigatória")
        LocalDate previsaoTermino,

        LocalDate dataRealTermino,

        @NotNull(message = "Orçamento total é obrigatório")
        @DecimalMin(value = "0.01", message = "Orçamento deve ser positivo")
        @Digits(integer = 13, fraction = 2, message = "Orçamento inválido")
        BigDecimal orcamentoTotal,

        @Size(max = 5000, message = "Descrição deve ter no máximo 5000 caracteres")
        String descricao,

        Long gerenteId
) {
}
