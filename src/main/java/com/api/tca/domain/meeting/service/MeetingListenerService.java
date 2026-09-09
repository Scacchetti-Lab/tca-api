package com.api.tca.domain.meeting.service;

import com.api.tca.common.ai.dto.request.MeetingTranscriptProcessDto;
import com.api.tca.common.ai.dto.request.TranscriptEmbeddingDto;
import com.api.tca.common.ai.provider.MeetingAnalyseProvider;
import com.api.tca.common.ai.provider.TranscriptProvider;
import com.api.tca.domain.transcript.enums.TranscriptStatus;
import com.api.tca.domain.transcript.exceptions.TranscriptProcessErrorException;
import com.api.tca.domain.transcript.service.TranscriptService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
            log.error("Falha ao processar transcrição {}", transcriptRequest.transcriptId(), ex);
            transcriptService.updateStatus(transcriptRequest.transcriptId(), TranscriptStatus.ERROR);
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onMeetingUpdated(UUID meetingId) {
        meetingProvider.createEmbeds(meetingId);
    }

    // TODO: Método para excluir todas as referências de transcrição caso ocorra algum erro
}
