package com.portfolio.gerenciamento.mapper;

import com.portfolio.gerenciamento.dto.request.ProjetoCriacaoRequest;
import com.portfolio.gerenciamento.dto.response.ProjetoResponse;
import com.portfolio.gerenciamento.entity.Projeto;
import com.portfolio.gerenciamento.service.RiscoCalculator;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {MembroMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ProjetoMapper {

    @Autowired
    protected RiscoCalculator riscoCalculator;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "gerente", ignore = true)
    @Mapping(target = "membros", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    public abstract Projeto toProjeto(ProjetoCriacaoRequest request);

    @Mapping(target = "statusDescricao", expression = "java(projeto.getStatus().getDescricao())")
    @Mapping(target = "classificacaoRisco",
             expression = "java(riscoCalculator.calcular(projeto.getOrcamentoTotal(), projeto.getDataInicio(), projeto.getPrevisaoTermino()))")
    @Mapping(target = "classificacaoRiscoDescricao",
             expression = "java(riscoCalculator.calcular(projeto.getOrcamentoTotal(), projeto.getDataInicio(), projeto.getPrevisaoTermino()).getDescricao())")
    @Mapping(target = "totalMembros", expression = "java(projeto.getMembros().size())")
    public abstract ProjetoResponse toResponse(Projeto projeto);
}
