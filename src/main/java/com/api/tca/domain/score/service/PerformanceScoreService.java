package com.api.tca.domain.score.service;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.service.MeetingService;
import com.api.tca.domain.salesperson.exceptions.SalespersonNotFoundException;
import com.api.tca.domain.salesperson.service.SalespersonService;
import com.api.tca.domain.score.dto.serviceParams.PerformancePointsDto;
import com.api.tca.domain.score.entity.PerformanceScoreEntity;
import com.api.tca.domain.score.enums.StreakType;
import com.api.tca.domain.score.interfaces.ScorePerformanceImplements;
import com.api.tca.domain.score.repository.PerformanceScoreRepository;
import com.api.tca.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class PerformanceScoreService implements ScorePerformanceImplements {

    @Autowired
    private PerformanceScoreRepository scoreRepository;

    @Autowired
    private SalespersonService salespersonService;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void setScorePoints(MeetingService meetingService, MeetingEntity entity) {
        int baseScore = calculateBaseScore(entity.getMeetingAnalysePerformance());

        var participants = entity.getUsers().stream()
                .filter(u -> u.getFirstProfileName().contains("Salesperson"))
                .toList();

        if (participants.isEmpty()) {
            throw new SalespersonNotFoundException("Nenhum funcionário do sistema foi encontrado na reunião");
        }

        List<PerformanceScoreEntity> performances = new ArrayList<>();
        List<UUID> sellerIds = new ArrayList<>();

        for (var user : participants) {
            var seller = salespersonService.findByUserId(user.getId());
            var history = getLastMeetingScores(seller.getId());

            var newPerformanceScore = new PerformanceScoreEntity();
            newPerformanceScore.setMeeting(entity);
            newPerformanceScore.setSalesperson(seller);
            newPerformanceScore.setBaseScore(BigDecimal.valueOf(baseScore));
            newPerformanceScore.setCalculated_at(BrazilRealTime.now());

            calculateScore(newPerformanceScore, baseScore, history);

            performances.add(newPerformanceScore);
            sellerIds.add(seller.getUser().getId());
        }

        scoreRepository.saveAll(performances);

        sellerIds.forEach(this::setUserScoreAverage);
    }

    @Transactional
    public void reprocessFullHistory(UUID salespersonId) {
        List<PerformanceScoreEntity> fullHistory = scoreRepository
                .findAllBySalespersonIdOrderByMeeting_ScheduledAsc(salespersonId);

        List<PerformanceScoreEntity> recalculated = new ArrayList<>();

        for (PerformanceScoreEntity current : fullHistory) {
            int baseScore = current.getBaseScore().intValue();

            calculateScore(current, baseScore, recalculated);

            recalculated.add(current);
        }

        scoreRepository.saveAll(recalculated);
    }

    private void calculateScore(
            PerformanceScoreEntity entity,
            int baseScore,
            List<PerformanceScoreEntity> lastHistory
    ) {
        if (lastHistory.isEmpty()) {
            setDefaultValues(entity, baseScore);
            return;
        }

        var mostRecent = lastHistory.getLast();

        StreakType streakType = setStreakTypeByHistory(baseScore, mostRecent.getBaseScore().intValue());
        int streakCount = (streakType == StreakType.STABLE)
                ? 0
                : setStreakVal(lastHistory, streakType);

        double multi = calculateMultiplier(streakType, streakCount);

        entity.setStreakType(streakType);
        entity.setStreakCount(streakCount);
        entity.setMultiplier(BigDecimal.valueOf(multi));
        entity.setFinalScore(calculateFinalScore(baseScore, multi));
    }

    private void setDefaultValues(PerformanceScoreEntity entity, int baseScore) {
        entity.setStreakType(StreakType.STABLE);
        entity.setStreakCount(0);
        entity.setMultiplier(BigDecimal.valueOf(1.0));
        entity.setFinalScore(calculateFinalScore(baseScore, 1.0));
    }

    private double calculateMultiplier(StreakType streakType, int streakCount) {
        double multi = 1.0;

        if (streakType == StreakType.HIGH)
            multi += 0.1 * streakCount;
        else if (streakType == StreakType.FALL)
            multi -= 0.1 * streakCount;

        return Math.clamp(multi, 0.5, 2.0);
    }

    private int setStreakVal(List<PerformanceScoreEntity> historico, StreakType newStreak) {
        List<StreakType> lastStreaks = new ArrayList<>(
                historico.stream().map(PerformanceScoreEntity::getStreakType).toList()
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
        if (baseScore > lastScore)
            return StreakType.HIGH;
        else if (baseScore < lastScore)
            return StreakType.FALL;
        else
            return StreakType.STABLE;
    }

    public Integer calculateBaseScore(MeetingAnalysePerformanceEntity entity) {
        var points = new PerformancePointsDto(entity);
        return (int) Math.round(
                (points.engagement() + points.communication() + points.objectionHandling() + points.opportunities())
                        - points.missedOpportunities() + 8
        );
    }

    @Transactional
    public void setUserScoreAverage(UUID userId) {
        var seller = salespersonService.findByUserId(userId);
        var finalScoreAvg = scoreRepository
                .findAllBySalespersonIdOrderByMeeting_ScheduledAsc(seller.getId())
                .stream().map(PerformanceScoreEntity::getFinalScore)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);

        var user = seller.getUser();
        user.setScore((int) Math.round(finalScoreAvg));
        userRepository.save(user);
    }

    @Override
    public BigDecimal calculateFinalScore(Integer baseScore, Double multi) {
        double result = baseScore + (baseScore * (multi - 1.0));
        return BigDecimal.valueOf(result);
    }

    public List<PerformanceScoreEntity> getLastMeetingScores(UUID salespersonId) {
        List<PerformanceScoreEntity> lastFourMeetings = scoreRepository
                .findTop4BySalespersonIdOrderByMeeting_ScheduledDesc(salespersonId);

        Collections.reverse(lastFourMeetings);
        return lastFourMeetings;
    }
}