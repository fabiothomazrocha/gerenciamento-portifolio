package com.portfolio.gerenciamento.service;

import com.portfolio.gerenciamento.enums.ClassificacaoRisco;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Component
public class CalculadorRisco {

    private static final BigDecimal LIMITE_BAIXO_RISCO = new BigDecimal("100000.00");
    private static final BigDecimal LIMITE_MEDIO_RISCO = new BigDecimal("500000.00");
    private static final long MESES_BAIXO_RISCO = 3L;
    private static final long MESES_MEDIO_RISCO = 6L;

    public CalculadorRisco() {
    }

    public ClassificacaoRisco calcular(BigDecimal orcamento, LocalDate dataInicio, LocalDate previsaoTermino) {
        long meses = ChronoUnit.MONTHS.between(dataInicio, previsaoTermino);

        boolean orcamentoBaxo  = orcamento.compareTo(LIMITE_BAIXO_RISCO) <= 0;
        boolean orcamentoMedio  = orcamento.compareTo(LIMITE_BAIXO_RISCO) > 0
                               && orcamento.compareTo(LIMITE_MEDIO_RISCO) <= 0;
        boolean orcamentoAlto   = orcamento.compareTo(LIMITE_MEDIO_RISCO) > 0;

        boolean prazoBaixo  = meses <= MESES_BAIXO_RISCO;
        boolean prazoMedio  = meses > MESES_BAIXO_RISCO && meses <= MESES_MEDIO_RISCO;
        boolean prazoAlto   = meses > MESES_MEDIO_RISCO;

        if (orcamentoAlto || prazoAlto) {
            return ClassificacaoRisco.ALTO;
        }

        if (orcamentoMedio || prazoMedio) {
            return ClassificacaoRisco.MEDIO;
        }

        if (orcamentoBaxo && prazoBaixo) {
            return ClassificacaoRisco.BAIXO;
        }

        return ClassificacaoRisco.MEDIO;
    }
}
