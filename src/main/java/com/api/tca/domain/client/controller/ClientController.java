package com.api.tca.domain.client.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.common.model.dto.PredictRequestDto;
import com.api.tca.domain.client.dto.analyse.ClientDetailedDto;
import com.api.tca.domain.client.dto.client.ClientDescriptionDto;
import com.api.tca.domain.client.dto.client.ClientSimplerDto;
import com.api.tca.domain.client.dto.client.RegisterClientRequestDto;
import com.api.tca.domain.client.dto.client.UpdateClientDto;
import com.api.tca.domain.client.dto.predict.ClientPredictResponseDto;
import com.api.tca.domain.client.service.ClientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClientSimplerDto>>> getPaginatesClients(@PageableDefault(size = 10, sort = {"fantasyName"}) Pageable pageable) {
        Page<ClientSimplerDto> clients = clientService.getClients(pageable);
        ApiResponse<Page<ClientSimplerDto>> result = new SuccessResult<>(clients.stream().count() + " clientes encontrados!", clients);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientDescriptionDto>> registerNewClient(@RequestBody @Valid RegisterClientRequestDto request, UriComponentsBuilder uriBuilder) {
        ClientDescriptionDto newClient = clientService.registerClient(request);
        URI clientUri = uriBuilder.path("/client/{id}").buildAndExpand(newClient.id()).toUri();
        return ResponseEntity.created(clientUri).body(new SuccessResult<>(HttpStatus.CREATED, "Novo cliente registrado!", newClient));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientDescriptionDto>> updateClient(@PathVariable UUID id, @RequestBody @Valid UpdateClientDto request) {
        ClientDescriptionDto updatedClient = clientService.updateClient(id, request);
        ApiResponse<ClientDescriptionDto> result = new SuccessResult<>("Cliente atualizado", updatedClient);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientDetailedDto>> getClientDetails(@PathVariable UUID id)
    {
        ClientDetailedDto clientComplete = clientService.getDetailedClientById(id);
        ApiResponse<ClientDetailedDto> result = new SuccessResult<>("Cliente encontrado", clientComplete);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/predict")
    public ResponseEntity<ApiResponse<ClientPredictResponseDto>> predictClientBaseOnLastMeetings(@RequestBody @Valid PredictRequestDto request) {
        var predict = clientService.predictClientRelationship(request.entityId(), request.reprocess());
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult<>(HttpStatus.CREATED, "Previsão criada", predict));
    }
}
