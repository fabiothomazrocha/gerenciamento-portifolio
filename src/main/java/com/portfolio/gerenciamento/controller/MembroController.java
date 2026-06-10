package com.portfolio.gerenciamento.controller;

import com.portfolio.gerenciamento.dto.request.MembroCriacaoRequest;
import com.portfolio.gerenciamento.dto.response.MembroResponse;
import com.portfolio.gerenciamento.service.MembroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membros")
@RequiredArgsConstructor
@Tag(name = "Membros", description = "Gerenciamento de membros via API externa mockada")
public class MembroController {

    private final MembroService membroService;

    @PostMapping
    @Operation(summary = "Criar membro", description = "Cria um novo membro via API externa e persiste localmente")
    public ResponseEntity<MembroResponse> criar(@Valid @RequestBody MembroCriacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membroService.criarMembro(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar membro por ID")
    public ResponseEntity<MembroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(membroService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos os membros")
    public ResponseEntity<List<MembroResponse>> listarTodos() {
        return ResponseEntity.ok(membroService.listarTodos());
    }
}
