package com.api.tca.common.voicer.dto.response;

import org.springframework.web.multipart.MultipartFile;

public record BaseResponseDto<T>(
        String status,
        T data,
        String message
) { }
