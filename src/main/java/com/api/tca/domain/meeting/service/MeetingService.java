package com.api.tca.domain.meeting.service;

import com.api.tca.common.ai.dto.request.ClientContextRequestDto;
import com.api.tca.common.ai.dto.request.MeetingTranscriptProcessDto;
import com.api.tca.common.ai.dto.request.TranscriptProcessRequestDto;
import com.api.tca.common.ai.dto.response.transcript.TranscriptAnalyseDto;
import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.client.dto.client.UpdateClientDto;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.service.ClientService;
import com.api.tca.domain.meeting.dto.request.*;
import com.api.tca.domain.meeting.dto.response.*;
import com.api.tca.domain.meeting.dto.response.predict.MeetingAiCustomDto;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.entity.MeetingStakeholdersEntity;
import com.api.tca.domain.meeting.enums.MeetingPriority;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import com.api.tca.domain.meeting.enums.MeetingUserSource;
import com.api.tca.domain.meeting.exception.rules.*;
import com.api.tca.domain.meeting.mapper.MeetingMapper;
import com.api.tca.domain.meeting.repository.MeetingRepository;
import com.api.tca.domain.meeting.repository.MeetingStakeHolderRepository;
import com.api.tca.domain.meeting.validations.meetings.MeetingValidate;
import com.api.tca.domain.meeting.validations.stakeholder.StakeholderValidate;
import com.api.tca.domain.transcript.dto.TranscriptBasicDto;
import com.api.tca.domain.transcript.dto.TranscriptFormDataDto;
import com.api.tca.domain.transcript.enums.TranscriptStatus;
import com.api.tca.domain.transcript.exceptions.TranscriptProcessErrorException;
import com.api.tca.domain.transcript.service.TranscriptService;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.enums.ProfileTypes;
import com.api.tca.domain.user.exception.UserNotFound;
import com.api.tca.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingStakeHolderRepository stakeholderRepository;

    @Autowired
    private MeetingPerformanceService performanceService;

    @Autowired
    private MeetingStrategicService strategicService;

    @Autowired
    private MeetingMapper mapper;

    @Autowired
    private TranscriptService transcriptService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private List<StakeholderValidate> shValidator;

    @Autowired
    private List<MeetingValidate> mtValidator;

    public Page<MeetingBasicDataDto> getAllMeetings(Pageable pageable) {
        var data = meetingRepository.findAllByIsDeletedFalse(pageable).map(MeetingBasicDataDto::new);
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public Page<MeetingBasicDataDto> getAllMeetingByUserId(UUID id, Pageable pageable) {
        var data = meetingRepository.findAllByUserIdAndIsDeletedFalse(pageable, id).map(MeetingBasicDataDto::new);
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public Page<MeetingBasicDataDto> getAllMeetingByClientId(UUID id, Pageable pageable) {
        var data = meetingRepository.findAllByClientIdAndIsDeletedFalse(pageable, id).map(MeetingBasicDataDto::new);
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public Set<MeetingBasicDataDto> getAllMeetingByClientId(UUID id) {
        var data = meetingRepository.findAllByClientIdAndIsDeletedFalse(id).stream().map(MeetingBasicDataDto::new).collect(Collectors.toSet());
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public Page<MinimalMeetingDto> getAllMinimalMeetings(Pageable pageable) {
        var data = meetingRepository.findAllByIsDeletedFalse(pageable).map(MinimalMeetingDto::new);
        if (data.isEmpty()) throw new MeetingNotFoundException("Nenhuma reunião encontrada");
        return data;
    }

    public MeetingEntity getMeetingEntityById(UUID id) {
        return meetingRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new MeetingNotFoundException("Reunião não encontrada"));
    }

    public MeetingDetailedDto getMeetingDetailedDtoById(UUID id) {
        var entity = getMeetingEntityById(id);
        var stakeHolders = findStakeholdersByMeetingId(entity.getId());

        return new MeetingDetailedDto(entity, stakeHolders);
    }

    public MeetingDetailedDto getUserMeetingByKey(UUID id, Boolean isNext) {
        Pageable limitOne = PageRequest.of(0, 1);
        var results = isNext
                ? meetingRepository.findFirstNextByUserId(id, limitOne)
                : meetingRepository.findFirstLastByUserId(id, limitOne);

        var meetingEntity = results.stream().findFirst()
                .orElseThrow(() -> new MeetingNotFoundException(
                        "Usuário não possuí uma " + (isNext ? "próxima" : "última") + " reunião"));
        var stakeHolders = findStakeholdersByMeetingId(meetingEntity.getId());
        return new MeetingDetailedDto(meetingEntity, stakeHolders);
    }

    public MeetingDetailedDto getClientMeetingByDirection(UUID id, Boolean isNext) {
        Pageable limitOne = PageRequest.of(0, 1);
        var results = isNext
                ? meetingRepository.findNextClientMeeting(id, limitOne)
                : meetingRepository.findLastClientMeeting(id, limitOne);

        var searchedMeeting = results.stream().findFirst()
                .orElseThrow(() -> new MeetingNotFoundException(
                        "Cliente não possuí uma " + (isNext ? "próxima" : "última") + " reunião"));

        var stakeHolders = findStakeholdersByMeetingId(searchedMeeting.getId());
        return new MeetingDetailedDto(searchedMeeting, stakeHolders);
    }

    public MeetingAiCustomDto predictFutureMeeting(UUID id) {
        MeetingEntity meetingEntity = getMeetingEntityById(id);
        // TODO: Primeiro busca pela tabela meeting_predicts para checar se já não existe (retorna caso já tenha)
        // TODO: Novas previsões passam pelo Gemini (provider) e evento assíncrono de processo
        return null;
    }

    @Transactional
    public void deleteMeeting(UUID id) {
        var meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Nenhuma reunião encontrada"));
        if (meeting.getStatus() == MeetingStatus.IN_PROGRESS)
            throw new MeetingValidateException("Reuniões em andamento não podem ser excluídas.");
        meeting.setIsDeleted(true);
        if (meeting.getStatus() == MeetingStatus.SCHEDULED)
            meeting.setStatus(MeetingStatus.CANCELLED);
    }

    @Transactional
    public MeetingBasicDataDto createMeeting(RegisterMeetingDto request) {
        if (meetingRepository.existsMeetingsByTotvsIdAndIsDeletedFalse(request.totvsId()))
            throw new MeetingAlreadyExistsException("Reunião de TotvsId " + request.totvsId()  + " já cadastrada no sistema");

        var entity = new MeetingEntity(request);
        entity.setClient(findMeetingClientByEmailOrName(request));
        mtValidator.forEach(v -> v.validate(entity));

        var result = meetingRepository.save(entity);
        eventPublisher.publishEvent(new MeetingTranscriptProcessDto(entity.getId(), null));
        return new MeetingBasicDataDto(result);
    }

    @Transactional
    public MeetingTranscriptBasicDto createAndAnalyseMeetingByRequest(MinimalRegisterMeetingDto request, ProfileTypes userProfile) {
        if (meetingRepository.existsMeetingsByTotvsIdAndIsDeletedFalse(request.totvsId()))
            throw new MeetingAlreadyExistsException("Reunião de TotvsId " + request.totvsId()  + " já cadastrada no sistema");

        var entity = new MeetingEntity(request);
        entity.setClient(findMeetingClientByEmailOrName(request));
        if (entity.getScheduled().isAfter(BrazilRealTime.now()))
            throw new MeetingValidateException("Reuniões com transcrição não podem terem sido agendadas para o futuro");

        var savedEntity = meetingRepository.save(entity);
        if (!request.employees().isEmpty())
            addStakeholdersToTheMeeting(savedEntity.getId(), request.employees(), false);

        return processTranscript(savedEntity, request.transcriptData(), userProfile);
    }

    @Transactional
    public MeetingTranscriptBasicDto findAndAnalyseMeetingById(UUID id, TranscriptFormDataDto request, ProfileTypes userProfile) {
        MeetingEntity meeting = getMeetingEntityById(id);
        return processTranscript(meeting, request, userProfile);
    }

    @Transactional
    public MeetingBasicDataDto updateMeeting(UUID id, UpdateMeetingDto request) {
        var entity = meetingRepository.findById(id).orElseThrow(() -> new MeetingNotFoundException("Reunião não encontrada"));
        var updatedEntity = mapper.mapUpdateMeetingDtoToMeetingEntity(request, entity);

        eventPublisher.publishEvent(entity.getId());
        return new MeetingBasicDataDto(updatedEntity);
    }


    @Transactional
    public Set<StakeholderResponseDto> addStakeholdersToTheMeeting(UUID id, Set<StakeholderRegisterDto> request, boolean isTranscriptProcess) {
        MeetingEntity meeting = getMeetingEntityById(id);
        var systemFilteredStakeholders = request.stream()
                .filter(s -> s.source() == MeetingUserSource.SYSTEM).toList();
        var guestFilteredStakeholders = request.stream()
                .filter(s -> s.source() == MeetingUserSource.GUEST).toList();

        if (!guestFilteredStakeholders.isEmpty()) {
            if (!isTranscriptProcess) {
                request.forEach(r -> {
                    shValidator.forEach(v -> v.validate(new MeetingStakeholdersEntity(meeting, r)));
                });
            }
            var guests = guestFilteredStakeholders.stream().map(s -> new MeetingStakeholdersEntity(meeting, s)).toList();
            stakeholderRepository.saveAll(guests);
        }

        if (!systemFilteredStakeholders.isEmpty()) {
            Set<UserEntity> users = new java.util.HashSet<>(Set.of());
            for (StakeholderRegisterDto stakeholder : systemFilteredStakeholders) {
                var user = userRepository.findByIsDeletedFalseAndUsernameOrIsDeletedFalseAndEmail(stakeholder.userName(), stakeholder.email())
                        .orElse(null);
                if (user == null || meetingRepository.isUserInMeeting(user.getId(), id)) continue;
                users.add(user);
            }
            if (users.isEmpty()) throw new StakeholderListAlreadyAddedException("Stakeholders já adicionados a essa reunião");
            meeting.setUsers(users);
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

    @Transactional
    public void saveAnalyses(UUID meetingId, TranscriptAnalyseDto process) {
        if (process.status() != TranscriptStatus.DONE) throw new TranscriptProcessErrorException("Falha ao processar a transcrição");
        MeetingEntity meeting = getMeetingEntityById(meetingId);
        ClientEntity client = meeting.getClient();
        var description = process.basicDescription();
        var strategic = process.analytic().strategic();
        var performance = process.analytic().performance();

        if (meeting.getClientRepresent() == null || meeting.getClientRepresent().isEmpty())
            meeting.setClientRepresent(description.companyRepresentor());
        if (client.getSegment() == null || client.getSegment().isEmpty()) {
            client.setSegment(description.segment());
            clientService.updateClient(client.getId(), new UpdateClientDto(client.getSegment()));
        }

        meeting.setSummary(description.meetingSummary());
        Set<StakeholderRegisterDto> stakeholdersList = description.stakeholders()
                .stream().map(StakeholderRegisterDto::new).collect(Collectors.toSet());

        addStakeholdersToTheMeeting(meeting.getId(), stakeholdersList, true);

        meeting.setPriority(setMeetingPriority(
                performance.insights().priority(),
                strategic.insights().priority())
        );

        performanceService.addMeetingPerformanceAnalysed(meeting, performance);
        strategicService.addMeetingStrategicAnalysed(meeting, strategic);
        meetingRepository.save(meeting);
    }

    private MeetingPriority setMeetingPriority(MeetingPriority perfPriority, MeetingPriority stratPriority) {
        if (stratPriority == perfPriority) return perfPriority;
        if (stratPriority == MeetingPriority.HIGH) return stratPriority;
        if (perfPriority == MeetingPriority.HIGH) return perfPriority;
        if (stratPriority == MeetingPriority.MEDIUM && perfPriority == MeetingPriority.LOW) return stratPriority;

        return perfPriority;
    }

    public MeetingTranscriptBasicDto processTranscript(MeetingEntity meetingEntity, TranscriptFormDataDto request, ProfileTypes loggedIn) {
        var addedTranscript = transcriptService.addEmptyTranscript(request);
        var transcriptDto = new TranscriptProcessRequestDto(
                addedTranscript.getId(),
                loggedIn,
                new ClientContextRequestDto(
                        meetingEntity.getClient().getName(),
                        meetingEntity.getClient().getStatus(),
                        lastMeetingsByClient(meetingEntity.getClient()))
        );
        meetingEntity.setTranscript(addedTranscript);
        meetingRepository.save(meetingEntity);

        eventPublisher.publishEvent(new MeetingTranscriptProcessDto(meetingEntity.getId(), transcriptDto));
        return new MeetingTranscriptBasicDto(
            new MeetingBasicDataDto(meetingEntity),
            new TranscriptBasicDto(addedTranscript)
        );
    }

    private Set<String> lastMeetingsByClient(ClientEntity client) {
        try {
            var allMeetings = getAllMeetingByClientId(client.getId());
            return allMeetings.stream().map(MeetingBasicDataDto::summary).collect(Collectors.toSet());
        }
        catch (MeetingNotFoundException e) {
            log.error(e.getMessage(), e);
            return Set.of();
        }
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
