package com.api.tca.domain.chat.dto.request;

import com.api.tca.domain.chat.enums.AiModel;
import jakarta.validation.constraints.NotBlank;

public record NewChatDto(@NotBlank String input, AiModel model) { }
