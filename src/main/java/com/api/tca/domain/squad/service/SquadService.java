package com.api.tca.domain.squad.service;

import com.api.tca.domain.squad.dto.SquadDto;
import com.api.tca.domain.squad.entity.SquadEntity;
import com.api.tca.domain.squad.exceptions.SquadNotFoundException;
import com.api.tca.domain.squad.repository.SquadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SquadService {

    @Autowired
    private SquadRepository squadRepository;

    public Page<SquadDto> getSquads(Pageable pageable) {
        return squadRepository.findAll(pageable).map(SquadDto::new);
    }

    private String generateSquadCode() {
        String lastSquadCode = squadRepository.findLastSquadCodeAdded();
        String[] splitCode = lastSquadCode.split("-");
        int newNumber = Integer.parseInt(splitCode[2]) + 1;

        return String.format("TCA-SQD-%03d", newNumber);
    }

    public Boolean isSquadExists(String code) {
        return squadRepository.findSquadByCode(code).isPresent();
    }

    public SquadEntity findSquadByCode(String code) {
        return squadRepository.findSquadByCode(code)
                .orElseThrow(() -> new SquadNotFoundException("Squad não encontrado"));
    }
}
