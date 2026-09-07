package com.api.tca.domain.client.service;

import com.api.tca.domain.client.dto.analyse.ClientRecommendationDto;
import com.api.tca.domain.client.entity.ClientAnalyseEntity;
import com.api.tca.domain.client.exception.ClientNotFoundException;
import com.api.tca.domain.client.repository.ClientAnalyseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientAnalyseService {
    @Autowired
    private ClientAnalyseRepository repository;

    public ClientAnalyseEntity getAnalyseByClientId(UUID clientId) {
        return repository.findByClientId(clientId).orElse(null);
    }

    // FIRST TODO: Entidade de reuniões e analises
    public ClientRecommendationDto getRecommendationByClientId(UUID clientId) {
        throw new RuntimeException("Not Implemented");
    }
}
