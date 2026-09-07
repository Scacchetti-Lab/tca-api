package com.api.tca.domain.client.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.domain.client.dto.analyse.ClientAnalyseDto;
import com.api.tca.domain.client.dto.analyse.ClientCompleteDto;
import com.api.tca.domain.client.dto.analyse.ClientRecommendationDto;
import com.api.tca.domain.client.dto.client.ClientDetailedDto;
import com.api.tca.domain.client.dto.client.ClientSimplerDto;
import com.api.tca.domain.client.dto.client.RegisterClientRequestDto;
import com.api.tca.domain.client.dto.client.UpdateClientDto;
import com.api.tca.domain.client.service.ClientAnalyseService;
import com.api.tca.domain.client.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientAnalyseService analyseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClientSimplerDto>>> getPaginatesClients(@PageableDefault(size = 10, sort = {"fantasyName"}) Pageable pageable) {
        Page<ClientSimplerDto> clients = clientService.getClients(pageable);
        ApiResponse<Page<ClientSimplerDto>> result = new SuccessResult<>(clients.stream().count() + " clientes encontrados!", clients);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientDetailedDto>> registerNewClient(@RequestBody @Valid RegisterClientRequestDto request, UriComponentsBuilder uriBuilder) {
        ClientDetailedDto newClient = clientService.registerClient(request);
        URI clientUri = uriBuilder.path("/client/{id}").buildAndExpand(newClient.id()).toUri();
        return ResponseEntity.created(clientUri).body(new SuccessResult<>(HttpStatus.CREATED, "Novo cliente registrado!", newClient));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientDetailedDto>> updateClient(@PathVariable UUID id, @RequestBody @Valid UpdateClientDto request) {
        ClientDetailedDto updatedClient = clientService.updateClient(id, request);
        ApiResponse<ClientDetailedDto> result = new SuccessResult<>("Cliente atualizado", updatedClient);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientCompleteDto>> getClientDetails(@PathVariable UUID id,
                                                                           @RequestParam(value = "complete", defaultValue = "false") Boolean isCompleted)
    {
        ClientCompleteDto clientComplete = isCompleted
            ? clientService.getCompleteClientById(id)
            : new ClientCompleteDto(new ClientDetailedDto(clientService.getClientById(id)), null);

        ApiResponse<ClientCompleteDto> result = new SuccessResult<>("Cliente encontrado", clientComplete);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/analyse")
    public ResponseEntity<ApiResponse<ClientRecommendationDto>> analyseClientByStatus(@PathVariable UUID id) {
        var recommendation = analyseService.getRecommendationByClientId(id);

        ApiResponse<ClientRecommendationDto> result = new SuccessResult<>("Analise de cliente definida", recommendation);
        return ResponseEntity.ok(result);
    }

    // FIRST TODO: Popular e desenvolver CRUD de reuniões
    @PostMapping("/{id}/predict")
    public void predictClientBaseOnLastMeetings(@PathVariable UUID id) {
        throw new RuntimeException("Not yet implemented");
    }
}
