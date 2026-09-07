package com.api.tca.domain.client.dto.analyse;

public record ClientRecommendationDto(String summary, ClientFeedbackDto strategic, ClientFeedbackDto performance) {

    private record ClientFeedbackDto(String feedback, String tip) {}
}
