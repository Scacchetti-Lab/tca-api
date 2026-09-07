package com.api.tca.domain.meeting.dto.response;

import com.api.tca.domain.meeting.dto.score.PerformanceAnalyseDto;
import com.api.tca.domain.meeting.dto.score.StrategicAnalyseDto;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.entity.MeetingStakeholdersEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;
import java.util.UUID;

public record MeetingDetailedDto(
        UUID id,
        Integer totvsId,
        String title,
        String summary,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Set<MeetingStakeHolder> stakeholders,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Integer performanceAvg,

        String clientName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String clientRepresentor,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        PerformanceAnalyseDto performanceAnalyse,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        StrategicAnalyseDto strategicAnalyse
) {
        public MeetingDetailedDto(MeetingEntity entity, Set<MeetingStakeHolder> stakeholders) {
                this(
                        entity.getId(),
                        entity.getTotvsId(),
                        entity.getTitle(),
                        entity.getSummary(),
                        stakeholders,
                        entity.getPerformanceAvg(),
                        entity.getClient().getName(),
                        entity.getClientRepresent(),
                        entity.getMeetingAnalysePerformance() != null ? new PerformanceAnalyseDto(entity.getMeetingAnalysePerformance()) : null,
                        entity.getMeetingAnalyseStrategic() != null ? new StrategicAnalyseDto(entity.getMeetingAnalyseStrategic()) : null
                );
        }
}
