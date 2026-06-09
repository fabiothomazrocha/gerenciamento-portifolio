package com.portfolio.gerenciamento.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MembroCriacaoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255)
        String nome,

        @NotBlank(message = "Atribuição é obrigatória")
        @Size(max = 100)
        String atribuicao)
{}
