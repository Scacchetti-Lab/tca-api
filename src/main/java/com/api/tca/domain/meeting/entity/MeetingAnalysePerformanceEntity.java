package com.api.tca.domain.meeting.entity;

import com.api.tca.common.ai.dto.response.transcript.performance.PerformanceAnalyseDto;
import com.api.tca.common.helpers.BrazilRealTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meeting_performance_analyses")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MeetingAnalysePerformanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double engagement;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double communicationQuality;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double opportunitiesSeized;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double objectionHandling;
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double missedOpportunities;
    private Integer leadsConverted;

    @Column(columnDefinition = "TEXT")
    private String tips;
    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime createdAt;

    public MeetingAnalysePerformanceEntity(MeetingEntity meeting, PerformanceAnalyseDto analyseDto) {
        this.meeting = meeting;
        this.engagement = analyseDto.metrics().engagement();
        this.communicationQuality = analyseDto.metrics().communicationQuality();
        this.opportunitiesSeized = analyseDto.metrics().opportunitiesSeized();
        this.objectionHandling = analyseDto.metrics().objectionHandling();
        this.missedOpportunities = analyseDto.metrics().missedOpportunities();
        this.leadsConverted = analyseDto.metrics().leadsConverted();
        this.tips = analyseDto.insights().tips();
        this.feedback = analyseDto.feedback();
        this.createdAt = BrazilRealTime.now();
    }
}
