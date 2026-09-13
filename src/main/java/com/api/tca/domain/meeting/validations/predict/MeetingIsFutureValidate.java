package com.api.tca.domain.meeting.validations.predict;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.api.tca.domain.meeting.exception.rules.MeetingValidateException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public final class MeetingIsFutureValidate implements MeetingPredictValidation {
    public void validate(MeetingEntity entity) {
        LocalDateTime now = BrazilRealTime.now();
        LocalDateTime scheduled = entity.getScheduled();
        if (scheduled.isBefore(now)) {
            throw new MeetingValidateException("Somente reuniões futuras podem ter previsões");
        }
        if (entity.getStatus() != MeetingStatus.SCHEDULED) {
            throw new MeetingValidateException("Somente reuniões agendadas podem receber previsões");
        }
    }
}
