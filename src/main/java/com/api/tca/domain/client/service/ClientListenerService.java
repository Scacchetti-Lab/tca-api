package com.api.tca.domain.client.service;

import com.api.tca.common.ai.dto.request.ClientEmbeddingDto;
import com.api.tca.common.ai.dto.request.predict.PredictEventRequestDto;
import com.api.tca.common.ai.provider.ClientAnalyseProvider;
import com.api.tca.common.exception.custom.FailOnPredictException;
import com.api.tca.domain.meeting.enums.PredictProcessStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Log4j2
@Service
public class ClientListenerService {

    @Autowired
    private ClientAnalyseProvider provider;

    @Autowired
    private ClientService clientService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onClientCreated(ClientEmbeddingDto event) {
        provider.addClientEmbeds(event.clientId());
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onClientPredictCreated(PredictEventRequestDto event) {
        try {
            clientService.updatePredictStatus(event.entityId(), PredictProcessStatus.PROCESSING);
            var response = provider.predictClientStatus(event.entityId());
            if (!response.isValid())
                throw new FailOnPredictException(response.message());

            var predictEntity = clientService.getPredictByClientId(event.entityId());

            clientService.addPredictData(predictEntity, response.content().predict(), event.reprocess());
        }
        catch (Exception ex) {
            log.error("Falha ao processar predição {}", event.entityId(), ex);
            clientService.updatePredictStatus(event.entityId(), PredictProcessStatus.CANCELLED);
        }
    }
}
