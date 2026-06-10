package com.portfolio.gerenciamentoportifolio.service;

import com.portfolio.gerenciamento.enums.ClassificacaoRisco;
import com.portfolio.gerenciamento.service.CalculadorRisco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RiscoCalculator - Cálculo de risco dos projetos")
public class CalculadorRiscoTest {

    private CalculadorRisco calculator;
    private final LocalDate hoje = LocalDate.now();

    @BeforeEach
    void setUp() {
        calculator = new CalculadorRisco();
    }

    @Nested
    @DisplayName("Baixo Risco")
    class BaixoRisco {

        @Test
        @DisplayName("Orçamento exatamente R$100.000 e prazo exatamente 3 meses = BAIXO")
        void orcamentoLimiteBaixoEPrazoLimiteBaixo_deveriaBaixoRisco() {
            BigDecimal orcamento = new BigDecimal("100000.00");
            LocalDate termino = hoje.plusMonths(3);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.BAIXO);
        }

        @Test
        @DisplayName("Orçamento R$50.000 e prazo 1 mês = BAIXO")
        void orcamentoBaixoEPrazoCurto_deveriaBaixoRisco() {
            BigDecimal orcamento = new BigDecimal("50000.00");
            LocalDate termino = hoje.plusMonths(1);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.BAIXO);
        }

        @Test
        @DisplayName("Orçamento R$1.000 e prazo 2 meses = BAIXO")
        void orcamentoMinimoEPrazoCurto_deveriaBaixoRisco() {
            BigDecimal orcamento = new BigDecimal("1000.00");
            LocalDate termino = hoje.plusMonths(2);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.BAIXO);
        }
    }

    @Nested
    @DisplayName("Médio Risco")
    class MedioRisco {

        @Test
        @DisplayName("Orçamento entre R$100.001 e R$500.000 com prazo curto = MÉDIO")
        void orcamentoMedioEPrazoCurto_deveriaMedioRisco() {
            BigDecimal orcamento = new BigDecimal("300000.00");
            LocalDate termino = hoje.plusMonths(2);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.MEDIO);
        }

        @Test
        @DisplayName("Orçamento baixo com prazo entre 3 e 6 meses = MÉDIO")
        void orcamentoBaixoEPrazoMedio_deveriaMedioRisco() {
            BigDecimal orcamento = new BigDecimal("50000.00");
            LocalDate termino = hoje.plusMonths(5);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.MEDIO);
        }

        @Test
        @DisplayName("Orçamento exatamente R$500.000 = MÉDIO")
        void orcamentoLimiteMedioSuperior_deveriaMedioRisco() {
            BigDecimal orcamento = new BigDecimal("500000.00");
            LocalDate termino = hoje.plusMonths(2);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.MEDIO);
        }

        @Test
        @DisplayName("Prazo exatamente 6 meses = MÉDIO")
        void prazoLimiteMedioSuperior_deveriaMedioRisco() {
            BigDecimal orcamento = new BigDecimal("50000.00");
            LocalDate termino = hoje.plusMonths(6);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.MEDIO);
        }

        @Test
        @DisplayName("Orçamento R$100.001 = MÉDIO (limite inferior)")
        void orcamentoPrimeiroMedio_deveriaMedioRisco() {
            BigDecimal orcamento = new BigDecimal("100001.00");
            LocalDate termino = hoje.plusMonths(2);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.MEDIO);
        }
    }

    @Nested
    @DisplayName("Alto Risco")
    class AltoRisco {

        @Test
        @DisplayName("Orçamento acima de R$500.000 = ALTO")
        void orcamentoAlto_deveriaAltoRisco() {
            BigDecimal orcamento = new BigDecimal("500001.00");
            LocalDate termino = hoje.plusMonths(2);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.ALTO);
        }

        @Test
        @DisplayName("Prazo superior a 6 meses = ALTO")
        void prazoAlto_deveriaAltoRisco() {
            BigDecimal orcamento = new BigDecimal("50000.00");
            LocalDate termino = hoje.plusMonths(7);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.ALTO);
        }

        @Test
        @DisplayName("Orçamento e prazo ambos altos = ALTO")
        void ambosAltos_deveriaAltoRisco() {
            BigDecimal orcamento = new BigDecimal("1000000.00");
            LocalDate termino = hoje.plusYears(2);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.ALTO);
        }

        @Test
        @DisplayName("Orçamento R$1.000.000 = ALTO")
        void orcamentoMuitoAlto_deveriaAltoRisco() {
            BigDecimal orcamento = new BigDecimal("1000000.00");
            LocalDate termino = hoje.plusMonths(1);

            ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

            assertThat(resultado).isEqualTo(ClassificacaoRisco.ALTO);
        }
    }

    @ParameterizedTest(name = "orçamento={0}, meses={1} => {2}")
    @CsvSource({
        "50000,   1, BAIXO",
        "100000,  3, BAIXO",
        "100001,  2, MEDIO",
        "300000,  5, MEDIO",
        "500000,  6, MEDIO",
        "500001,  1, ALTO",
        "50000,   7, ALTO",
        "999999,  12, ALTO"
    })
    @DisplayName("Testes parametrizados de classificação de risco")
    void testeParametrizado(String orcamentoStr, int meses, String esperado) {
        BigDecimal orcamento = new BigDecimal(orcamentoStr);
        LocalDate termino = hoje.plusMonths(meses);

        ClassificacaoRisco resultado = calculator.calcular(orcamento, hoje, termino);

        assertThat(resultado.name()).isEqualTo(esperado);
    }
}
