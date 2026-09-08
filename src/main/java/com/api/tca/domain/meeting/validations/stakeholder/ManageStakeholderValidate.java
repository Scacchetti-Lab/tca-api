package com.api.tca.domain.meeting.validations.stakeholder;

import com.api.tca.domain.meeting.entity.MeetingStakeholdersEntity;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.api.tca.domain.meeting.exception.MeetingValidateException;
import com.api.tca.domain.meeting.repository.MeetingRepository;
import com.api.tca.domain.meeting.repository.MeetingStakeHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageStakeholderValidate implements StakeholderValidate {

    @Autowired
    private MeetingStakeHolderRepository stakeholderRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    public void validate(MeetingStakeholdersEntity entity) {
        if (stakeholderRepository.existsByNameAndMeetingId(entity.getName(), entity.getMeeting().getId())) {
            throw new MeetingValidateException("Stakeholder já existe nessa reunião");
        }

        if (entity.getMeeting().getStatus() != MeetingStatus.SCHEDULED) {
            throw new MeetingValidateException("Somente em reuniões agendadas é possível remover ou adicionar stakeholders");
        }
    }
}
