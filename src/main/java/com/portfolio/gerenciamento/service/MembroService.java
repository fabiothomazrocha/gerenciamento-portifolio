package com.portfolio.gerenciamento.service;

import com.portfolio.gerenciamento.dto.request.MembroCriacaoRequest;
import com.portfolio.gerenciamento.dto.response.MembroResponse;

import java.util.List;

public interface MembroService {

    MembroResponse criarMembro(MembroCriacaoRequest request);

    MembroResponse buscarPorId(Long id);

    List<MembroResponse> listarTodos();

}
