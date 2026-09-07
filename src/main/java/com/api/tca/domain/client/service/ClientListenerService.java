package com.api.tca.domain.client.service;

import com.api.tca.common.ai.dto.request.ClientEmbeddingRequest;
import com.api.tca.common.ai.provider.ClientAnalyseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
public class ClientListenerService {

    @Autowired
    private ClientAnalyseProvider provider;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onClientCreated(ClientEmbeddingRequest event) {
        System.out.println("ENTREI NO EVENT LISTENER");
        var response = provider.addClientEmbeds(event.clientId());
        System.out.println("OPERACAO FINALIZADA");
        System.out.println("\n\n" + response + "\n\n");
    }
}
