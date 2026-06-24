package com.yourname.etl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EtlPipeline {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/bank_risk";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "BankRisk2026";

    public static void main(String[] args) throws Exception {

        CsvReader reader = new CsvReader();
        TransactionParser parser = new TransactionParser();
        RiskScorer scorer = new RiskScorer();
        ReportGenerator reportGenerator = new ReportGenerator();

        List<String> lines = reader.readLines("data/raw/bank_transactions_data_2_augmented_clean_2.csv");
        List<TransactionRecord> records = new ArrayList<>();

        int rowsRead = 0;
        int rowsDropped = 0;

        for (String line : lines) {
            rowsRead++;
            TransactionRecord record = parser.parse(line);
            if (record != null) {
                records.add(record);
            } else {
                rowsDropped++;
            }
        }
        rowsRead--; // correct for header line counted above

        for (TransactionRecord record : records) {
            record.setRiskScore(scorer.calculateRiskScore(record));
        }

        for (TransactionRecord record : records) {
            double finalScore = scorer.applyVelocityScore(record, records);
            record.setRiskScore(finalScore);
            record.setSuspicious(scorer.isSuspicious(finalScore));
        }

        System.out.println("Rows read:    " + rowsRead);
        System.out.println("Rows dropped: " + rowsDropped);
        System.out.println("Rows valid:   " + records.size());

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            loadToDatabase(conn, records);
            System.out.println("Loaded " + records.size() + " transactions into the database.");
        }

        reportGenerator.exportSuspiciousTransactions(records, "data/raw/output.csv");

        long suspiciousCount = records.stream().filter(TransactionRecord::isSuspicious).count();
        System.out.println("Flagged " + suspiciousCount + " suspicious transactions.");
        System.out.println("ETL Pipeline completed successfully.");
    }

    private static void loadToDatabase(Connection conn, List<TransactionRecord> records) throws SQLException {
        String customerSql = "INSERT INTO customers (customer_id) VALUES (?) ON CONFLICT (customer_id) DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(customerSql)) {
            List<String> seen = new ArrayList<>();
            for (TransactionRecord r : records) {
                if (seen.contains(r.getCustomerId())) continue;
                seen.add(r.getCustomerId());
                stmt.setString(1, r.getCustomerId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }

        String txnSql = "INSERT INTO transactions " +
                "(transaction_id, customer_id, amount, transaction_type, channel, transaction_date, risk_score, suspicious) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (transaction_id) DO NOTHING";

        try (PreparedStatement stmt = conn.prepareStatement(txnSql)) {
            int batchCount = 0;
            for (TransactionRecord r : records) {
                stmt.setString(1, r.getTransactionId());
                stmt.setString(2, r.getCustomerId());
                stmt.setDouble(3, r.getAmount());
                stmt.setString(4, r.getTransactionType());
                stmt.setString(5, r.getChannel());
                stmt.setTimestamp(6, java.sql.Timestamp.valueOf(r.getTransactionDate()));
                stmt.setDouble(7, r.getRiskScore());
                stmt.setBoolean(8, r.isSuspicious());
                stmt.addBatch();
                batchCount++;
                if (batchCount % 1000 == 0) stmt.executeBatch(); // batch every 1000 rows — 50K single-row inserts would be very slow otherwise
            }
            stmt.executeBatch();
        }
    }
}