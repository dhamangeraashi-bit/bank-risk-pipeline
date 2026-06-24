package com.yourname.etl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    public void exportSuspiciousTransactions(List<TransactionRecord> records, String outputFile) throws IOException {

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("transactionId,customerId,amount,transactionType,riskScore\n");

            for (TransactionRecord record : records) {
                if (record.isSuspicious()) {
                    writer.write(
                        record.getTransactionId() + "," +
                        record.getCustomerId() + "," +
                        record.getAmount() + "," +
                        record.getTransactionType() + "," +
                        record.getRiskScore() + "\n"
                    );
                }
            }
        }
    }
}