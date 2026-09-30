package com.api.tca.common.voicer.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ReadAudioDto(MultipartFile audio) { }
