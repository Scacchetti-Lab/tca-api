package com.api.tca.domain.score.service;
import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.repository.ClientRepository;
import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.service.MeetingStrategicService;
import com.api.tca.domain.score.dto.FinancialStrategicScore;
import com.api.tca.domain.score.entity.StrategicScoreEntity;
import com.api.tca.domain.score.enums.FinancialImpactLevel;
import com.api.tca.domain.score.repository.StrategicScoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StrategicScoreService {

    private static final BigDecimal POSITIVE_FLOOR = BigDecimal.valueOf(15);
    private static final BigDecimal NEGATIVE_FLOOR = BigDecimal.valueOf(10);
    private static final BigDecimal INCREMENT = BigDecimal.valueOf(0.1);
    private static final BigDecimal HIGH_FLOOR = BigDecimal.valueOf(15);
    private static final BigDecimal MEDIUM_FLOOR = BigDecimal.valueOf(5);
    private static final BigDecimal HIGH_PERCENT = BigDecimal.valueOf(0.20);
    private static final BigDecimal MEDIUM_PERCENT = BigDecimal.valueOf(0.10);

    @Autowired
    private MeetingStrategicService strategicService;

    @Autowired
    private StrategicScoreRepository strategicRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public void setScorePoints(MeetingEntity entity) {
        var client = entity.getClient();
        var clientMeetings = new ArrayList<>(strategicService.getAllMeetingByClientId(client.getId()));
        clientMeetings.add(entity.getMeetingAnalyseStrategic());
        List<BigDecimal> baseScoresMeetings = clientMeetings.stream().map(this::calculateBaseScore).toList();

        BigDecimal currentBaseScore = calculateBaseScore(entity.getMeetingAnalyseStrategic());
        BigDecimal average = calculateMeetingAverage(baseScoresMeetings);
        BigDecimal multi = calculateMultiplier(baseScoresMeetings);
        FinancialStrategicScore finData = calculateFinancialImpactFactor(entity.getMeetingAnalyseStrategic());
        BigDecimal finalScore = calculateFinalScore(average, multi, finData.financialImpact());

        var scoreEntity = new StrategicScoreEntity();
        scoreEntity.setMeeting(entity);
        scoreEntity.setClient(client);
        scoreEntity.setBaseScore(currentBaseScore);
        scoreEntity.setMultiplier(multi);
        scoreEntity.setFinancialFactor(finData.financialImpact());
        scoreEntity.setWalletPercentage(finData.percentage());
        scoreEntity.setFinancialImpactLevel(finData.level());
        scoreEntity.setFinalScore(finalScore);
        scoreEntity.setCalculated_at(BrazilRealTime.now());

        strategicRepository.save(scoreEntity);

        client.setScore(getFinalScoreAvg(client));
        clientRepository.save(client);
    }

    private BigDecimal calculateMultiplier(List<BigDecimal> baseScoresMeetings) {
        if (baseScoresMeetings.size() < 2) {
            return BigDecimal.ONE;
        }

        BigDecimal positive = BigDecimal.ZERO;
        BigDecimal negative = BigDecimal.ZERO;

        for (int i = 1; i < baseScoresMeetings.size(); i++) {
            BigDecimal atual = baseScoresMeetings.get(i);
            BigDecimal anterior = baseScoresMeetings.get(i - 1);
            BigDecimal delta = atual.subtract(anterior);

            if (delta.compareTo(BigDecimal.ZERO) > 0) {
                positive = positive.add(delta);
            } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
                negative = negative.add(delta.abs());
            }
        }

        int multiPositive = positive.divideToIntegralValue(POSITIVE_FLOOR).intValue();
        int multiNegative = negative.divideToIntegralValue(NEGATIVE_FLOOR).intValue();

        BigDecimal bonus = INCREMENT.multiply(BigDecimal.valueOf(multiPositive));
        BigDecimal penalty = INCREMENT.multiply(BigDecimal.valueOf(multiNegative));

        BigDecimal multi = BigDecimal.ONE.add(bonus).subtract(penalty);
        return BigDecimal.valueOf(Math.clamp(multi.doubleValue(), 0.5, 2.0));
    }

    private BigDecimal calculateNetValue(MeetingAnalyseStrategicEntity entity) {
        BigDecimal avgPositive = getAveragePositivePoints(entity);
        int comparison = avgPositive.compareTo(BigDecimal.valueOf(entity.getRisk()));

        if (comparison > 0)
            return entity.getFinancialImpact();
        else if (comparison < 0)
            return entity.getFinancialImpact().negate();
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateImpactPercentage(MeetingAnalyseStrategicEntity entity) {
        UUID squadId = entity.getClient().getSquad().getId();
        BigDecimal walletRevenue = clientRepository.sumRevenueBySquadId(squadId);

        if (walletRevenue.compareTo(BigDecimal.ZERO) <= 0) return null;

        BigDecimal netVal = calculateNetValue(entity);

        return netVal
                .divide(walletRevenue, 10, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private FinancialStrategicScore calculateFinancialImpactFactor(MeetingAnalyseStrategicEntity entity) {
        BigDecimal percentage = calculateImpactPercentage(entity);
        if (percentage == null) return new FinancialStrategicScore(null, BigDecimal.ONE, null);
        BigDecimal abs = percentage.abs();

        boolean isHigh = abs.compareTo(HIGH_FLOOR) > 0;
        boolean isMedium = abs.compareTo(MEDIUM_FLOOR) >= 0;

        BigDecimal adjust = isHigh ? HIGH_PERCENT : isMedium ? MEDIUM_PERCENT : BigDecimal.ZERO;
        BigDecimal financialImpact = percentage.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ONE.add(adjust) : BigDecimal.ONE.subtract(adjust);
        FinancialImpactLevel level = isHigh ? FinancialImpactLevel.HIGH : isMedium ? FinancialImpactLevel.MEDIUM : FinancialImpactLevel.LOW;

        return new FinancialStrategicScore(percentage, financialImpact, level);
    }

    private BigDecimal getFinalScoreAvg(ClientEntity client) {
        return strategicRepository
                .findAllByClientId(client.getId())
                .stream()
                .map(StrategicScoreEntity::getFinalScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(strategicRepository.countByClientId(client.getId())), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getAveragePositivePoints(MeetingAnalyseStrategicEntity analysis) {
        BigDecimal sum = BigDecimal.valueOf(analysis.getCompanyPerformance())
                .add(BigDecimal.valueOf(analysis.getClosingProbability()))
                .add(BigDecimal.valueOf(analysis.getFlexibility()));
        return sum.divide(BigDecimal.valueOf(3), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMeetingAverage(List<BigDecimal> baseScoresMeetings) {
        BigDecimal soma = baseScoresMeetings.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(baseScoresMeetings.size()), 4, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateBaseScore(MeetingAnalyseStrategicEntity entity) {
        return BigDecimal.valueOf(Math.round((entity.getCompanyPerformance() + entity.getClosingProbability() + entity.getFlexibility()) - entity.getRisk() + 10));
    }

    public BigDecimal calculateFinalScore(BigDecimal average, BigDecimal multi, BigDecimal financialImpact) {
        return average.multiply(multi).multiply(financialImpact).setScale(2, RoundingMode.HALF_UP);
    }
}
