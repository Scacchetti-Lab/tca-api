package com.api.tca.domain.meeting.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private String tips;
    private String feedback;

    private LocalDateTime createdAt;
}
