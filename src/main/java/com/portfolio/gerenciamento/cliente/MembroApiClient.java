package com.portfolio.gerenciamento.cliente;

import com.portfolio.gerenciamento.dto.request.MembroCriacaoRequest;
import com.portfolio.gerenciamento.dto.response.MembroExternoResponse;
import com.portfolio.gerenciamento.exception.IntegracaoExternaException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MembroApiClient {


    // Mock em memória para simular API externa
    private static final ConcurrentHashMap<String, MembroExternoResponse>
            MOCK_STORAGE = new ConcurrentHashMap<>();

    public MembroApiClient() {
    }


        public MembroExternoResponse criarMembro (MembroCriacaoRequest request){
            String id = UUID.randomUUID().toString();
            MembroExternoResponse response = new MembroExternoResponse(id, request.nome(), request.atribuicao());
            MOCK_STORAGE.put(id, response);
            return response;
        }

        public MembroExternoResponse buscarMembroPorId (String id){
            MembroExternoResponse response = MOCK_STORAGE.get(id);
            if (response == null) {
                throw new IntegracaoExternaException("Membro com id " + id + " não encontrado na API externa");
            }
            return response;
        }


        public List<MembroExternoResponse> listarMembros () {

            return List.copyOf(MOCK_STORAGE.values());
        }
    }
