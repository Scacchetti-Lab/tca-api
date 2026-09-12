package com.api.tca.domain.transcript.service;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.transcript.dto.TranscriptFormDataDto;
import com.api.tca.domain.transcript.entity.TranscriptEntity;
import com.api.tca.domain.transcript.enums.TranscriptStatus;
import com.api.tca.domain.transcript.exceptions.TranscriptNotFound;
import com.api.tca.domain.transcript.repository.TranscriptRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TranscriptService {

    @Autowired
    private TranscriptRepository transcriptRepository;

    public TranscriptEntity getTranscriptById(UUID transcriptId) {
        return transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new TranscriptNotFound("Transcrição não encontrada"));
    }

    @Transactional
    public TranscriptEntity addEmptyTranscript(TranscriptFormDataDto request) {
        TranscriptEntity transcript = new TranscriptEntity(request.rawTranscript(), TranscriptStatus.NOT_STARTED);
        return transcriptRepository.save(transcript);
    }

    @Transactional
    public void updateResume(UUID id, String resume) {
       var transcript = getTranscriptById(id);
       transcript.setResume(resume);
       transcriptRepository.save(transcript);
    }

    @Transactional
    public void updateStatus(UUID transcriptId, TranscriptStatus newStatus) {
        TranscriptEntity transcript = getTranscriptById(transcriptId);
        transcript.setStatus(newStatus);
    }
}
