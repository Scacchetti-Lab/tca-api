package com.api.tca.domain.meeting.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.domain.meeting.dto.request.*;
import com.api.tca.domain.meeting.dto.response.*;
import com.api.tca.domain.meeting.enums.SearchReference;
import com.api.tca.domain.meeting.service.MeetingService;
import com.api.tca.domain.transcript.dto.TranscriptFormDataDto;
import com.api.tca.domain.user.enums.ProfileTypes;
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
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MeetingBasicDataDto>>> getAllMeetings(@PageableDefault(size = 5) Pageable pageable) {
        var meetings = meetingService.getAllMeetings(pageable);
        return ResponseEntity.ok(new SuccessResult<>( meetings.stream().count() + " reuniões encontradas", meetings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> getDetailedMeeting(@PathVariable UUID id) {
        var data = meetingService.getMeetingDetailedDtoById(id);
        return ResponseEntity.ok(new SuccessResult<>("Reunião encontrada", data));
    }

    @GetMapping("/minimal")
    public ResponseEntity<ApiResponse<Page<MinimalMeetingDto>>> getMinimalMeetings(Pageable pageable) {
        var meetings = meetingService.getAllMinimalMeetings(pageable);
        return ResponseEntity.ok(new SuccessResult<>( meetings.stream().count() + " reuniões encontradas", meetings));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MeetingBasicDataDto>> addRawMeeting(@RequestBody @Valid RegisterMeetingDto request, UriComponentsBuilder uriBuilder) {
        var data = meetingService.createMeeting(request);
        URI meetingUri = uriBuilder.path("/meeting/{id}").buildAndExpand(data.id()).toUri();
        return ResponseEntity.created(meetingUri).body(new SuccessResult<>(HttpStatus.CREATED, "Reunião criada", data));
    }

    @PostMapping(value = "/new-analyse")
    public ResponseEntity<ApiResponse<MeetingTranscriptBasicDto>> addMeetingTranscript(
            @RequestBody @Valid MinimalRegisterMeetingDto request,
            UriComponentsBuilder uriBuilder)
    {
        // TODO: Ao obter JWT, pegar o profiletype atual do usuário
        var data = meetingService.createAndAnalyseMeetingByRequest(request, ProfileTypes.DIRECTOR);
        URI meetingUri = uriBuilder.path("/meeting/{id}").buildAndExpand(data.meeting().id()).toUri();
        return ResponseEntity.created(meetingUri).body(new SuccessResult<>(HttpStatus.CREATED, "Reunião criada e transcrição em processamento", data));
    }

    @PutMapping("/{id}/analyse")
    public ResponseEntity<ApiResponse<MeetingTranscriptBasicDto>> analyseTranscriptMeetingById(
            @PathVariable UUID id,
            @RequestBody @Valid TranscriptFormDataDto request) {
        // TODO: Ao obter JWT, pegar o profiletype atual do usuário
        var data = meetingService.findAndAnalyseMeetingById(id, request, ProfileTypes.DIRECTOR);
        return ResponseEntity.ok(new SuccessResult<>("Transcrição em Processamento", data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingBasicDataDto>> updateMeetingDetails(@PathVariable UUID id, @RequestBody @Valid UpdateMeetingDto meetingDto) {
        var data = meetingService.updateMeeting(id, meetingDto);
        return ResponseEntity.ok(new SuccessResult<>("Reunião atualizada", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable UUID id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Page<MeetingBasicDataDto>>> getAllMeetingsByUser(@PathVariable UUID id, Pageable pageable) {
        var data = meetingService.getAllMeetingByUserId(id, pageable);
        return ResponseEntity.ok(new SuccessResult<>(data.stream().count() + " reuniões encontrada", data));
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<ApiResponse<Page<MeetingBasicDataDto>>> getAllMeetingsByClient(@PathVariable UUID id, Pageable pageable) {
        var data = meetingService.getAllMeetingByClientId(id, pageable);
        return ResponseEntity.ok(new SuccessResult<>("Reunião encontrada", data));
    }

    @GetMapping("/client/{id}/insight")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> getClientNextOrLastMeetingStatus(
            @RequestParam("reference") @Valid SearchReference reference,
            @PathVariable UUID id
    ) {
        var data = meetingService.getClientMeetingByDirection(id, reference == SearchReference.NEXT);
        return ResponseEntity.ok(new SuccessResult<>("Reunião encontrada", data));
    }

    @GetMapping("/user/{id}/insight")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> getLastUserMeeting(
            @RequestParam("reference") @Valid SearchReference reference,
            @PathVariable UUID id) {
        var data = meetingService.getUserMeetingByKey(id, reference == SearchReference.NEXT);
        return ResponseEntity.ok(new SuccessResult<>("Reunião encontrada", data));
    }

    @PostMapping("/{id}/stakeholder")
    public ResponseEntity<ApiResponse<Set<StakeholderResponseDto>>> addParticipantToTheMeeting(@PathVariable UUID id,
                                                                                                @RequestBody @Valid Set<StakeholderRegisterDto> requestParticipants) {
        var data = meetingService.addStakeholdersToTheMeeting(id, requestParticipants, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult<>(HttpStatus.CREATED, "Stakeholder adicionado", data));
    }

    @DeleteMapping("/{id}/stakeholder")
    public ResponseEntity<ApiResponse<StakeholderResponseDto>> deleteParticipantFromTheMeeting(@PathVariable UUID id,
                                                                                               @RequestBody @Valid StakeholderRegisterDto requestParticipants) {
        var removedStakeholder = meetingService.deleteStakeholderFromTheMeeting(id, requestParticipants);
        return ResponseEntity.ok(new SuccessResult<>("Participante removido", removedStakeholder));
    }
}
