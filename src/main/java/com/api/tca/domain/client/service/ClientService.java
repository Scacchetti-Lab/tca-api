package com.api.tca.domain.client.service;

import com.api.tca.common.ai.dto.request.ClientEmbeddingDto;
import com.api.tca.common.ai.dto.request.predict.PredictEventRequestDto;
import com.api.tca.common.exception.custom.FailOnPredictException;
import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.address.service.AddressService;
import com.api.tca.domain.client.dto.analyse.ClientDetailedDto;
import com.api.tca.domain.client.dto.client.ClientDescriptionDto;
import com.api.tca.domain.client.dto.client.ClientSimplerDto;
import com.api.tca.domain.client.dto.client.RegisterClientRequestDto;
import com.api.tca.domain.client.dto.client.UpdateClientDto;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.entity.ClientPredictEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.client.exception.ClientNotFoundException;
import com.api.tca.domain.client.exception.InvalidClientStatusException;
import com.api.tca.domain.client.exception.NullableClientContactException;
import com.api.tca.domain.client.mapper.ClientMapper;
import com.api.tca.domain.client.repository.ClientPredictRepository;
import com.api.tca.domain.client.repository.ClientRepository;
import com.api.tca.domain.client.validation.ClientPredictValidate;
import com.api.tca.domain.client.dto.predict.ClientPredictResponseDto;
import com.api.tca.domain.meeting.entity.MeetingPredictEntity;
import com.api.tca.domain.meeting.enums.PredictProcessStatus;
import com.api.tca.domain.meeting.exception.rules.MeetingNotFoundException;
import com.api.tca.domain.squad.entity.SquadEntity;
import com.api.tca.domain.squad.service.SquadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SquadService squadService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ClientMapper mapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private List<ClientPredictValidate> clValidate;

    @Autowired
    private ClientPredictRepository predictRepository;

    public ClientEntity getClientById(UUID id) {
        return clientRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
    }

    public ClientEntity getClientByEmail(String email) {
        return clientRepository.findClientByEmailAndIsDeletedFalse(email).orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
    }

    public ClientEntity getClientByName(String name) {
        return clientRepository.findClientByNameAndIsDeletedFalse(name).orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
    }

    public ClientDetailedDto getDetailedClientById(UUID id) {
       return clientRepository.findClientWithAnalysis(id)
               .orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
    }

    public Page<ClientSimplerDto> getClients(Pageable pageable) {
        return clientRepository.findAllClientsByIsDeletedFalse(pageable).map(ClientSimplerDto::new);
    }

    public ClientPredictEntity getPredictByClientId(UUID id) {
        return predictRepository.findByClientId(id).orElseThrow(() -> new ClientNotFoundException("Previsão não encontrada"));
    }

    @Transactional
    public ClientDescriptionDto registerClient(RegisterClientRequestDto request) {
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
        eventPublisher.publishEvent(new ClientEmbeddingDto(newClient.getId()));
        return new ClientDescriptionDto(clientEntity);
    }

    @Transactional
    public ClientDescriptionDto updateClient(UUID id, UpdateClientDto request) {
        ClientEntity oldClient = getClientById(id);
        ClientEntity updatedClient = mapper.mapUpdateClientDtoToClientEntity(request, oldClient);

        if (request.address() != null)
            updatedClient.setAddress(addressService.findOrCreateAddressByPostalCode(request.address()));
        if (request.squad() != null && !Objects.equals(request.squad().code(), updatedClient.getSquad().getCode()))
            updatedClient.setSquad(squadService.findSquadByCode(request.squad().code()));
        if (request.status() == ClientStatus.UNDEFINED || request.status() == ClientStatus.DELETED) {
            throw new InvalidClientStatusException("Status de cliente inválido para definição");
        }

        eventPublisher.publishEvent(new ClientEmbeddingDto(updatedClient.getId()));
        return new ClientDescriptionDto(updatedClient);
    }

    @Transactional
    public void deleteClient(UUID id) {
        ClientEntity clientEntity = getClientById(id);
        clientEntity.setStatus(ClientStatus.DELETED);
        clientEntity.setDeleted(true);
    }

    @Transactional
    public ClientPredictResponseDto predictClientRelationship(UUID id, Boolean reprocess) {
        ClientEntity clientEntity = getClientById(id);
        clValidate.forEach(v -> v.validate(clientEntity));
        var predictEntity = predictRepository.findByClientId(clientEntity.getId())
                .orElse(null);

        if (predictEntity == null) {
            var newPredict = predictRepository.save(new ClientPredictEntity(clientEntity));
            eventPublisher.publishEvent(new PredictEventRequestDto(clientEntity.getId(), false));
            return new ClientPredictResponseDto(newPredict);
        }

        if (predictEntity.getStatus().equals(PredictProcessStatus.CANCELLED)) {
            predictEntity.setStatus(PredictProcessStatus.CREATED);
            predictRepository.save(predictEntity);
            eventPublisher.publishEvent(new PredictEventRequestDto(clientEntity.getId(), true));
            return new ClientPredictResponseDto(predictEntity);
        }

        if (!reprocess) return new ClientPredictResponseDto(predictEntity);
        if (predictEntity.getReprocess()) {
            throw new FailOnPredictException(
                    "Esta previsão já foi reprocessada uma vez e não pode ser gerada novamente.");
        }

        eventPublisher.publishEvent(new PredictEventRequestDto(clientEntity.getId(), true));
        return new ClientPredictResponseDto(predictEntity);
    }

    @Transactional
    public void addPredictData(ClientPredictEntity predictEntity, String predict, boolean reprocess) {
        predictEntity.setStatus(PredictProcessStatus.COMPLETED);
        predictEntity.setReprocess(reprocess);
        predictEntity.setPredict(predict);
        predictRepository.save(predictEntity);
    }

    @Transactional
    public void updatePredictStatus(UUID id, PredictProcessStatus status) {
        var predict = getPredictByClientId(id);
        predict.setStatus(status);
        predictRepository.save(predict);
    }

    private static String cleanCnpj(String cnpj) {
        return cnpj.replace(".", "").replace("-", "").replace("/", "").strip();
    }
}
