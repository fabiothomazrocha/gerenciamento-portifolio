package com.portfolio.gerenciamento.controller;

import com.portfolio.gerenciamento.dto.request.AssociacaoMembroRequest;
import com.portfolio.gerenciamento.dto.request.ProjetoAtualizacaoRequest;
import com.portfolio.gerenciamento.dto.request.ProjetoCriacaoRequest;
import com.portfolio.gerenciamento.dto.request.StatusTransicaoRequest;
import com.portfolio.gerenciamento.dto.response.ProjetoResponse;
import com.portfolio.gerenciamento.dto.response.RelatorioPortfolioResponse;
import com.portfolio.gerenciamento.enums.StatusProjeto;
import com.portfolio.gerenciamento.service.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projetos")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "CRUD completo e gerenciamento do ciclo de vida de projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    @PostMapping
    @Operation(summary = "Criar projeto", description = "Cria um novo projeto com status inicial 'Em análise'")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "422", description = "Violação de regra de negócio")
    })
    public ResponseEntity<ProjetoResponse> criar(@Valid @RequestBody ProjetoCriacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.criar(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
        @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<ProjetoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar projetos com paginação e filtros",
               description = "Retorna lista paginada de projetos, filtrável por nome, status e gerente")
    public ResponseEntity<Page<ProjetoResponse>> listar(
            @Parameter(description = "Filtro por nome (busca parcial, case-insensitive)")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Filtro por status")
            @RequestParam(required = false) StatusProjeto status,

            @Parameter(description = "Filtro por ID do gerente")
            @RequestParam(required = false) Long gerenteId,

            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(projetoService.listar(nome, status, gerenteId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto", description = "Atualiza dados do projeto (campos não informados são ignorados)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Projeto atualizado"),
        @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
        @ApiResponse(responseCode = "422", description = "Violação de regra de negócio")
    })
    public ResponseEntity<ProjetoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProjetoAtualizacaoRequest request) {
        return ResponseEntity.ok(projetoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto",
               description = "Exclui projeto. Não permitido para status: Iniciado, Em andamento ou Encerrado")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Projeto excluído"),
        @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
        @ApiResponse(responseCode = "422", description = "Projeto não pode ser excluído no status atual")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transicionar status",
               description = "Avança o status do projeto na sequência definida. CANCELADO pode ser aplicado a qualquer momento.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado"),
        @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    public ResponseEntity<ProjetoResponse> transicionarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusTransicaoRequest request) {
        return ResponseEntity.ok(projetoService.transicionarStatus(id, request));
    }

    @PostMapping("/{projetoId}/membros")
    @Operation(summary = "Associar membro ao projeto",
               description = "Associa um membro (somente funcionários). Limite: 10 por projeto, 3 projetos ativos por membro.")
    public ResponseEntity<ProjetoResponse> associarMembro(
            @PathVariable Long projetoId,
            @Valid @RequestBody AssociacaoMembroRequest request) {
        return ResponseEntity.ok(projetoService.associarMembro(projetoId, request));
    }

    @DeleteMapping("/{projetoId}/membros/{membroId}")
    @Operation(summary = "Desassociar membro do projeto")
    public ResponseEntity<ProjetoResponse> desassociarMembro(
            @PathVariable Long projetoId,
            @PathVariable Long membroId) {
        return ResponseEntity.ok(projetoService.desassociarMembro(projetoId, membroId));
    }

    @GetMapping("/relatorio")
    @Operation(summary = "Gerar relatório do portfólio",
               description = "Retorna resumo com: projetos por status, total orçado por status, média de duração e membros únicos alocados")
    public ResponseEntity<RelatorioPortfolioResponse> gerarRelatorio() {
        return ResponseEntity.ok(projetoService.gerarRelatorio());
    }
}
