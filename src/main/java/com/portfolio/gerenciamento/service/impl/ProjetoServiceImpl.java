package com.portfolio.gerenciamento.service.impl;

import com.portfolio.gerenciamento.dto.request.AssociacaoMembroRequest;
import com.portfolio.gerenciamento.dto.request.ProjetoAtualizacaoRequest;
import com.portfolio.gerenciamento.dto.request.ProjetoCriacaoRequest;
import com.portfolio.gerenciamento.dto.request.StatusTransicaoRequest;
import com.portfolio.gerenciamento.dto.response.ProjetoResponse;
import com.portfolio.gerenciamento.dto.response.RelatorioPortfolioResponse;
import com.portfolio.gerenciamento.entity.Membro;
import com.portfolio.gerenciamento.entity.Projeto;
import com.portfolio.gerenciamento.enums.StatusProjeto;
import com.portfolio.gerenciamento.exception.*;
import com.portfolio.gerenciamento.mapper.ProjetoMapper;
import com.portfolio.gerenciamento.repository.MembroRepository;
import com.portfolio.gerenciamento.repository.ProjetoRepository;
import com.portfolio.gerenciamento.service.ProjetoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjetoServiceImpl implements ProjetoService {

    private static final int MAX_MEMBROS_POR_PROJETO = 10;
    private static final int MIN_MEMBROS_POR_PROJETO = 1;
    private static final int MAX_PROJETOS_ATIVOS_POR_MEMBRO = 3;

    private final ProjetoRepository projetoRepository;
    private final MembroRepository membroRepository;
    private final ProjetoMapper projetoMapper;

    @Override
    @Transactional
    public ProjetoResponse criar(ProjetoCriacaoRequest request) {
        log.info("Criando projeto: {}", request.nome());
        validaData(request.dataInicio(), request.previsaoTermino());

        Projeto projeto = projetoMapper.toProjeto(request);
        projeto.setStatus(StatusProjeto.EM_ANALISE);

        if (request.gerenteId() != null) {
            Membro gerente = findMembro(request.gerenteId());
            projeto.setGerente(gerente);
        }

        Projeto salvo = projetoRepository.save(projeto);
        log.info("Projeto criado com id: {}", salvo.getId());
        return projetoMapper.toResponse(salvo);
    }

    @Override
    public ProjetoResponse buscarPorId(Long id) {
        return projetoMapper.toResponse(findProjeto(id));
    }

    @Override
    public Page<ProjetoResponse> listar(String nome, StatusProjeto status, Long gerenteId, Pageable pageable) {
        return projetoRepository.findByFiltros(nome, status, gerenteId, pageable)
                .map(projetoMapper::toResponse);
    }

    @Override
    @Transactional
    public ProjetoResponse atualizar(Long id, ProjetoAtualizacaoRequest request) {
        log.info("Atualizando projeto id: {}", id);
        Projeto projeto = findProjeto(id);

        if (request.nome() != null) {
            projeto.setNome(request.nome());
        }
        if (request.dataInicio() != null) {
            projeto.setDataInicio(request.dataInicio());
        }
        if (request.previsaoTermino() != null) {
            projeto.setPrevisaoTermino(request.previsaoTermino());
        }
        if (request.dataRealTermino() != null) {
            projeto.setDataRealTermino(request.dataRealTermino());
        }
        if (request.orcamentoTotal() != null) {
            projeto.setOrcamentoTotal(request.orcamentoTotal());
        }
        if (request.descricao() != null) {
            projeto.setDescricao(request.descricao());
        }

        if (request.gerenteId() != null) {
            Membro gerente = findMembro(request.gerenteId());
            projeto.setGerente(gerente);
        }

        validaData(projeto.getDataInicio(), projeto.getPrevisaoTermino());

        return projetoMapper.toResponse(projetoRepository.save(projeto));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo projeto id: {}", id);
        Projeto projeto = findProjeto(id);

        if (StatusProjeto.statusImpedemExclusao().contains(projeto.getStatus())) {
            throw new RegraDeNegocioException(
                    "Projetos com status '" + projeto.getStatus().getDescricao()
                            + "' não podem ser excluídos.");
        }

        projetoRepository.delete(projeto);
    }

    @Override
    @Transactional
    public ProjetoResponse transicionarStatus(Long id, StatusTransicaoRequest request) {
        Projeto projeto = findProjeto(id);
        StatusProjeto statusAtual = projeto.getStatus();
        StatusProjeto novoStatus = request.novoStatus();

        log.info("Transicionando projeto {} de {} para {}", id, statusAtual, novoStatus);

        if (!statusAtual.podeTransicionarPara(novoStatus)) {
            throw new TransicaoStatusInvalidaException(statusAtual, novoStatus);
        }

        projeto.setStatus(novoStatus);
        return projetoMapper.toResponse(projetoRepository.save(projeto));
    }

    @Override
    @Transactional
    public ProjetoResponse associarMembro(Long projetoId, AssociacaoMembroRequest request) {
        Projeto projeto = findProjeto(projetoId);
        Membro membro = findMembro(request.membroId());

        log.info("Associando membro {} ao projeto {}", membro.getId(), projetoId);

        if (!membro.isFuncionario()) {
            throw new RegraDeNegocioException(
                    "Apenas membros com atribuição 'funcionário' podem ser associados a projetos. " +
                            "Atribuição atual: " + membro.getAtribuicao());
        }

        if (projeto.getMembros().contains(membro)) {
            throw new RegraDeNegocioException("Membro já está associado a este projeto.");
        }


        if (projeto.getMembros().size() >= MAX_MEMBROS_POR_PROJETO) {
            throw new RegraDeNegocioException(
                    "Projeto já atingiu o limite máximo de " + MAX_MEMBROS_POR_PROJETO + " membros.");
        }

        long projetosAtivos = projetoRepository.countProjetosAtivosDoMembro(membro.getId());
        if (projetosAtivos >= MAX_PROJETOS_ATIVOS_POR_MEMBRO) {
            throw new RegraDeNegocioException(
                    "Membro já está alocado em " + MAX_PROJETOS_ATIVOS_POR_MEMBRO
                            + " projetos ativos. Não é possível adicionar em mais projetos.");
        }

        projeto.getMembros().add(membro);
        return projetoMapper.toResponse(projetoRepository.save(projeto));
    }

    @Override
    @Transactional
    public ProjetoResponse desassociarMembro(Long projetoId, Long membroId) {
        Projeto projeto = findProjeto(projetoId);
        Membro membro = findMembro(membroId);

        log.info("Desassociando membro {} do projeto {}", membroId, projetoId);

        if (!projeto.getMembros().contains(membro)) {
            throw new RegraDeNegocioException("Membro não está associado a este projeto.");
        }

        if (projeto.getMembros().size() <= MIN_MEMBROS_POR_PROJETO
                && !Set.of(StatusProjeto.CANCELADO, StatusProjeto.ENCERRADO,
                StatusProjeto.EM_ANALISE, StatusProjeto.ANALISE_REALIZADA,
                StatusProjeto.ANALISE_APROVADA).contains(projeto.getStatus())) {
            throw new RegraDeNegocioException(
                    "Não é possível remover o último membro de um projeto ativo. " +
                            "O projeto deve ter pelo menos 1 membro.");
        }

        projeto.getMembros().remove(membro);
        return projetoMapper.toResponse(projetoRepository.save(projeto));
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioPortfolioResponse gerarRelatorio() {
        log.info("Gerando relatório de portfólio");

        List<Projeto> todos = projetoRepository.findAll();

        Map<StatusProjeto, Long> projetosPorStatus = todos.stream()
                .collect(Collectors.groupingBy(Projeto::getStatus, Collectors.counting()));


        Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus = todos.stream()
                .collect(Collectors.groupingBy(
                        Projeto::getStatus,
                        Collectors.reducing(BigDecimal.ZERO,
                                Projeto::getOrcamentoTotal,
                                BigDecimal::add)));


        List<Projeto> encerrados = projetoRepository.findEncerrados();
        Double mediaDuracao = encerrados.stream()
                .filter(p -> p.getDataRealTermino() != null)
                .mapToLong(p -> ChronoUnit.DAYS.between(p.getDataInicio(), p.getDataRealTermino()))
                .average()
                .orElse(0.0);
        long totalMembrosUnicos = todos.stream()
                .flatMap(p -> p.getMembros().stream())
                .map(Membro::getId)
                .distinct()
                .count();

        return new RelatorioPortfolioResponse(
                projetosPorStatus,
                totalOrcadoPorStatus,
                mediaDuracao,
                totalMembrosUnicos
        );
    }


    private Projeto findProjeto(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto", id));
    }

    private Membro findMembro(Long id) {
        return membroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Membro", id));
    }

    private void validaData(java.time.LocalDate inicio, java.time.LocalDate previsao) {
        if (previsao != null && inicio != null && previsao.isBefore(inicio)) {
            throw new RegraDeNegocioException(
                    "A previsão de término não pode ser anterior à data de início.");
        }
    }
}
