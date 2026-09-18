package com.api.tca.domain.score.service;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.service.MeetingService;
import com.api.tca.domain.salesperson.exceptions.SalespersonNotFoundException;
import com.api.tca.domain.salesperson.service.SalespersonService;
import com.api.tca.domain.score.dto.serviceParams.PerformancePointsDto;
import com.api.tca.domain.score.entity.PerformanceScoreEntity;
import com.api.tca.domain.score.enums.StreakType;
import com.api.tca.domain.score.interfaces.ScoreImplements;
import com.api.tca.domain.score.repository.PerformanceScoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PerformanceScoreService implements ScoreImplements {

    @Autowired
    private PerformanceScoreRepository scoreRepository;

    @Autowired
    private SalespersonService salespersonService;


    @Override
    @Transactional
    public void setScorePoints(MeetingService meetingService, MeetingEntity entity) {
        int baseScore = calculateBaseScore(entity.getMeetingAnalysePerformance());
        var participants = entity.getUsers().stream().filter(u -> u.getFirstProfileName().contains("Salesperson")).toList();;
        if (participants.isEmpty()) {
            throw new SalespersonNotFoundException("Nenhum funcionário do sistema foi encontrado na reunião");
        }

        List<PerformanceScoreEntity> performances = new ArrayList<>();
        participants.forEach(user -> {
            System.out.println("entrei no loop");
            var seller = salespersonService.findByUserId(user.getId());
            var lastPerformanceScores = getLastMeetingScores(meetingService, user.getId());
            if (lastPerformanceScores.isEmpty()) {
                System.out.println("entrei no IF");
                var finalScore = calculateFinalScore(baseScore, 1.0);
                var scoreEntity = new PerformanceScoreEntity(
                    entity, seller,
                    BigDecimal.valueOf(baseScore), BigDecimal.valueOf(1),
                    StreakType.STABLE, 1, finalScore
                );
                performances.add(scoreEntity);
                return;
            }
            System.out.println("passei do IF");
            var mostRecentAnalysis = lastPerformanceScores.getFirst();
            StreakType streakType = setStreakTypeByHistory(baseScore, mostRecentAnalysis.getBaseScore().intValue());
            int streakVal;
            if (streakType == StreakType.STABLE) streakVal = 0;
            else streakVal = setStreakVal(lastPerformanceScores, streakType);
            Double multi = calculateMultiplier(lastPerformanceScores, mostRecentAnalysis.getMultiplier().doubleValue());
            var finalScore = calculateFinalScore(baseScore, multi);
            System.out.println("calculei tudo");

            performances.add(new PerformanceScoreEntity(
                    entity,
                    seller,
                    BigDecimal.valueOf(baseScore),
                    BigDecimal.valueOf(multi),
                    streakType,
                    streakVal,
                    finalScore
            ));
            System.out.println(performances);
        });
        scoreRepository.saveAll(performances);
    }

    private Double calculateMultiplier(List<PerformanceScoreEntity> performances, Double currentMulti) {
        System.out.println("calculei multi");
        double multi = 1.0;
        if (currentMulti != null) multi = currentMulti;

        var highStreak = performances.stream().filter(p -> p.getStreakType() == StreakType.HIGH).count();
        var fallStreak = performances.stream().filter(p -> p.getStreakType() == StreakType.FALL).count();

        if (highStreak > 0)
            multi += 0.1 * highStreak;

        if (fallStreak > 0)
            multi -= 0.1 * fallStreak;

        if (multi > 2)
            multi = 2;

        if (multi < 0.5)
            multi = 0.5;

        return multi;
    }

    private int setStreakVal(List<PerformanceScoreEntity> analyses, StreakType newStreak) {
        System.out.println("calculei streak");
        List<StreakType> lastStreaks = new ArrayList<>(
                analyses.stream().map(PerformanceScoreEntity::getStreakType).toList()
        );
        lastStreaks.add(newStreak);

        int streak = 1;
        for (int i = lastStreaks.size() - 1; i > 0; i--) {
            if (lastStreaks.get(i) == lastStreaks.get(i - 1))
                streak++;
            else
                break;
        }
        return streak;
    }

    private StreakType setStreakTypeByHistory(int baseScore, int lastScore) {
        System.out.println("calculei streak type");
        if (baseScore > lastScore)
            return StreakType.HIGH;
        else if (baseScore < lastScore)
            return StreakType.FALL;
        else
            return StreakType.STABLE;
    }

    public Integer calculateBaseScore(MeetingAnalysePerformanceEntity entity) {
        System.out.println("calculei score base");
        var points = new PerformancePointsDto(entity);
        return (int) Math.round((points.engagement() + points.communication() + points.objectionHandling() + points.opportunities()) - points.missedOpportunities());
    }

    @Override
    public BigDecimal calculateFinalScore(Integer baseScore, Double multi) {
        System.out.println("calculei score final");
        double result = baseScore + (baseScore * (multi - 1.0));
        return BigDecimal.valueOf(result);
    }

    public List<PerformanceScoreEntity> getLastMeetingScores(MeetingService meetingService, UUID userId) {
        var pageable = PageRequest.of(0, 4, Sort.by("scheduled").descending());
        var lastMeetings = meetingService.getAllMeetingByUserId(userId, pageable).getContent();
        if (lastMeetings.isEmpty()) return List.of();
        List<PerformanceScoreEntity> performances = new ArrayList<>(lastMeetings.stream().map(MeetingEntity::getPerformanceScore).toList());
        Collections.reverse(performances);
        return performances;
    }
}
