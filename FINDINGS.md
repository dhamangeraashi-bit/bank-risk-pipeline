# Findings

Results from running the full pipeline against the real 50,000-row dataset 
(495 unique accounts). Full query details are in `sql/analysis_queries.sql`.

## 1. Debit transactions carry meaningfully more risk than Credit
4.22% of Debit transactions were flagged as suspicious, compared to 2.28% 
for Credit — almost double. This tracks with how the scoring rules are 
weighted, but the gap held consistently across nearly 39,000 Debit 
transactions, so it's not just noise from a small sample.

| Transaction Type | Total Txns | Suspicious | Rate |
|---|---|---|---|
| Debit  | 38,747 | 1,635 | 4.22% |
| Credit | 11,253 |   257 | 2.28% |

## 2. The top-risk transactions all cluster around one profile
Every transaction in the top 10 highest-risk list was Online + Debit, in the 
$1,000–$1,900 range, scoring exactly 65 points. That uniformity is itself a 
finding: the current rule set converges on a single transaction profile 
rather than surfacing varied risk patterns. A more mature version would need 
additional signals — like deviation from a customer's own typical spend — to 
differentiate risk within that cluster.

## 3. A small number of accounts account for a disproportionate share of flags
The top 10 riskiest customers each had between 28 and 49 flagged 
transactions.

| Customer ID | Suspicious Transactions |
|---|---|
| AC00303 | 49 |
| AC00098 | 44 |
| AC00460 | 39 |
| AC00265 | 37 |
| AC00179 | 37 |

**Important caveat:** this is likely inflated, not purely behavioral. About 
87% of this dataset's timestamps have no time component and were defaulted 
to midnight during parsing, which triggers the model's late-night risk rule. 
A meaningful chunk of these "repeat offender" counts is probably an artifact 
of that default rather than genuinely risky behavior. See `DATASET.md` for 
details, and `README.md` → Known Limitations.

## What this suggests for next steps
- Separate the late-night rule from records with no real timestamp, so 
  date-only rows don't get an automatic risk bump they didn't earn
- Add a per-customer baseline (e.g. compare each transaction to that 
  customer's own historical average) to break up the uniform top-risk cluster
- Validate thresholds against labeled fraud outcomes if such data ever 
  becomes available, rather than hand-tuning against amount distribution alone

  
