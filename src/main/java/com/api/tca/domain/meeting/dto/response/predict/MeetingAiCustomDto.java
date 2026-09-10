package com.api.tca.domain.meeting.dto.response.predict;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.meeting.dto.score.PerformanceAnalyseDto;
import com.api.tca.domain.meeting.dto.score.StrategicAnalyseDto;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.enums.MeetingPriority;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record MeetingAiCustomDto(
        String title,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String totvsId,
        LocalDateTime scheduled,
        MeetingStatus status,
        MeetingPriority priority,
        MeetingClientPresentDto client,

        @JsonProperty("data")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        MeetingPredictDto nextMeeting,

        @JsonProperty("data")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        MeetingLastAnalyseDto lastMeeting
) {
    public MeetingAiCustomDto(MeetingEntity meeting) {
        this(
                meeting.getTitle(),
                meeting.getTotvsId(),
                meeting.getScheduled(),
                meeting.getStatus(),
                meeting.getPriority(),
                new MeetingClientPresentDto(meeting.getClient(), meeting.getClientRepresent()),
                null,
                new MeetingLastAnalyseDto(meeting)
        );
    }

    public MeetingAiCustomDto(MeetingEntity meeting, boolean disableAnalyse) {
        this(
                meeting.getTitle(),
                meeting.getTotvsId(),
                meeting.getScheduled(),
                meeting.getStatus(),
                meeting.getPriority(),
                new MeetingClientPresentDto(meeting.getClient(), meeting.getClientRepresent()),
                null,
                disableAnalyse ? new MeetingLastAnalyseDto(meeting) : null
        );
    }

    public MeetingAiCustomDto(MeetingEntity meeting, MeetingPredictDto predict) {
        this(
                meeting.getTitle(),
                meeting.getTotvsId(),
                meeting.getScheduled(),
                meeting.getStatus(),
                meeting.getPriority(),
                new MeetingClientPresentDto(meeting.getClient(), meeting.getClientRepresent()),
                predict,
                null
        );
    }
}
