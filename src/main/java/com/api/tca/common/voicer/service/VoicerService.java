package com.api.tca.common.voicer.service;

import com.api.tca.common.voicer.exception.InvalidKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VoicerService {

    @Value("${tca.voicer.webhook.key}")
    private String webhookKey;

    public void isValidKeyOrThrow(String key) {
        if (key == null || !key.equals(webhookKey)) throw new InvalidKeyException("Chave de acesso para webhook é inválida.");
    }
}
