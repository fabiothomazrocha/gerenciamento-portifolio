package com.portfolio.gerenciamento.service.impl;

import com.portfolio.gerenciamento.cliente.MembroApiClient;
import com.portfolio.gerenciamento.dto.request.MembroCriacaoRequest;
import com.portfolio.gerenciamento.dto.response.MembroExternoResponse;
import com.portfolio.gerenciamento.dto.response.MembroResponse;
import com.portfolio.gerenciamento.entity.Membro;
import com.portfolio.gerenciamento.exception.RecursoNaoEncontradoException;
import com.portfolio.gerenciamento.mapper.MembroMapper;
import com.portfolio.gerenciamento.repository.MembroRepository;
import com.portfolio.gerenciamento.service.MembroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembroServiceImpl implements MembroService {

    private final MembroApiClient membroApiClient;
    private final MembroRepository membroRepository;
    private final MembroMapper membroMapper;

    @Override
    @Transactional
    public MembroResponse criarMembro(MembroCriacaoRequest request) {
        log.info("Criando membro via API externa: {}", request.nome());

        // Chama API externa (mockada)
        MembroExternoResponse externo = membroApiClient.criarMembro(request);

        // Persiste localmente como cache
        Membro membro = Membro.builder()
                .idExterno(externo.id())
                .nome(externo.nome())
                .atribuicao(externo.atribuicao())
                .build();

        Membro salvo = membroRepository.save(membro);
        log.info("Membro criado com id local: {}", salvo.getId());
        return membroMapper.toResponse(salvo);
    }

    @Override
    public MembroResponse buscarPorId(Long id) {
        return membroMapper.toResponse(findMembro(id));
    }

    @Override
    public List<MembroResponse> listarTodos() {
        return membroRepository.findAll()
                .stream()
                .map(membroMapper::toResponse)
                .toList();
    }

    private Membro findMembro(Long id) {
        return membroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Membro", id));
    }
}
