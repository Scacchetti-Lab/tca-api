package com.api.tca.domain.email.service;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.email.configuration.MailerooConfig;
import com.api.tca.domain.email.dto.maileroo.MailerooDto;
import com.api.tca.domain.email.dto.maileroo.MailerooEmailAddress;
import com.api.tca.domain.email.dto.maileroo.MailerooEmailRequest;
import com.api.tca.domain.email.entity.EmailEntity;
import com.api.tca.domain.email.exception.EmailFailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class MailerooService {

    @Autowired
    private MailerooConfig config;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper mapper;

    @Async
    public void sendEmail(EmailEntity email, String toDisplayName) {
        URI uri = URI.create(config.getSmtpBaseUrl() + "/emails");
        String bodyRequest = createBody(email, toDisplayName);
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getSmtpSendingKey())
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            MailerooDto responseDto = mapper.readValue(response.body(), MailerooDto.class);

            if (response.statusCode() != 200 || !responseDto.success()) {
                throw new EmailFailed(responseDto.message());
            }

            CompletableFuture.completedFuture(responseDto);

        } catch (IOException e) {
            throw new EmailFailed("Falha na comunicação com o Maileroo: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EmailFailed("Envio interrompido");
        }
    }


    private String createBody(EmailEntity email, String toDisplayName) {
        var host = new MailerooEmailAddress(config.getSmtpHost(), "TOTVS AI - Support");
        var to = new MailerooEmailAddress(email.getTo(), toDisplayName);

        var request = new MailerooEmailRequest(
                host,
                Set.of(to),
                email.getSubject(),
                email.getBody(),
                null,
                false,
                BrazilRealTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
        );

        return mapper.writeValueAsString(request);
    }
}
