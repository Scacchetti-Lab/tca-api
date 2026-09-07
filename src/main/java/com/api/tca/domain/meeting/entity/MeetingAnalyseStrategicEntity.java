package com.api.tca.domain.meeting.entity;

import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.meeting.enums.MeetingFinancialImpact;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "meeting_strategic_analyses")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MeetingAnalyseStrategicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_status")
    private ClientStatus status;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double companyPerformance;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double closingProbability;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double flexibility;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double risk;
    @JdbcTypeCode(SqlTypes.NUMERIC)

    @Column(name = "financial_impact")
    private Double financialImpactGrade;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    @Column(name = "financial_impact_value")
    private BigDecimal financialImpact;

    @Enumerated(EnumType.STRING)
    private MeetingFinancialImpact financialImpactStatus;
    private String feedback;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;
}
