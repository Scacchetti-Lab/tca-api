package com.api.tca.domain.client.entity;

import com.api.tca.domain.client.enums.FinancialImpact;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "client_current_analysis")
public class ClientAnalysisView {
    @Id
    @Column(name = "client_id")
    private UUID clientId;

    private BigDecimal performance;

    @Column(name = "closing_probability")
    private BigDecimal closingProbability;

    private BigDecimal flexibility;
    private BigDecimal risk;

    @Column(name = "financial_impact")
    private BigDecimal financialImpact;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status")
    private FinancialImpact financialStatus;

    @Column(name = "meetings_analysed")
    private Integer meetingsAnalysed;

    @Column(name = "last_analysis_at")
    private OffsetDateTime lastAnalysisAt;
}