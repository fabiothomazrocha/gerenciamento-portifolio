package com.portfolio.gerenciamento.exception;

import com.portfolio.gerenciamento.enums.StatusProjeto;

public class TransicaoStatusInvalidaException extends RuntimeException {

    public TransicaoStatusInvalidaException(StatusProjeto atual, StatusProjeto pretendido) {
        super(String.format(
                "Transição de status inválida: não é possível ir de '%s' para '%s'",
                atual.getDescricao(), pretendido.getDescricao()
        ));
    }
}
