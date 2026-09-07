package com.api.tca.domain.meeting.mapper;

import com.api.tca.domain.meeting.dto.request.UpdateMeetingDto;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeetingMapper {

    @Mapping(target = "id", ignore = true)
    MeetingEntity mapUpdateMeetingDtoToMeetingEntity(UpdateMeetingDto updateMeetingDto, @MappingTarget MeetingEntity meetingEntity);
}
