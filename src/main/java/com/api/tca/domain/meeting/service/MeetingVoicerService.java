package com.api.tca.domain.meeting.service;

import com.api.tca.common.helpers.AudioSignature;
import com.api.tca.common.voicer.provider.VoicerProvider;
import com.api.tca.domain.meeting.dto.response.voicer.TranscriptVoicerDto;
import com.api.tca.domain.meeting.exception.rules.InvalidFileUploadedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
public class MeetingVoicerService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private VoicerProvider voicerProvider;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp3", "m4a", "wav", "ogg", "opus", "aac", "flac", "webm");

    public boolean checkFile(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            if (file.isEmpty()) return false;
            String ext = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            if (!ALLOWED_EXTENSIONS.contains(ext)) return false;
            byte[] header = in.readNBytes(AudioSignature.HEADER_SIZE);

            return AudioSignature.isSupported(header);
        }
        catch (IOException ioex) {
            log.error(ioex);
            return false;
        }
    }

    public TranscriptVoicerDto transcriptAudio(MultipartFile file) {
        if (!checkFile(file))
            throw new InvalidFileUploadedException("Transcrição enviada, não é um arquivo de áudio suportado.");

        var data = voicerProvider.transcriptAudio(file);
    }
}
