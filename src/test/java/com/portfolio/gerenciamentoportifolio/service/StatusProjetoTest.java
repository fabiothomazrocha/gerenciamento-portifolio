package com.portfolio.gerenciamentoportifolio.service;

import com.portfolio.gerenciamento.enums.StatusProjeto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StatusProjeto - Lógica de transição de status")
class StatusProjetoTest {

    @Nested
    @DisplayName("Sequência correta de transições")
    class SequenciaCorreta {

        @Test void emAnalise_podeIrPara_analiseRealizada() {
            assertThat(StatusProjeto.EM_ANALISE.podeTransicionarPara(StatusProjeto.ANALISE_REALIZADA)).isTrue();
        }

        @Test void analiseRealizada_podeIrPara_analiseAprovada() {
            assertThat(StatusProjeto.ANALISE_REALIZADA.podeTransicionarPara(StatusProjeto.ANALISE_APROVADA)).isTrue();
        }

        @Test void analiseAprovada_podeIrPara_iniciado() {
            assertThat(StatusProjeto.ANALISE_APROVADA.podeTransicionarPara(StatusProjeto.INICIADO)).isTrue();
        }

        @Test void iniciado_podeIrPara_planejado() {
            assertThat(StatusProjeto.INICIADO.podeTransicionarPara(StatusProjeto.PLANEJADO)).isTrue();
        }

        @Test void planejado_podeIrPara_emAndamento() {
            assertThat(StatusProjeto.PLANEJADO.podeTransicionarPara(StatusProjeto.EM_ANDAMENTO)).isTrue();
        }

        @Test void emAndamento_podeIrPara_encerrado() {
            assertThat(StatusProjeto.EM_ANDAMENTO.podeTransicionarPara(StatusProjeto.ENCERRADO)).isTrue();
        }
    }

    @Nested
    @DisplayName("Transições inválidas - não pode pular etapas")
    class TransicoesInvalidas {

        @Test void emAnalise_naoPodeIrPara_analiseAprovada() {
            assertThat(StatusProjeto.EM_ANALISE.podeTransicionarPara(StatusProjeto.ANALISE_APROVADA)).isFalse();
        }

        @Test void emAnalise_naoPodeIrPara_iniciado() {
            assertThat(StatusProjeto.EM_ANALISE.podeTransicionarPara(StatusProjeto.INICIADO)).isFalse();
        }

        @Test void emAnalise_naoPodeIrPara_encerrado() {
            assertThat(StatusProjeto.EM_ANALISE.podeTransicionarPara(StatusProjeto.ENCERRADO)).isFalse();
        }

        @Test void encerrado_naoPodeTransicionar() {
            for (StatusProjeto s : StatusProjeto.values()) {
                assertThat(StatusProjeto.ENCERRADO.podeTransicionarPara(s)).isFalse();
            }
        }

        @Test void cancelado_naoPodeTransicionar() {
            for (StatusProjeto s : StatusProjeto.values()) {
                assertThat(StatusProjeto.CANCELADO.podeTransicionarPara(s)).isFalse();
            }
        }

        @Test void emAndamento_naoPodeVoltar_para_planejado() {
            assertThat(StatusProjeto.EM_ANDAMENTO.podeTransicionarPara(StatusProjeto.PLANEJADO)).isFalse();
        }
    }

    @Nested
    @DisplayName("Cancelamento - pode ser aplicado a qualquer momento (exceto encerrado/cancelado)")
    class Cancelamento {

        @ParameterizedTest
        @EnumSource(value = StatusProjeto.class,
                    names = {"ENCERRADO", "CANCELADO"},
                    mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("Qualquer status ativo pode ser cancelado")
        void statusAtivo_podeCancelar(StatusProjeto status) {
            assertThat(status.podeTransicionarPara(StatusProjeto.CANCELADO)).isTrue();
        }

        @Test
        @DisplayName("Projeto encerrado NÃO pode ser cancelado")
        void encerrado_naoPodeCancelar() {
            assertThat(StatusProjeto.ENCERRADO.podeTransicionarPara(StatusProjeto.CANCELADO)).isFalse();
        }

        @Test
        @DisplayName("Projeto já cancelado NÃO pode ser cancelado novamente")
        void cancelado_naoPodeCancelar() {
            assertThat(StatusProjeto.CANCELADO.podeTransicionarPara(StatusProjeto.CANCELADO)).isFalse();
        }
    }

    @Nested
    @DisplayName("Status que impedem exclusão")
    class StatusExclusao {

        @Test
        void iniciado_impede_exclusao() {
            assertThat(StatusProjeto.statusImpedemExclusao()).contains(StatusProjeto.INICIADO);
        }

        @Test
        void emAndamento_impede_exclusao() {
            assertThat(StatusProjeto.statusImpedemExclusao()).contains(StatusProjeto.EM_ANDAMENTO);
        }

        @Test
        void encerrado_impede_exclusao() {
            assertThat(StatusProjeto.statusImpedemExclusao()).contains(StatusProjeto.ENCERRADO);
        }

        @Test
        void emAnalise_nao_impede_exclusao() {
            assertThat(StatusProjeto.statusImpedemExclusao()).doesNotContain(StatusProjeto.EM_ANALISE);
        }

        @Test
        void cancelado_nao_impede_exclusao() {
            assertThat(StatusProjeto.statusImpedemExclusao()).doesNotContain(StatusProjeto.CANCELADO);
        }
    }
}
