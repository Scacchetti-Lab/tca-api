package com.api.tca.domain.meeting.validations.predict;

import com.api.tca.domain.meeting.entity.MeetingEntity;

public interface MeetingPredictValidation {

    void validate(MeetingEntity entity);
}
