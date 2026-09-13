package com.api.tca.domain.meeting.validations.predict;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.exception.rules.MeetingValidateException;
import com.api.tca.domain.meeting.repository.MeetingPredictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PredictAlreadyReprocessedValidate implements MeetingPredictValidation {

    @Autowired
    private MeetingPredictRepository predictRepository;

    public void validate(MeetingEntity entity) {
        var predictEntity = predictRepository.findByMeetingId(entity.getId()).orElse(null);
        if (predictEntity == null) return;
        if (predictEntity.getReprocess())
            throw new MeetingValidateException("A previsão pode ser reprocessada somente 1x");
    }
}
