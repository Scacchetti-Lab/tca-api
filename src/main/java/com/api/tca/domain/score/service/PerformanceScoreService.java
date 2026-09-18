package com.api.tca.domain.score.service;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.service.MeetingService;
import com.api.tca.domain.salesperson.entity.SalespersonEntity;
import com.api.tca.domain.salesperson.service.SalespersonService;
import com.api.tca.domain.score.dto.serviceParams.PerformancePointsDto;
import com.api.tca.domain.score.entity.PerformanceScoreEntity;
import com.api.tca.domain.score.enums.StreakType;
import com.api.tca.domain.score.interfaces.ScoreImplements;
import com.api.tca.domain.score.repository.PerformanceScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PerformanceScoreService implements ScoreImplements {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private PerformanceScoreRepository scoreRepository;

    @Autowired
    private SalespersonService salespersonService;

    @Override
    public void setScorePoints(MeetingEntity entity) {
        int baseScore = calculateBaseScore(entity.getMeetingAnalysePerformance());
        var sellers = entity.getUsers().stream().filter(u -> u.getFirstProfileName().contains("Salesperson")).toList();

        List<PerformanceScoreEntity> performances = new ArrayList<>();
        sellers.forEach(seller -> {
            var sellerEntity = salespersonService.findByUserId(seller.getId());
            var lastPerformanceScores = getLastMeetingScores(seller.getId());
            if (lastPerformanceScores.isEmpty()) {
                var scoreEntity = new PerformanceScoreEntity(
                    entity, sellerEntity,
                    BigDecimal.valueOf(baseScore), BigDecimal.valueOf(1),
                    StreakType.STABLE, 1, null // finalscore
                );
            }
            var mostRecentAnalysis = lastPerformanceScores.getFirst();
            StreakType streakType = setStreakTypeByHistory(baseScore, mostRecentAnalysis.getBaseScore().intValue());
            int streakVal = 1;
            if (streakType == StreakType.STABLE) streakVal = 0;
            else streakVal = setStreakVal(lastPerformanceScores, streakType);


            performances.add(new PerformanceScoreEntity(
                    entity,
                    sellerEntity,
                    BigDecimal.valueOf(baseScore),
                    null, // multiplicador
                    streakType,
                    streakVal,
                    null // final score
            ));
        });

        scoreRepository.saveAll(performances);
    }

    private int setStreakVal(List<PerformanceScoreEntity> analyses, StreakType newStreak) {
        List<StreakType> lastStreaks = new ArrayList<>(
                analyses.stream().map(PerformanceScoreEntity::getStreakType).toList()
        );
        lastStreaks.add(newStreak);

        int streak = 1; // o proprio newStreak ja conta como 1
        for (int i = lastStreaks.size() - 1; i > 0; i--) {
            if (lastStreaks.get(i) == lastStreaks.get(i - 1))
                streak++;
            else
                break;
        }
        return streak;
    }

    private StreakType setStreakTypeByHistory(int baseScore, int lastScore) {
        if (baseScore > lastScore)
            return StreakType.HIGH;
        else if (baseScore < lastScore)
            return StreakType.FALL;
        else
            return StreakType.STABLE;
    }

    public Integer calculateBaseScore(MeetingAnalysePerformanceEntity entity) {
        var points = new PerformancePointsDto(entity);
        return (int) Math.round((points.engagement() + points.communication() + points.objectionHandling() + points.opportunities()) - points.missedOpportunities());
    }

    @Override
    public BigDecimal calculateFinalScore(Integer baseScore, Double multi) {
        double result = baseScore + (baseScore * multi);
        return BigDecimal.valueOf(result);
    }

    public List<PerformanceScoreEntity> getLastMeetingScores(UUID userId) {
        var pageable = PageRequest.of(0, 4, Sort.by("scheduled").descending());
        var lastMeetings = meetingService.getAllMeetingByUserId(userId, pageable).getContent();
        if (lastMeetings.isEmpty()) return List.of();
        List<PerformanceScoreEntity> performances = new ArrayList<>(lastMeetings.stream().map(MeetingEntity::getPerformanceScore).toList());
        Collections.reverse(performances);
        return performances;
    }

}
