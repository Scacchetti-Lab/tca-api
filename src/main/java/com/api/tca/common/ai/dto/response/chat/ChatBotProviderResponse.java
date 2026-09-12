package com.api.tca.common.ai.dto.response.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatBotProviderResponse(
        String reply,

        @JsonProperty("previous_interaction_id")
        String previousInteractId,

        @JsonProperty("tokens_used")
        Integer tokensUsed
) {
}
