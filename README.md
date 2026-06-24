See [DATASET.md](DATASET.md) for dataset details and [FINDINGS.md](FINDINGS.md) for full results.

## Evidence / Screenshots

### Full pipeline run — real 50,000-row dataset

**Confirming all 50,000 transactions and 495 unique accounts loaded into PostgreSQL:**
![Real dataset counts](screenshots/01-real-dataset-counts.png)

**Suspicious rate by transaction type (Debit vs Credit):**
![Suspicious by type](screenshots/02-suspicious-by-type.png)

**Top 10 highest-risk transactions:**
![Top risk transactions](screenshots/03-top-risk-transactions.png)

**Top 10 accounts by number of flagged transactions:**
![Repeat offenders](screenshots/04-repeat-offenders.png)

### Logic validation — small hand-built test dataset

Before running against the full dataset, the scoring logic was validated 
against a small hand-built sample (10 transactions across 6 customers) with 
deliberately varied amounts, types, and timing — used to confirm the rules 
for large amounts, structuring-range amounts, and transaction type were each 
firing correctly before scaling up.

![Test data validation](screenshots/05-small-test-data-validation.png)
