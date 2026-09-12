package com.api.tca.domain.meeting.validations.meetings;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.api.tca.domain.meeting.exception.rules.MeetingValidateException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public final class MeetingScheduleValidate implements MeetingValidate {

    public void validate(MeetingEntity meetingEntity) {
        if (meetingEntity.getStatus() == MeetingStatus.COMPLETED) return;

        if (meetingEntity.getStatus() == MeetingStatus.IN_PROGRESS)
            throw new MeetingValidateException("Reunião em andamento não pode ter o agendamento modificado");

        LocalDateTime scheduledAt = meetingEntity.getScheduled();
        if (scheduledAt == null)
            throw new MeetingValidateException("Reunião deve ter data e hora de agendamento");

        scheduledAt = BrazilRealTime.cast(scheduledAt);
        if (scheduledAt.isBefore(BrazilRealTime.now()))
            throw new MeetingValidateException("A reunião não pode ser agendada para uma data no passado.");
    }
}
