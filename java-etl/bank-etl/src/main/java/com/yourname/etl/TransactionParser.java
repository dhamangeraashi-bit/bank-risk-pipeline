package com.yourname.etl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TransactionParser {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm");

    private static final DateTimeFormatter DATE_ONLY_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy");

    public TransactionRecord parse(String line) {

        if (line == null || line.isBlank()) {
            return null;
        }

        line = line.replace("\uFEFF", "").replace("\r", "").trim();

        String[] parts = line.split(",");

        if (parts[0].equalsIgnoreCase("TransactionID")) {
            return null;
        }

        if (parts.length < 10) {
            System.out.println("Skipping invalid line (wrong column count): " + line);
            return null;
        }

        try {
            TransactionRecord record = new TransactionRecord();
            record.setTransactionId(parts[0].trim());
            record.setCustomerId(parts[1].trim());
            record.setAmount(Double.parseDouble(parts[2].trim()));
            record.setTransactionDate(parseFlexibleDate(parts[3].trim()));
            record.setTransactionType(parts[4].trim());
            record.setChannel(parts[9].trim());
            return record;

        } catch (NumberFormatException e) {
            System.out.println("Skipping invalid line (bad amount): " + line);
            return null;
        } catch (DateTimeParseException e) {
            System.out.println("Skipping invalid line (bad date): " + line);
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Skipping invalid line (missing column): " + line);
            return null;
        }
    }

    /**
     * Handles two date formats seen in the real dataset:
     *  - "4/11/2023 16:29"  (date + time)
     *  - "7/13/2023"        (date only, no time)
     * Date-only values are treated as midnight (00:00) on that day.
     */
    private LocalDateTime parseFlexibleDate(String rawDate) {
        if (rawDate.contains(":")) {
            return LocalDateTime.parse(rawDate, DATE_TIME_FORMAT);
        } else {
            LocalDate dateOnly = LocalDate.parse(rawDate, DATE_ONLY_FORMAT);
            return dateOnly.atStartOfDay();
        }
    }
}