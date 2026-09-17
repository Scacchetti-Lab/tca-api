package com.api.tca.domain.salesperson.service;

import com.api.tca.domain.salesperson.dto.UpdateSalespersonDto;
import com.api.tca.domain.salesperson.entity.SalespersonEntity;
import com.api.tca.domain.salesperson.exceptions.SalespersonNotFoundException;
import com.api.tca.domain.salesperson.repository.SalespersonRepository;
import com.api.tca.domain.squad.entity.SquadEntity;
import com.api.tca.domain.squad.repository.SquadRepository;
import com.api.tca.domain.squad.service.SquadService;
import com.api.tca.domain.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SalespersonService {

    @Autowired
    private SalespersonRepository salespersonRepository;

    @Autowired
    private SquadService squadService;

    public SalespersonEntity findById(UUID id) {
        return salespersonRepository.findById(id)
                .orElseThrow(() -> new SalespersonNotFoundException("Vendedor com esse ID não existe no sistema."));
    }

    public SalespersonEntity findByUserId(UUID id) {
        return salespersonRepository.findByUserId(id)
                .orElseThrow(() -> new SalespersonNotFoundException("Não existe vendedor com esse usuário"));
    }

    @Transactional
    public SalespersonEntity saveSalesperson(UserEntity user, SquadEntity squad) {
        SalespersonEntity salesperson = new SalespersonEntity(user, squad);
        return salespersonRepository.save(salesperson);
    }

    @Transactional
    public void updateSalesPerson(UUID id, UpdateSalespersonDto updateDto) {
        var squad = squadService.findSquadByCode(updateDto.squadCode());
        var salesperson = findById(id);

        if (updateDto.totalMeetings() != 0)
            salesperson.setTotalMeetings(updateDto.totalMeetings());
        if (!squad.getCode().equals(updateDto.squadCode()))
            salesperson.setSquad(squad);
        salesperson.setNeedTraining(updateDto.needTraining());

        salespersonRepository.save(salesperson);
    }
}
