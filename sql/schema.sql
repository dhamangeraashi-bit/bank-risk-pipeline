CREATE TABLE accounts (
    account_id VARCHAR(50) PRIMARY KEY,
    customer_age INT,
    account_type VARCHAR(30)
);

CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    account_id VARCHAR(50) REFERENCES accounts(account_id),
    txn_timestamp TIMESTAMP NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    merchant_category VARCHAR(50),
    channel VARCHAR(20),         -- ATM, online, branch
    location VARCHAR(50),
    is_fraud BOOLEAN DEFAULT FALSE,
    txn_hour INT,                 -- derived
    is_weekend BOOLEAN            -- derived
);

CREATE INDEX idx_txn_account ON transactions(account_id);
CREATE INDEX idx_txn_timestamp ON transactions(txn_timestamp);