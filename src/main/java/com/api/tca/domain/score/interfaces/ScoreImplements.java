package com.api.tca.domain.score.interfaces;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;

import java.math.BigDecimal;

public interface ScoreImplements {
    void setScorePoints(MeetingEntity entity);
    Integer calculateBaseScore(MeetingAnalysePerformanceEntity entity);
    BigDecimal calculateFinalScore(Integer baseScore, Double multi);
}
