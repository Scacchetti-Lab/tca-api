package com.api.tca.domain.user.mapper;

import com.api.tca.domain.user.dto.user.UpdateUserDto;
import com.api.tca.domain.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    void mapUpdateDtoToUserEntity(UpdateUserDto updateDto, @MappingTarget UserEntity user);
}
