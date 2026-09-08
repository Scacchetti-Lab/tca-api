package com.api.tca.domain.meeting.service;

import com.api.tca.common.ai.dto.request.MeetingEmbeddingRequest;
import com.api.tca.common.ai.provider.MeetingAnalyseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
public class MeetingListenerService {

    @Autowired
    private MeetingAnalyseProvider provider;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onMeetingCreated(MeetingEmbeddingRequest event) {
        provider.createEmbedds(event.meetingId());
    }
}
