package com.portfolio.gerenciamento.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProjetoAtualizacaoRequest(
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String nome,
        LocalDate dataInicio,
        LocalDate previsaoTermino,
        LocalDate dataRealTermino,
        @DecimalMin(value = "0.01", message = "Orçamento deve ser positivo")
        @Digits(integer = 13, fraction = 2, message = "Orçamento inválido")
        BigDecimal orcamentoTotal,
        @Size(max = 5000, message = "Descrição deve ter no máximo 5000 caracteres")
        String descricao,
        Long gerenteId
) {
}
