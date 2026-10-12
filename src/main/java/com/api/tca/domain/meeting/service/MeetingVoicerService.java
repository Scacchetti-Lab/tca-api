package com.api.tca.domain.meeting.service;

import com.api.tca.common.helpers.AudioSignature;
import com.api.tca.common.voicer.dto.response.BaseResponseDto;
import com.api.tca.common.voicer.provider.VoicerProvider;
import com.api.tca.domain.meeting.dto.request.MeetingLiveDto;
import com.api.tca.domain.meeting.enums.AudioTranscriptType;
import com.api.tca.domain.meeting.exception.rules.InvalidFileUploadedException;
import com.api.tca.domain.salesperson.exceptions.SalespersonNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

@Log4j2
@Service
public class MeetingVoicerService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private VoicerProvider voicerProvider;

    private final String callbackPath = "webhook/meeting";

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp3", "m4a", "wav", "ogg", "opus", "aac", "flac", "webm");

    public boolean checkFile(MultipartFile file)  {
        try (InputStream in = file.getInputStream()) {
            if (file.isEmpty()) return false;
            String name = file.getOriginalFilename();
            if (name == null) return false;
            int dot = name.lastIndexOf('.');
            if (dot < 0 || dot == name.length() - 1) return false;
            String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
            if (!ALLOWED_EXTENSIONS.contains(ext)) return false;
            byte[] header = in.readNBytes(AudioSignature.HEADER_SIZE);

            return AudioSignature.isSupported(header);
        }
        catch (IOException ioex) {
            log.error(ioex);
            return false;
        }
    }

    public BaseResponseDto<?> sendAudioToTranscript(MultipartFile file, MeetingLiveDto liveDto, String apiUrl) {
        if (!checkFile(file))
            throw new InvalidFileUploadedException("Transcrição enviada, não é um arquivo de áudio suportado.");
        int duration = getAudioInMinutes(file);

        var entity = meetingService.createDefaultLiveMeeting(duration, liveDto);
        try {
            return voicerProvider.transcriptAudio(file, entity.id(), String.format("%s/%s", apiUrl, callbackPath));
        }
        catch (IOException ioex) {
            log.error(ioex);
            meetingService.deleteMeeting(entity.id());
            return null;
        }
        catch (RuntimeException ex) {
            meetingService.deleteMeeting(entity.id());
            throw ex;
        }
    }

    private int getAudioInMinutes(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }

            Path tempFile = Files.createTempFile("audio-", extension);
            try {
                file.transferTo(tempFile);
                AudioFile audioFile = AudioFileIO.read(tempFile.toFile());
                return Math.toIntExact(Math.round(audioFile.getAudioHeader().getPreciseTrackLength() / 60.0));
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }
        catch (Exception ex) {
            return 0;
        }
    }
}
