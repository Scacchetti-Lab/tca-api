package com.api.tca.domain.meeting.controller;

import com.api.tca.common.helpers.ProfileTypeByLabel;
import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.common.model.dto.PredictRequestDto;
import com.api.tca.domain.meeting.dto.request.*;
import com.api.tca.domain.meeting.dto.response.*;
import com.api.tca.domain.meeting.dto.response.predict.MeetingPredictResponseDto;
import com.api.tca.domain.meeting.enums.SearchReference;
import com.api.tca.domain.meeting.service.MeetingService;
import com.api.tca.domain.transcript.dto.TranscriptFormDataDto;
import com.api.tca.domain.user.enums.ProfileTypes;
import com.api.tca.domain.user.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
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

@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private TokenService tokenService;

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

    @PostMapping("/new-analyse")
    public ResponseEntity<ApiResponse<MeetingTranscriptBasicDto>> addMeetingTranscript(
            HttpServletRequest httpRequest,
            @RequestBody @Valid MinimalRegisterMeetingDto request,
            UriComponentsBuilder uriBuilder)
    {
        var tokenData = tokenService.getAuthenticatedUser(httpRequest);
        var data = meetingService.createAndAnalyseMeetingByRequest(request, ProfileTypeByLabel.get(tokenData.profile()), tokenData.email());
        URI meetingUri = uriBuilder.path("/meeting/{id}").buildAndExpand(data.meeting().id()).toUri();
        return ResponseEntity.created(meetingUri).body(new SuccessResult<>(HttpStatus.CREATED, "Reunião criada e transcrição em processamento", data));
    }

    @PutMapping("/{id}/analyse")
    public ResponseEntity<ApiResponse<MeetingTranscriptBasicDto>> analyseTranscriptMeetingById(
            HttpServletRequest httpRequest,
            @PathVariable UUID id,
            @RequestBody @Valid TranscriptFormDataDto request) {

        var tokenData = tokenService.getAuthenticatedUser(httpRequest);
        var data = meetingService.findAndAnalyseMeetingById(id, request, ProfileTypeByLabel.get(tokenData.profile()), tokenData.email());
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
        var entities = meetingService.getAllMeetingByUserId(id, pageable);
        var data = entities.map(MeetingBasicDataDto::new);
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

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<MinimalMeetingDto>> completeMeeting(@PathVariable UUID meetingId) {
        var updatedMeeting = meetingService.completeMeeting(meetingId);
        return ResponseEntity.ok(new SuccessResult<>("Reunião Concluída", updatedMeeting));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<MinimalMeetingDto>> cancelMeeting(@PathVariable UUID meetingId) {
        var updatedMeeting = meetingService.cancelMeeting(meetingId);
        return ResponseEntity.ok(new SuccessResult<>("Reunião Cancelada", updatedMeeting));
    }

    @PostMapping("/predict")
    public ResponseEntity<ApiResponse<MeetingPredictResponseDto>> predictNextClientMeetingBaseOnHistory(@RequestBody @Valid PredictRequestDto request) {
        var predict = meetingService.predictFutureMeeting(request.entityId(), request.reprocess());
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult<>(HttpStatus.CREATED, "Previsão criada", predict));
    }

    @PostMapping("/{id}/score")
    public ResponseEntity<ApiResponse<String>> setScorePoints(@PathVariable UUID id) {
        meetingService.updateMeetingScores(id);
        // TODO: DTO para retorno de scores
        return ResponseEntity.ok(new SuccessResult<>(null, "Score recalculado"));
    }
}
