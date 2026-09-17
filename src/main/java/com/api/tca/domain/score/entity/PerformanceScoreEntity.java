package com.api.tca.domain.score.entity;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.salesperson.entity.SalespersonEntity;
import com.api.tca.domain.score.enums.StreakType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meeting_performance_scores")
@Getter
@Setter
public class PerformanceScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "meeting_id")
    private MeetingEntity meeting;

    @ManyToOne
    @JoinColumn(name = "salesperson_id")
    private SalespersonEntity salesperson;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal baseScore;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal multiplier;

    @Enumerated(EnumType.STRING)
    private StreakType streakType;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer streakCount;

    @JdbcTypeCode(SqlTypes.NUMERIC)
    private BigDecimal finalScore;

    private LocalDateTime calculated_at;
}
