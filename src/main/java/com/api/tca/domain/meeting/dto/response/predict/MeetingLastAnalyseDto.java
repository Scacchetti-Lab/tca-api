package com.api.tca.domain.meeting.dto.response.predict;

import com.api.tca.domain.meeting.dto.score.PerformanceAnalyseDto;
import com.api.tca.domain.meeting.dto.score.StrategicAnalyseDto;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

public record MeetingLastAnalyseDto(
        String summary,
        Integer performance,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        PerformanceAnalyseDto performanceAnalyse,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        StrategicAnalyseDto strategicAnalyse
) {
    public MeetingLastAnalyseDto(MeetingEntity meeting) {
        this(
                meeting.getSummary(),
                meeting.getPerformanceAvg(),
                new PerformanceAnalyseDto(meeting.getMeetingAnalysePerformance()),
                new StrategicAnalyseDto(meeting.getMeetingAnalyseStrategic())
        );
    };
}
