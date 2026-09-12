package com.api.tca.common.ai.dto.response.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatBotFirstResponse(
        String title,
        String reply,

        @JsonProperty("previous_interact_id")
        String previousInteractId,

        @JsonProperty("tokens_used")
        Integer tokensUsed
) {
}
