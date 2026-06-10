package com.portfolio.gerenciamento.service.impl;

import com.portfolio.gerenciamento.cliente.MembroApiClient;
import com.portfolio.gerenciamento.dto.request.MembroCriacaoRequest;
import com.portfolio.gerenciamento.dto.response.MembroExternoResponse;
import com.portfolio.gerenciamento.dto.response.MembroResponse;
import com.portfolio.gerenciamento.entity.Membro;
import com.portfolio.gerenciamento.mapper.MembroMapper;
import com.portfolio.gerenciamento.repository.MembroRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjetoService - Regras de Negócio")
public class MembroServiceImplTest {
    @Mock
    MembroApiClient membroApiClient;
    @Mock
    MembroRepository membroRepository;
    @Mock
    MembroMapper membroMapper;
    @InjectMocks
    MembroServiceImpl membroServiceImpl;

    @Test
    void testCriarMembro() {
        when(membroApiClient.criarMembro(any(MembroCriacaoRequest.class))).thenReturn(new MembroExternoResponse("id", "nome", "atribuicao"));
        when(membroRepository.save(any(Membro.class))).thenReturn(new Membro());
        when(membroMapper.toResponse(any(Membro.class))).thenReturn(new MembroResponse(1L, "idExterno", "nome", "atribuicao", LocalDateTime.of(2026, Month.JUNE, 10, 15, 15, 12)));

        MembroResponse result = membroServiceImpl.criarMembro(new MembroCriacaoRequest("nome", "atribuicao"));
        Assertions.assertEquals(new MembroResponse(1L, "idExterno", "nome", "atribuicao", LocalDateTime.of(2026, Month.JUNE, 10, 15, 15, 12)), result);
    }

    @Test
    void testBuscarPorId() {
        when(membroRepository.findById(anyLong())).thenReturn(Optional.of(new Membro()));
        when(membroMapper.toResponse(any(Membro.class))).thenReturn(new MembroResponse(1L, "idExterno", "nome", "atribuicao", LocalDateTime.of(2026, Month.JUNE, 10, 15, 15, 12)));

        MembroResponse result = membroServiceImpl.buscarPorId(1L);
        Assertions.assertEquals(new MembroResponse(1L, "idExterno", "nome", "atribuicao", LocalDateTime.of(2026, Month.JUNE, 10, 15, 15, 12)), result);
    }

    @Test
    void testListarTodos() {
        when(membroRepository.findAll()).thenReturn(List.of(new Membro(1L,
                "idExterno",
                "nome",
                "atribuicao",
                LocalDateTime.of(2026, Month.JUNE, 10, 15, 15, 12),
                Set.of())));

        List<MembroResponse> result = membroServiceImpl.listarTodos();
        Assertions.assertFalse(result.isEmpty());
    }
}