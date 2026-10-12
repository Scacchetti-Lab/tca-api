package com.api.tca.domain.meeting.controller;

import com.api.tca.common.helpers.ProfileTypeByLabel;
import com.api.tca.common.voicer.service.VoicerService;
import com.api.tca.domain.meeting.dto.request.MeetingWebhookDto;
import com.api.tca.domain.meeting.service.MeetingService;
import com.api.tca.domain.transcript.dto.TranscriptFormDataDto;
import com.api.tca.domain.user.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook/meeting")
public class MeetingVoicerWebhookController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private VoicerService voicerService;

    @PostMapping
    public ResponseEntity<?> receiveTranscriptWebhook(@RequestBody @Valid MeetingWebhookDto request, HttpServletRequest http) {
        voicerService.isValidKeyOrThrow(http.getHeader("X-VOICER-SECRET"));

        var user = meetingService.getAudioOwnerByMeetingId(request.jobId());
        var transcriptDto = new TranscriptFormDataDto(request.transcript());
        meetingService.findAndAnalyseMeetingById(request.jobId(), transcriptDto, ProfileTypeByLabel.get(user.getFirstProfileName()), user.getEmail());

        return ResponseEntity.ok().build();
    }
}
