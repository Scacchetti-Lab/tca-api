package com.api.tca.domain.score.entity;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.score.enums.FinancialImpactLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "meeting_strategic_scores")
@Entity
@Getter
@Setter
public class StrategicScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal baseScore;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal multiplier;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal walletPercentage;

    @Enumerated(EnumType.STRING)
    private FinancialImpactLevel financialImpactLevel;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal financialFactor;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal finalScore;

    private LocalDateTime calculated_at;
}
