package com.api.tca.domain.meeting.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.FailureResult;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.domain.meeting.dto.request.MinimalRegisterMeetingDto;
import com.api.tca.domain.meeting.dto.request.RegisterMeetingDto;
import com.api.tca.domain.meeting.dto.request.UpdateMeetingDto;
import com.api.tca.domain.meeting.dto.response.MeetingBasicDataDto;
import com.api.tca.domain.meeting.dto.response.MeetingDetailedDto;
import com.api.tca.domain.meeting.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;


    // Rota exclusiva para Gerente e Diretores
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MeetingBasicDataDto>>> getAllMeetings(Pageable pageable) {
        var meetings = meetingService.getAllMeetings(pageable);
        return ResponseEntity.ok(new SuccessResult<>( meetings.stream().count() + " reuniões encontradas", meetings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> getDetailedMeeting(@PathVariable UUID id) {
        var data = meetingService.getMeetingDetailedDtoById(id);
        return ResponseEntity.ok(new SuccessResult<>("Reunião encontrada", data));
    }

    @PostMapping
    /**
     * Cria uma nova futura reunião com data de agendamento especificada
     * */
    public ResponseEntity<ApiResponse<MeetingBasicDataDto>> addRawMeeting(@RequestBody @Valid RegisterMeetingDto request, UriComponentsBuilder uriBuilder) {
        var data = meetingService.createMeeting(request);
        URI meetingUri = uriBuilder.path("/meeting/{id}").buildAndExpand(data.id()).toUri();
        return ResponseEntity.created(meetingUri).body(new SuccessResult<>(HttpStatus.CREATED, "Reunião criada", data));
    }

    @PostMapping("/new-analyse")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> addMeetingTranscript(
            @RequestParam("transcript") MultipartFile transcript,
            @RequestBody @Valid MinimalRegisterMeetingDto request,
            UriComponentsBuilder uriBuilder)
    {
        var data = meetingService.createMeeting(request);
        URI meetingUri = uriBuilder.path("/meeting/{id}").buildAndExpand(data.id()).toUri();
        return ResponseEntity.created(meetingUri).body(new SuccessResult<>(HttpStatus.CREATED, "Reunião com transcrição criada", data));
    }

    @PutMapping("/{id}/analyse")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> analyseTranscritMeetingById(@PathVariable UUID id) {
        var data = meetingService.analyseMeetingById(id);
        return ResponseEntity.ok(new SuccessResult<>("Reunião analisada com sucesso", data));
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

    @GetMapping("/user/{id}/last")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> getLastUserMeeting(@PathVariable UUID id) {
        var data = meetingService.getUserMeetingByKey(id, false);
        return ResponseEntity.ok(new SuccessResult<>("Reunião encontrada", data));
    }

    @GetMapping("/user/{id}/next")
    public ResponseEntity<ApiResponse<MeetingDetailedDto>> getNextUserMeeting(@PathVariable UUID id) {
        var data = meetingService.getUserMeetingByKey(id, true);
        return ResponseEntity.ok(new SuccessResult<>("Reunião encontrada", data));
    }

    @PostMapping("/user")
    public ResponseEntity addEmployeeToTheMeeting() {
        throw new RuntimeException("Not Implemented");
    }

    @DeleteMapping("/user")
    public ResponseEntity deleteEmployeeFromTheMeeting() {
        throw new RuntimeException("Not Implemented");
    }
}
