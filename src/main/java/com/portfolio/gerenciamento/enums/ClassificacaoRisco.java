package com.portfolio.gerenciamento.enums;

public enum ClassificacaoRisco {
    BAIXO("Baixo risco"),
    MEDIO("Médio risco"),
    ALTO("Alto risco");

    private final String descricao;

    ClassificacaoRisco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
