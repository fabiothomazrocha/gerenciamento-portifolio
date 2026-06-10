package com.portfolio.gerenciamento.dto.response;

import java.time.LocalDateTime;

public record MembroResponse(
        Long id,
        String idExterno,
        String nome,
        String atribuicao,
        LocalDateTime criadoEm
) {}
