package com.api.tca.domain.email.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tca.email")
@Getter
@Setter
public class MailerooConfig {
    private String smtpHost;
    private String smtpPassword;
    private String smtpSendingKey;
    private String smtpBaseUrl;
}
