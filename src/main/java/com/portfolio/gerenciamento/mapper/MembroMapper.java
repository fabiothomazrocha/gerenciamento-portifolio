package com.portfolio.gerenciamento.mapper;

import com.portfolio.gerenciamento.dto.response.MembroResponse;
import com.portfolio.gerenciamento.entity.Membro;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MembroMapper {

    MembroResponse toResponse(Membro membro);

    Set<MembroResponse> toResponseSet(Set<Membro> membros);
}
