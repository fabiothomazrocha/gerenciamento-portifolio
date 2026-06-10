package com.portfolio.gerenciamentoportifolio.service;

import com.portfolio.gerenciamento.dto.request.*;
import com.portfolio.gerenciamento.dto.response.ProjetoResponse;
import com.portfolio.gerenciamento.dto.response.RelatorioPortfolioResponse;
import com.portfolio.gerenciamento.entity.Membro;
import com.portfolio.gerenciamento.entity.Projeto;
import com.portfolio.gerenciamento.enums.ClassificacaoRisco;
import com.portfolio.gerenciamento.enums.StatusProjeto;
import com.portfolio.gerenciamento.exception.*;
import com.portfolio.gerenciamento.mapper.ProjetoMapper;
import com.portfolio.gerenciamento.repository.MembroRepository;
import com.portfolio.gerenciamento.repository.ProjetoRepository;
import com.portfolio.gerenciamento.service.impl.ProjetoServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjetoService - Regras de Negócio")
public class ProjetoServiceTest {

    @Mock private ProjetoRepository projetoRepository;
    @Mock private MembroRepository membroRepository;
    @Mock private ProjetoMapper projetoMapper;

    @InjectMocks
    private ProjetoServiceImpl projetoService;

    private Projeto projetoPadrao;
    private Membro membroPadrao;
    private ProjetoResponse projetoResponsePadrao;

    @BeforeEach
    void setUp() {
        membroPadrao = Membro.builder()
                .id(1L)
                .idExterno("ext-1")
                .nome("João Silva")
                .atribuicao("funcionário")
                .build();

        projetoPadrao = Projeto.builder()
                .id(1L)
                .nome("Projeto Teste")
                .dataInicio(LocalDate.now())
                .previsaoTermino(LocalDate.now().plusMonths(2))
                .orcamentoTotal(new BigDecimal("50000.00"))
                .status(StatusProjeto.EM_ANALISE)
                .membros(new HashSet<>())
                .build();

        projetoResponsePadrao = new ProjetoResponse(
                1L, "Projeto Teste",
                LocalDate.now(), LocalDate.now().plusMonths(2), null,
                new BigDecimal("50000.00"), "desc",
                StatusProjeto.EM_ANALISE, "Em análise",
                ClassificacaoRisco.BAIXO, "Baixo risco",
                null, Set.of(), 0,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }


    @Nested
    @DisplayName("Criação de projeto")
    class Criacao {

        @Test
        @DisplayName("Deve criar projeto com sucesso")
        void devecriarProjetoComSucesso() {
            ProjetoCriacaoRequest request = new ProjetoCriacaoRequest(
                    "Projeto Teste", LocalDate.now(), LocalDate.now().plusMonths(2),
                    null, new BigDecimal("50000.00"), "desc", null);

            when(projetoMapper.toProjeto(request)).thenReturn(projetoPadrao);
            when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoPadrao);
            when(projetoMapper.toResponse(projetoPadrao)).thenReturn(projetoResponsePadrao);

            ProjetoResponse resultado = projetoService.criar(request);

            assertThat(resultado).isNotNull();
            assertThat(resultado.nome()).isEqualTo("Projeto Teste");
            verify(projetoRepository).save(any(Projeto.class));
        }


        @Test
        @DisplayName("Deve lançar exceção quando previsão de término é anterior à data de início")
        void deveLancarExcecaoQuandoDataInvalida() {
            ProjetoCriacaoRequest request = new ProjetoCriacaoRequest(
                    "Projeto",
                    LocalDate.now(),
                    LocalDate.now().minusDays(1),
                    null,
                    new BigDecimal("50000.00"),
                    null,
                    null);

            assertThatThrownBy(() -> projetoService.criar(request))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("previsão de término");
        }
        @Test
        @DisplayName("Deve criar projeto com gerente quando gerenteId informado")
        void deveCriarProjetoComGerente() {
            ProjetoCriacaoRequest request = new ProjetoCriacaoRequest(
                    "Projeto", LocalDate.now(), LocalDate.now().plusMonths(2),
                    null, new BigDecimal("50000.00"), null, 1L);

            when(projetoMapper.toProjeto(request)).thenReturn(projetoPadrao);
            when(membroRepository.findById(1L)).thenReturn(Optional.of(membroPadrao));
            when(projetoRepository.save(any())).thenReturn(projetoPadrao);
            when(projetoMapper.toResponse(projetoPadrao)).thenReturn(projetoResponsePadrao);

            projetoService.criar(request);

            verify(membroRepository).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando gerente não encontrado")
        void deveLancarExcecaoGerenteNaoEncontrado() {
            ProjetoCriacaoRequest request = new ProjetoCriacaoRequest(
                    "Projeto", LocalDate.now(), LocalDate.now().plusMonths(2),
                    null, new BigDecimal("50000.00"), null, 99L);

            when(projetoMapper.toProjeto(request)).thenReturn(projetoPadrao);
            when(membroRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projetoService.criar(request))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }


    @Nested
    @DisplayName("Exclusão de projeto")
    class Exclusao {

        @Test
        @DisplayName("Deve excluir projeto com status EM_ANALISE")
        void deveExcluirProjetoEmAnalise() {
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));

            projetoService.excluir(1L);

            verify(projetoRepository).delete(projetoPadrao);
        }

        @Test
        @DisplayName("NÃO deve excluir projeto com status INICIADO")
        void naoDeveExcluirProjetoIniciado() {
            projetoPadrao.setStatus(StatusProjeto.INICIADO);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));

