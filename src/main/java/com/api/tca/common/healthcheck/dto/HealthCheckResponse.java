package com.api.tca.common.healthcheck.dto;

public record HealthCheckResponse(HealthCheckStatus javaApi, HealthCheckStatus pythonApi)
{

}
