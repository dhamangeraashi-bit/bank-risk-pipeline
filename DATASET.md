# Dataset

This project uses the **Bank Transaction Dataset for Fraud Detection** 
(`bank_transactions_data_2_augmented_clean_2.csv`), sourced from Kaggle:

https://www.kaggle.com/datasets/thuandao/bank-transactions-dataset-for-fraud-detection

## Dataset Summary
- **Rows:** 50,000 transactions
- **Unique accounts:** 495
- **Columns:** TransactionID, AccountID, TransactionAmount, TransactionDate, 
  TransactionType (Credit/Debit), Location, DeviceID, IP Address, MerchantID, 
  Channel (ATM/Branch/Online), CustomerAge, CustomerOccupation, 
  TransactionDuration, LoginAttempts, AccountBalance

## Data Quality Notes
- ~87% of records (43,750 rows) have date-only timestamps with no time 
  component; these were parsed and defaulted to midnight (00:00) for 
  processing. This is handled explicitly in `TransactionParser.java` 
  (see `parseFlexibleDate()`), and is also noted as a limitation in the 
  main README, since it affects the late-night risk scoring rule.
- 1 row out of 50,000 was dropped during parsing due to a malformed field.

The raw CSV file itself is not committed to this repository (see `.gitignore`) 
since it's a third-party dataset — download it directly from the Kaggle link 
above and place it at `java-etl/bank-etl/data/raw/` to reproduce the results.
