package com.api.tca.domain.client.service;

import com.api.tca.common.ai.dto.request.ClientEmbeddingRequest;
import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.address.service.AddressService;
import com.api.tca.domain.client.dto.analyse.ClientAnalyseDto;
import com.api.tca.domain.client.dto.analyse.ClientCompleteDto;
import com.api.tca.domain.client.dto.client.ClientDetailedDto;
import com.api.tca.domain.client.dto.client.ClientSimplerDto;
import com.api.tca.domain.client.dto.client.RegisterClientRequestDto;
import com.api.tca.domain.client.dto.client.UpdateClientDto;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.client.exception.ClientNotFoundException;
import com.api.tca.domain.client.exception.InvalidClientStatusException;
import com.api.tca.domain.client.exception.NullableClientContactException;
import com.api.tca.domain.client.mapper.ClientMapper;
import com.api.tca.domain.client.repository.ClientRepository;
import com.api.tca.domain.squad.entity.SquadEntity;
import com.api.tca.domain.squad.service.SquadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientAnalyseService clientAnalyseService;

    @Autowired
    private SquadService squadService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ClientMapper mapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public ClientEntity getClientById(UUID id) {
        return clientRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
    }

    public ClientEntity getClientByEmail(String email) {
        return clientRepository.findClientByEmailAndIsDeletedFalse(email).orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
    }

    public ClientEntity getClientByName(String name) {
        return clientRepository.findClientByNameAndIsDeletedFalse(name).orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
    }

    public ClientCompleteDto getCompleteClientById(UUID id) {
        ClientDetailedDto client = new ClientDetailedDto(getClientById(id));
        var analyseEntity = clientAnalyseService.getAnalyseByClientId(id);
        if (analyseEntity == null) return new ClientCompleteDto(client, null);

        ClientAnalyseDto analyse = new ClientAnalyseDto(analyseEntity);
        return new ClientCompleteDto(client, analyse);
    }

    public Page<ClientSimplerDto> getClients(Pageable pageable) {
        return clientRepository.findAllClientsByIsDeletedFalse(pageable).map(ClientSimplerDto::new);
    }

    @Transactional
    public ClientDetailedDto registerClient(RegisterClientRequestDto request) {
        if (request.email() == null && request.phone() == null) {
            throw new NullableClientContactException("Cliente deve ter email ou telefone como contato");
        }

        AddressEntity clientAddress = addressService.findOrCreateAddressByPostalCode(request.address());
        SquadEntity squadSelected = squadService.findSquadByCode(request.squad().code());

        ClientEntity clientEntity = mapper.mapRegisterClientRequestDtoToClientEntity(request);

        clientEntity.setCnpj(cleanCnpj(request.cnpj()));
        clientEntity.setAddress(clientAddress);
        clientEntity.setSquad(squadSelected);
        clientEntity.setStatus(ClientStatus.UNDEFINED);
        clientEntity.setRevenue(request.revenue() == null ? new BigDecimal("0") : request.revenue());

        var newClient = clientRepository.save(clientEntity);
        eventPublisher.publishEvent(new ClientEmbeddingRequest(newClient.getId()));
        return new ClientDetailedDto(clientEntity);
    }

    @Transactional
    public ClientDetailedDto updateClient(UUID id, UpdateClientDto request) {
        ClientEntity oldClient = getClientById(id);
        ClientEntity updatedClient = mapper.mapUpdateClientDtoToClientEntity(request, oldClient);

        if (request.address() != null)
            updatedClient.setAddress(addressService.findOrCreateAddressByPostalCode(request.address()));
        if (request.squad() != null && !Objects.equals(request.squad().code(), updatedClient.getSquad().getCode()))
            updatedClient.setSquad(squadService.findSquadByCode(request.squad().code()));
        if (request.status() == ClientStatus.UNDEFINED || request.status() == ClientStatus.DELETED) {
            throw new InvalidClientStatusException("Status de cliente inválido para definição");
        }

        eventPublisher.publishEvent(new ClientEmbeddingRequest(updatedClient.getId()));
        return new ClientDetailedDto(updatedClient);
    }

    @Transactional
    public void deleteClient(UUID id) {
        ClientEntity clientEntity = getClientById(id);
        clientEntity.setStatus(ClientStatus.DELETED);
        clientEntity.setDeleted(true);
    }

    private static String cleanCnpj(String cnpj) {
        return cnpj.replace(".", "").replace("-", "").replace("/", "").strip();
    }
}
