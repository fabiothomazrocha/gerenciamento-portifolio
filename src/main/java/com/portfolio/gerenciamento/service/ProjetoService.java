package com.portfolio.gerenciamento.service;
import com.portfolio.gerenciamento.dto.request.AssociacaoMembroRequest;
import com.portfolio.gerenciamento.dto.request.ProjetoAtualizacaoRequest;
import com.portfolio.gerenciamento.dto.request.ProjetoCriacaoRequest;
import com.portfolio.gerenciamento.dto.request.StatusTransicaoRequest;
import com.portfolio.gerenciamento.dto.response.ProjetoResponse;
import com.portfolio.gerenciamento.dto.response.RelatorioPortfolioResponse;
import com.portfolio.gerenciamento.enums.StatusProjeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ProjetoService {

    ProjetoResponse criar(ProjetoCriacaoRequest request);

    ProjetoResponse buscarPorId(Long id);

    Page<ProjetoResponse> listar(String nome, StatusProjeto status, Long gerenteId, Pageable pageable);

    ProjetoResponse atualizar(Long id, ProjetoAtualizacaoRequest request);

    void excluir(Long id);

    ProjetoResponse transicionarStatus(Long id, StatusTransicaoRequest request);

    ProjetoResponse associarMembro(Long projetoId, AssociacaoMembroRequest request);

    ProjetoResponse desassociarMembro(Long projetoId, Long membroId);

    RelatorioPortfolioResponse gerarRelatorio();

}
