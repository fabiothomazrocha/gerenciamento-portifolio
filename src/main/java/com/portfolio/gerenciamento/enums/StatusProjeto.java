package com.portfolio.gerenciamento.enums;

import java.util.EnumSet;
import java.util.Set;

public enum StatusProjeto {

    EM_ANALISE("Em análise"),
    ANALISE_REALIZADA("Análise realizada"),
    ANALISE_APROVADA("Análise aprovada"),
    INICIADO("Iniciado"),
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    ENCERRADO("Encerrado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusProjeto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * CANCELADO pode ser aplicado a qualquer momento.
     */
    public boolean podeTransicionarPara(StatusProjeto proximo) {
        if (proximo == CANCELADO) {
            return this != ENCERRADO && this != CANCELADO;
        }
        return getProximosPermitidos().contains(proximo);
    }

    private Set<StatusProjeto> getProximosPermitidos() {
        return switch (this) {
            case EM_ANALISE       -> EnumSet.of(ANALISE_REALIZADA);
            case ANALISE_REALIZADA -> EnumSet.of(ANALISE_APROVADA);
            case ANALISE_APROVADA  -> EnumSet.of(INICIADO);
            case INICIADO         -> EnumSet.of(PLANEJADO);
            case PLANEJADO        -> EnumSet.of(EM_ANDAMENTO);
            case EM_ANDAMENTO     -> EnumSet.of(ENCERRADO);
            case ENCERRADO, CANCELADO -> EnumSet.noneOf(StatusProjeto.class);
        };
    }

    public static Set<StatusProjeto> statusImpedemExclusao() {
        return EnumSet.of(INICIADO, EM_ANDAMENTO, ENCERRADO);
    }
}
