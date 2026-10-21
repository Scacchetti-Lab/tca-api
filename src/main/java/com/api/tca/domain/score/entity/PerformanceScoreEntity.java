package com.api.tca.domain.score.entity;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.salesperson.entity.SalespersonEntity;
import com.api.tca.domain.score.enums.StreakType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class PerformanceScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public PerformanceScoreEntity(MeetingEntity meeting, SalespersonEntity seller,
                                  BigDecimal baseScore, BigDecimal multiplier, StreakType streakType,
                                  Integer streakCount, BigDecimal finalScore)
    {
        this.meeting = meeting;
        this.salesperson = seller;
        this.baseScore = baseScore;
        this.multiplier = multiplier;
        this.streakType = streakType;
        this.streakCount = streakCount;
        this.finalScore = finalScore;
        this.calculated_at = BrazilRealTime.now();
    }

    @Override
    public String toString() {
        return String.format("""
                Reunião: %s
                Vendedor: %s
                Score base: %.2f
                Multiplier: %.2f
                StreakType: %s
                StreakCount: %d
                FinalScore: %.2f
                """, this.meeting.getTitle(), this.salesperson.getUser().getFullName(), this.baseScore, this.multiplier, this.streakType, this.streakCount, this.finalScore);
    }
}