            assertThatThrownBy(() -> projetoService.excluir(1L))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("Iniciado");

            verify(projetoRepository, never()).delete(any(Projeto.class));
        }

        @Test
        @DisplayName("NÃO deve excluir projeto com status EM_ANDAMENTO")
        void naoDeveExcluirProjetoEmAndamento() {
            projetoPadrao.setStatus(StatusProjeto.EM_ANDAMENTO);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));

            assertThatThrownBy(() -> projetoService.excluir(1L))
                    .isInstanceOf(RegraDeNegocioException.class);
        }

        @Test
        @DisplayName("NÃO deve excluir projeto com status ENCERRADO")
        void naoDeveExcluirProjetoEncerrado() {
            projetoPadrao.setStatus(StatusProjeto.ENCERRADO);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));

            assertThatThrownBy(() -> projetoService.excluir(1L))
                    .isInstanceOf(RegraDeNegocioException.class);
        }

        @Test
        @DisplayName("Deve excluir projeto com status CANCELADO")
        void deveExcluirProjetoCancelado() {
            projetoPadrao.setStatus(StatusProjeto.CANCELADO);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));

            projetoService.excluir(1L);

            verify(projetoRepository).delete(projetoPadrao);
        }

        @Test
        @DisplayName("Deve lançar exceção quando projeto não encontrado")
        void deveLancarExcecaoProjetoNaoEncontrado() {
            when(projetoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projetoService.excluir(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Transição de status")
    class TransicaoStatus {

        @Test
        @DisplayName("Deve transicionar de EM_ANALISE para ANALISE_REALIZADA")
        void deveTransicionarEmAnaliseParaAnaliseRealizada() {
            StatusTransicaoRequest request = new StatusTransicaoRequest(StatusProjeto.ANALISE_REALIZADA);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(projetoRepository.save(any())).thenReturn(projetoPadrao);
            when(projetoMapper.toResponse(any())).thenReturn(projetoResponsePadrao);

            projetoService.transicionarStatus(1L, request);

            assertThat(projetoPadrao.getStatus()).isEqualTo(StatusProjeto.ANALISE_REALIZADA);
        }

        @Test
        @DisplayName("Deve lançar exceção em transição inválida")
        void deveLancarExcecaoTransicaoInvalida() {
            StatusTransicaoRequest request = new StatusTransicaoRequest(StatusProjeto.ENCERRADO);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));

            assertThatThrownBy(() -> projetoService.transicionarStatus(1L, request))
                    .isInstanceOf(TransicaoStatusInvalidaException.class)
                    .hasMessageContaining("Em análise")
                    .hasMessageContaining("Encerrado");
        }

        @Test
        @DisplayName("Deve cancelar projeto de qualquer status ativo")
        void deveCancelarProjetoDeQualquerStatusAtivo() {
            projetoPadrao.setStatus(StatusProjeto.EM_ANDAMENTO);
            StatusTransicaoRequest request = new StatusTransicaoRequest(StatusProjeto.CANCELADO);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(projetoRepository.save(any())).thenReturn(projetoPadrao);
            when(projetoMapper.toResponse(any())).thenReturn(projetoResponsePadrao);

            projetoService.transicionarStatus(1L, request);

            assertThat(projetoPadrao.getStatus()).isEqualTo(StatusProjeto.CANCELADO);
        }
    }


    @Nested
    @DisplayName("Associação de membros")
    class AssociacaoMembros {

        @Test
        @DisplayName("Deve associar funcionário ao projeto")
        void deveAssociarFuncionarioAoProjeto() {
            AssociacaoMembroRequest request = new AssociacaoMembroRequest(1L);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(membroRepository.findById(1L)).thenReturn(Optional.of(membroPadrao));
            when(projetoRepository.countProjetosAtivosDoMembro(1L)).thenReturn(0L);
            when(projetoRepository.save(any())).thenReturn(projetoPadrao);
            when(projetoMapper.toResponse(any())).thenReturn(projetoResponsePadrao);

            projetoService.associarMembro(1L, request);

            assertThat(projetoPadrao.getMembros()).contains(membroPadrao);
        }

        @Test
        @DisplayName("NÃO deve associar membro com atribuição diferente de funcionário")
        void naoDeveAssociarMembroNaoFuncionario() {
            Membro gerente = Membro.builder().id(2L).nome("Carlos").atribuicao("gerente").build();
            AssociacaoMembroRequest request = new AssociacaoMembroRequest(2L);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(membroRepository.findById(2L)).thenReturn(Optional.of(gerente));

            assertThatThrownBy(() -> projetoService.associarMembro(1L, request))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("funcionário");
        }

        @Test
        @DisplayName("NÃO deve associar membro que já está no projeto")
        void naoDeveAssociarMembroJaAssociado() {
            projetoPadrao.getMembros().add(membroPadrao);
            AssociacaoMembroRequest request = new AssociacaoMembroRequest(1L);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(membroRepository.findById(1L)).thenReturn(Optional.of(membroPadrao));

            assertThatThrownBy(() -> projetoService.associarMembro(1L, request))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("já está associado");
        }

        @Test
        @DisplayName("NÃO deve associar quando projeto já tem 10 membros")
        void naoDeveAssociarQuandoProjetoEstaLotado() {
            for (int i = 0; i < 10; i++) {
                projetoPadrao.getMembros().add(
                    Membro.builder().id((long)(i+10)).nome("Membro " + i).atribuicao("funcionário").build()
                );
            }
            AssociacaoMembroRequest request = new AssociacaoMembroRequest(1L);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(membroRepository.findById(1L)).thenReturn(Optional.of(membroPadrao));

            assertThatThrownBy(() -> projetoService.associarMembro(1L, request))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("limite máximo");
        }

        @Test
        @DisplayName("NÃO deve associar membro que já está em 3 projetos ativos")
        void naoDeveAssociarMembroEm3ProjetosAtivos() {
            AssociacaoMembroRequest request = new AssociacaoMembroRequest(1L);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(membroRepository.findById(1L)).thenReturn(Optional.of(membroPadrao));
            when(projetoRepository.countProjetosAtivosDoMembro(1L)).thenReturn(3L);

            assertThatThrownBy(() -> projetoService.associarMembro(1L, request))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("3 projetos ativos");
        }

        @Test
        @DisplayName("Deve desassociar membro do projeto")
        void deveDesassociarMembroDoProjeto() {
            projetoPadrao.getMembros().add(membroPadrao);
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(membroRepository.findById(1L)).thenReturn(Optional.of(membroPadrao));
            when(projetoRepository.save(any())).thenReturn(projetoPadrao);
            when(projetoMapper.toResponse(any())).thenReturn(projetoResponsePadrao);

            projetoService.desassociarMembro(1L, 1L);

            assertThat(projetoPadrao.getMembros()).doesNotContain(membroPadrao);
        }

        @Test
        @DisplayName("Deve lançar exceção ao desassociar membro que não está no projeto")
        void deveLancarExcecaoDesassociarMembroNaoAssociado() {
            when(projetoRepository.findById(1L)).thenReturn(Optional.of(projetoPadrao));
            when(membroRepository.findById(1L)).thenReturn(Optional.of(membroPadrao));

            assertThatThrownBy(() -> projetoService.desassociarMembro(1L, 1L))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("não está associado");
        }
    }

    @Nested
    @DisplayName("Geração de relatório")
    class Relatorio {

        @Test
        @DisplayName("Deve gerar relatório com dados corretos")
        void deveGerarRelatorio() {
            Projeto encerrado = Projeto.builder()
                    .id(2L)
                    .status(StatusProjeto.ENCERRADO)
                    .orcamentoTotal(new BigDecimal("200000.00"))
                    .dataInicio(LocalDate.now().minusMonths(3))
                    .dataRealTermino(LocalDate.now())
                    .membros(new HashSet<>(Set.of(membroPadrao)))
                    .build();

            when(projetoRepository.findAll()).thenReturn(List.of(projetoPadrao, encerrado));
            when(projetoRepository.findEncerrados()).thenReturn(List.of(encerrado));

            RelatorioPortfolioResponse relatorio = projetoService.gerarRelatorio();

            assertThat(relatorio).isNotNull();
            assertThat(relatorio.projetosPorStatus()).containsKey(StatusProjeto.EM_ANALISE);
            assertThat(relatorio.projetosPorStatus()).containsKey(StatusProjeto.ENCERRADO);
            assertThat(relatorio.totalMembrosUnicosAlocados()).isEqualTo(1L);
            assertThat(relatorio.mediaDuracaoProjetosEncerradosDias()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Deve retornar média zero quando não há projetos encerrados com data real")
        void deveRetornarMediaZeroSemProjetosEncerrados() {
            when(projetoRepository.findAll()).thenReturn(List.of(projetoPadrao));
            when(projetoRepository.findEncerrados()).thenReturn(List.of());

            RelatorioPortfolioResponse relatorio = projetoService.gerarRelatorio();

            assertThat(relatorio.mediaDuracaoProjetosEncerradosDias()).isEqualTo(0.0);
        }
    }
}
