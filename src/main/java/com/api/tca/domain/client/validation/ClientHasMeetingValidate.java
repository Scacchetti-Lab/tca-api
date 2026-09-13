package com.api.tca.domain.client.validation;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.meeting.dto.response.MeetingBasicDataDto;
import com.api.tca.domain.meeting.exception.rules.MeetingNotFoundException;
import com.api.tca.domain.meeting.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ClientHasMeetingValidate implements ClientPredictValidate {

    @Autowired
    private MeetingRepository meetingRepository;

    public void validate(ClientEntity client) {
        var data = meetingRepository.findAllByClientIdAndIsDeletedFalse(client.getId()).stream().map(MeetingBasicDataDto::new).collect(Collectors.toSet());
        if (data.isEmpty()) throw new MeetingNotFoundException("Esse cliente não possuí reuniões");

        if (data.size() == 1) {
            throw new MeetingNotFoundException("Impossível prever esse cliente, ele não possuí um histórico de reuniões.");
        }
    }
}
