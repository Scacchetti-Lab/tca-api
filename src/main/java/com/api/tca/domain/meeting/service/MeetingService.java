package com.api.tca.domain.meeting.service;

import com.api.tca.common.ai.dto.request.MeetingEmbeddingRequest;
import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.service.ClientService;
import com.api.tca.domain.meeting.dto.request.MinimalRegisterMeetingDto;
import com.api.tca.domain.meeting.dto.request.RegisterMeetingDto;
import com.api.tca.domain.meeting.dto.request.StakeholderRegisterDto;
import com.api.tca.domain.meeting.dto.request.UpdateMeetingDto;
import com.api.tca.domain.meeting.dto.response.MeetingBasicDataDto;
import com.api.tca.domain.meeting.dto.response.MeetingDetailedDto;
import com.api.tca.domain.meeting.dto.response.MeetingStakeHolder;
import com.api.tca.domain.meeting.dto.response.StakeholderResponseDto;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.entity.MeetingStakeholdersEntity;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.api.tca.domain.meeting.enums.MeetingUserSource;
import com.api.tca.domain.meeting.exception.*;
import com.api.tca.domain.meeting.mapper.MeetingMapper;
import com.api.tca.domain.meeting.repository.MeetingRepository;
import com.api.tca.domain.meeting.repository.MeetingStakeHolderRepository;
import com.api.tca.domain.meeting.validations.meetings.MeetingValidate;
import com.api.tca.domain.meeting.validations.stakeholder.StakeholderValidate;
import com.api.tca.domain.transcript.service.TranscriptService;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.exception.UserNotFound;
import com.api.tca.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingStakeHolderRepository stakeholderRepository;

    @Autowired
    private MeetingMapper mapper;

    @Autowired
    private TranscriptService transcriptService;

    @Autowired
    private MeetingListenerService listenerService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private List<StakeholderValidate> shValidator;

    @Autowired
    private List<MeetingValidate> mtValidator;

    public Page<MeetingBasicDataDto> getAllMeetings(Pageable pageable) {
        var data = meetingRepository.findAll(pageable).map(MeetingBasicDataDto::new);
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public Page<MeetingBasicDataDto> getAllMeetingByUserId(UUID id, Pageable pageable) {
        var data = meetingRepository.findAllByUserId(pageable, id).map(MeetingBasicDataDto::new);
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public Page<MeetingBasicDataDto> getAllMeetingByClientId(UUID id, Pageable pageable) {
        var data = meetingRepository.findAllByClientId(pageable, id).map(MeetingBasicDataDto::new);
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public MeetingEntity getMeetingEntityById(UUID id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Reunião não encontrada"));
    }

    public MeetingDetailedDto getMeetingDetailedDtoById(UUID id) {
        var entity = getMeetingEntityById(id);
        var stakeHolders = findStakeholdersByMeetingId(entity.getId());

        return new MeetingDetailedDto(entity, stakeHolders);
    }

    public MeetingDetailedDto getUserMeetingByKey(UUID id, Boolean isNext) {
        var optionalMeetingEntity = isNext
                ? meetingRepository.findFirstNextByUserId(id)
                : meetingRepository.findFirstLastByUserId(id);

        if (optionalMeetingEntity.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        var meetingEntity = optionalMeetingEntity.get();
        var stakeHolders = findStakeholdersByMeetingId(meetingEntity.getId());

        return new MeetingDetailedDto(meetingEntity, stakeHolders);
    }

    @Transactional
    public void deleteMeeting(UUID id) {
        var meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Nenhuma reunião encontrada"));
        meeting.setIsDeleted(true);
        meeting.setStatus(MeetingStatus.CANCELLED);
    }

    @Transactional
    public MeetingBasicDataDto createMeeting(RegisterMeetingDto request) {
        if (meetingRepository.existsMeetingsByTotvsId(request.totvsId()))
            throw new MeetingAlreadyExistsException("Reunião de TotvsId " + request.totvsId()  + " já cadastrada no sistema");

        var entity = new MeetingEntity(request);
        entity.setClient(findMeetingClientByEmailOrName(request));
        mtValidator.forEach(v -> v.validate(entity));

        var result = meetingRepository.save(entity);
        listenerService.onMeetingCreated(new MeetingEmbeddingRequest(entity.getId()));
        return new MeetingBasicDataDto(result);
    }

    // TODO - Integrar Leitura de arquivos de transcrição
    // TODO - Analise de Transcrição de forma assíncrona
    // TODO - Criar Chunks e Embeds
    @Transactional
    public MeetingDetailedDto createMeeting(MinimalRegisterMeetingDto request) {
        var entity = new MeetingEntity(request);
        entity.setClient(findMeetingClientByEmailOrName(request));
        if (entity.getScheduled().isAfter(BrazilRealTime.now()))
            throw new MeetingValidateException("Reuniões com transcrição não podem terem sido agendadas para o futuro");

        var result = meetingRepository.save(entity);
        var stakeHolders = findStakeholdersByMeetingId(result.getId());
        listenerService.onMeetingCreated(new MeetingEmbeddingRequest(entity.getId()));

        // chamada de transcricao
        return new MeetingDetailedDto(result, stakeHolders);
    }

    @Transactional
    public MeetingBasicDataDto updateMeeting(UUID id, UpdateMeetingDto request) {
        var entity = meetingRepository.findById(id).orElseThrow(() -> new MeetingNotFoundException("Reunião não encontrada"));
        var updatedEntity = mapper.mapUpdateMeetingDtoToMeetingEntity(request, entity);

        listenerService.onMeetingCreated(new MeetingEmbeddingRequest(entity.getId()));
        return new MeetingBasicDataDto(updatedEntity);
    }

    // TODO - Analise de Transcrição de forma assíncrona
    // TODO - Criar Chunks e Embeds
    @Transactional
    public MeetingDetailedDto analyseMeetingById(UUID id) {
        throw new RuntimeException("Not implemented yet");
    }

    @Transactional
    public Set<StakeholderResponseDto> addStakeholdersToTheMeeting(UUID id, Set<StakeholderRegisterDto> request) {
        MeetingEntity meeting = getMeetingEntityById(id);
        var systemFilteredStakeholders = request.stream()
                .filter(s -> s.source() == MeetingUserSource.SYSTEM).toList();
        var guestFilteredStakeholders = request.stream()
                .filter(s -> s.source() == MeetingUserSource.GUEST).toList();

        if (!guestFilteredStakeholders.isEmpty()) {
            request.forEach(r -> {
                shValidator.forEach(v -> v.validate(new MeetingStakeholdersEntity(meeting, r)));
            });
            var guests = guestFilteredStakeholders.stream().map(s -> new MeetingStakeholdersEntity(meeting, s)).toList();
            stakeholderRepository.saveAll(guests);
        }

        if (!systemFilteredStakeholders.isEmpty()) {
            Set<UserEntity> users = new java.util.HashSet<>(Set.of());
            for (StakeholderRegisterDto stakeholder : systemFilteredStakeholders) {
                var user = userRepository.findByIsDeletedFalseAndUsernameOrIsDeletedFalseAndEmail(stakeholder.userName(), stakeholder.email())
                        .orElse(null);
                if (user == null || meetingRepository.existsStakeholderByUserId(user.getId())) continue;
                users.add(user);
            }
            if (users.isEmpty()) throw new StakeholderListAlreadyAddedException("Stakeholders já adicionados a essa reunião");
            meeting.addMultiplesEmployee(users);
        }

        return request.stream().map(StakeholderResponseDto::new).collect(Collectors.toSet());
    }

    @Transactional
    public StakeholderResponseDto deleteStakeholderFromTheMeeting(UUID id, StakeholderRegisterDto request) {
        MeetingEntity meeting = getMeetingEntityById(id);
        if (meeting.getStatus() != MeetingStatus.SCHEDULED) {
            throw new MeetingValidateException("Somente em reuniões agendadas é possível remover ou adicionar stakeholders");
        }

        if (request.source() == MeetingUserSource.SYSTEM) {
            String login = request.email() != null ? request.email() : request.userName();
            UserEntity user = userRepository.findByLogin(login).orElseThrow(
                    () -> new UserNotFound("Usuário com login " + login + " não foi encontrado."));

            meeting.removeEmployee(user);
            return new StakeholderResponseDto(user.getFullName(), request.source());
        }
        var stakeHolderDb = stakeholderRepository.findFirstByNameAndMeetingId(request.name(), meeting.getId())
                .orElseThrow(() -> new StakeholderNotFoundException("Stakeholder não foi encontrado nessa reunião."));
        stakeholderRepository.delete(stakeHolderDb);
        return new StakeholderResponseDto(request.name(), request.source());
    }

    private Set<MeetingStakeHolder> findStakeholdersByMeetingId(UUID id) {
        return stakeholderRepository.findAllByMeetingId(id)
                .stream().map(MeetingStakeHolder::new).collect(Collectors.toSet());
    }

    private ClientEntity findMeetingClientByEmailOrName(RegisterMeetingDto request) {
        if (!request.email().isEmpty())
            return clientService.getClientByEmail(request.email());
        if (!request.clientName().isEmpty())
            return clientService.getClientByName(request.clientName());
        throw new MeetingValidateException("Cliente precisa ser identificado pelo Id ou pelo nome");
    }

    private ClientEntity findMeetingClientByEmailOrName(MinimalRegisterMeetingDto request) {
        if (!request.clientName().isEmpty())
            return clientService.getClientByName(request.clientName());
        throw new MeetingValidateException("Cliente precisa ser identificado pelo Id ou pelo nome");
    }
}
