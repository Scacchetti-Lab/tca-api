package com.api.tca.domain.salesperson.dto;

public record UpdateSalespersonDto(
        String squadCode,
        Boolean needTraining,
        Integer totalMeetings
) {
}
