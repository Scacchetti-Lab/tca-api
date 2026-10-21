package com.api.tca.domain.squad.controller;

import com.api.tca.domain.squad.dto.SquadDto;
import com.api.tca.domain.squad.service.SquadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("squad")
public class SquadController {

    @Autowired
    private SquadService squadService;

    @GetMapping
    public ResponseEntity<Page<SquadDto>> getSquads(@PageableDefault(size = 5, sort = "code") Pageable pageable) {
        var squads = squadService.getSquads(pageable);
        return ResponseEntity.ok(squads);
    }
}
