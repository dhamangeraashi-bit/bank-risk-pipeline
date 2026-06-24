package com.yourname.etl;

import java.time.LocalTime;
import java.util.List;

public class RiskScorer {

    private static final double LARGE_AMOUNT_THRESHOLD = 1000;   // tuned for this dataset's actual amount range
    private static final double STRUCTURING_LOWER_BOUND = 900;
    private static final int VELOCITY_WINDOW_MINUTES = 15;
    private static final int VELOCITY_COUNT_THRESHOLD = 2;

    public double calculateRiskScore(TransactionRecord record) {
        double score = 0.0;

        if (record.getAmount() > LARGE_AMOUNT_THRESHOLD) {
            score += 40;
        }

        if (record.getAmount() >= STRUCTURING_LOWER_BOUND && record.getAmount() <= LARGE_AMOUNT_THRESHOLD) {
            score += 35;
        }

        if ("Debit".equalsIgnoreCase(record.getTransactionType())) {
            score += 10;
        }

        if ("Online".equalsIgnoreCase(record.getChannel())) {
            score += 15; // online channel carries more fraud risk than branch/ATM
        }

        LocalTime time = record.getTransactionDate().toLocalTime();
        if (time.isAfter(LocalTime.of(0, 0)) && time.isBefore(LocalTime.of(5, 0))) {
            score += 20;
        }

        return score;
    }

    public double applyVelocityScore(TransactionRecord record, List<TransactionRecord> allRecords) {
        long nearbyCount = allRecords.stream()
                .filter(r -> r.getCustomerId().equals(record.getCustomerId()))
                .filter(r -> !r.getTransactionId().equals(record.getTransactionId()))
                .filter(r -> Math.abs(
                        java.time.Duration.between(record.getTransactionDate(), r.getTransactionDate()).toMinutes()
                ) <= VELOCITY_WINDOW_MINUTES)
                .count();

        double velocityScore = 0;
        if (nearbyCount >= VELOCITY_COUNT_THRESHOLD) {
            velocityScore = 25;
        }
        return record.getRiskScore() + velocityScore;
    }

    public boolean isSuspicious(double riskScore) {
        return riskScore >= 50;
    }
}