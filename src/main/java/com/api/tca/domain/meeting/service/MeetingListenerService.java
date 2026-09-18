package com.api.tca.domain.meeting.service;

import com.api.tca.common.ai.dto.request.MeetingTranscriptProcessDto;
import com.api.tca.common.ai.dto.request.TranscriptEmbeddingDto;
import com.api.tca.common.ai.dto.request.predict.PredictEventRequestDto;
import com.api.tca.common.ai.provider.MeetingAnalyseProvider;
import com.api.tca.common.ai.provider.TranscriptProvider;
import com.api.tca.common.exception.custom.FailOnPredictException;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.enums.PredictProcessStatus;
import com.api.tca.domain.score.service.PerformanceScoreService;
import com.api.tca.domain.transcript.enums.TranscriptStatus;
import com.api.tca.domain.transcript.exceptions.TranscriptProcessErrorException;
import com.api.tca.domain.transcript.service.TranscriptService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Log4j2
@Service
public class MeetingListenerService {

    @Autowired
    private MeetingAnalyseProvider meetingProvider;

    @Autowired
    private TranscriptProvider transcriptProvider;

    @Lazy
    @Autowired
    private MeetingService meetingService;

    @Autowired
    private TranscriptService transcriptService;

    @Autowired
    private PerformanceScoreService performanceService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onMeetingCreated(MeetingTranscriptProcessDto event) {
        var transcriptRequest = event.transcriptRequest();
        try {
            transcriptService.updateStatus(transcriptRequest.transcriptId(), TranscriptStatus.IN_PROGRESS);

            var process = transcriptProvider.processTranscript(event.transcriptRequest());
            if (!process.isValid()) throw new TranscriptProcessErrorException(process.message());

            meetingService.saveAnalyses(event.meetingId(), process.content());
            transcriptProvider.generateEmbedChunks(new TranscriptEmbeddingDto(transcriptRequest.transcriptId()));
            transcriptService.updateStatus(transcriptRequest.transcriptId(), TranscriptStatus.DONE);

            meetingProvider.createEmbeds(event.meetingId());
        } catch (Exception ex) {
            if (transcriptRequest != null) log.error("Falha ao processar transcrição {}", transcriptRequest.transcriptId(), ex);
            else log.error("Falha ao processar transcrição", ex);
            transcriptService.updateStatus(transcriptRequest.transcriptId(), TranscriptStatus.ERROR);
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onMeetingUpdated(UUID meetingId) {
        meetingProvider.createEmbeds(meetingId);
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onMeetingPredictCreated(PredictEventRequestDto event) {
        try {
            meetingService.updatePredictStatus(event.entityId(), PredictProcessStatus.PROCESSING);
            var response = meetingProvider.predictFutureMeetings(event.entityId());
            if (!response.isValid())
                throw new FailOnPredictException(response.message());

            var predictEntity = meetingService.getPredictByMeetingId(event.entityId());

            meetingService.addPredictData(predictEntity, response.content().predict(), event.reprocess());
        }
        catch (Exception ex) {
            log.error("Falha ao processar predição {}", event.entityId(), ex);
            meetingService.updatePredictStatus(event.entityId(), PredictProcessStatus.CANCELLED);
        }
    }
}
