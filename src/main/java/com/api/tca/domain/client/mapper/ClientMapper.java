package com.api.tca.domain.client.mapper;

import com.api.tca.domain.client.dto.client.RegisterClientRequestDto;
import com.api.tca.domain.client.dto.client.UpdateClientDto;
import com.api.tca.domain.client.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    ClientEntity mapRegisterClientRequestDtoToClientEntity(RegisterClientRequestDto registerClientRequestDto);

    @Mapping(target = "id", ignore = true)
    ClientEntity mapUpdateClientDtoToClientEntity(UpdateClientDto updateClientDto, @MappingTarget ClientEntity clientEntity);

}
