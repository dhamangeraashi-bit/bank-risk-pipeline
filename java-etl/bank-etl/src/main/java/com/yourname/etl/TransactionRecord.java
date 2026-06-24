package com.yourname.etl;

import java.time.LocalDateTime;

public class TransactionRecord {

    private String transactionId;
    private String customerId;       // AccountID
    private double amount;
    private String transactionType;  // Credit / Debit
    private LocalDateTime transactionDate;
    private String channel;          // ATM / Branch / Online
    private double riskScore;
    private boolean suspicious;

    public TransactionRecord() {
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

    public boolean isSuspicious() { return suspicious; }
    public void setSuspicious(boolean suspicious) { this.suspicious = suspicious; }
}