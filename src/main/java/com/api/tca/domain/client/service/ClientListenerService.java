package com.api.tca.domain.client.service;

import com.api.tca.common.ai.dto.request.ClientEmbeddingDto;
import com.api.tca.common.ai.provider.ClientAnalyseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
public class ClientListenerService {

    @Autowired
    private ClientAnalyseProvider provider;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onClientCreated(ClientEmbeddingDto event) {
        provider.addClientEmbeds(event.clientId());
    }
}
