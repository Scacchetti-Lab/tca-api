package com.api.tca.domain.meeting.entity;

import com.api.tca.common.ai.dto.response.transcript.strategic.StrategicAnalyseDto;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.client.enums.FinancialImpact;
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
    private FinancialImpact financialImpactStatus;
    @Column(columnDefinition = "TEXT")
    private String feedback;
    @Column(columnDefinition = "TEXT")
    private String tips;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    public MeetingAnalyseStrategicEntity(MeetingEntity meeting, StrategicAnalyseDto analyseDto) {
        this.meeting = meeting;
        this.client = meeting.getClient();
        this.status = analyseDto.companyStatus();
        this.companyPerformance = analyseDto.metrics().companyPerformance();
        this.closingProbability = analyseDto.metrics().closingProbability();
        this.flexibility = analyseDto.metrics().flexibility();
        this.risk = analyseDto.metrics().risk();
        this.financialImpactGrade = analyseDto.financial().financialImpact();
        this.financialImpact = analyseDto.financial().financialImpactValue();
        this.financialImpactStatus = analyseDto.financial().financialImpactStatus();
        this.feedback = analyseDto.feedback();
        this.tips = analyseDto.insights().tips();
    }
}
