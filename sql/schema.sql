DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id     VARCHAR(50) PRIMARY KEY
);

CREATE TABLE transactions (
    transaction_id    VARCHAR(50) PRIMARY KEY,
    customer_id       VARCHAR(50) REFERENCES customers(customer_id),
    amount            NUMERIC(12,2) NOT NULL,
    transaction_type  VARCHAR(30),
    channel           VARCHAR(30),
    transaction_date  TIMESTAMP NOT NULL,
    risk_score        NUMERIC(6,2),
    suspicious        BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_txn_customer ON transactions(customer_id);
CREATE INDEX idx_txn_date ON transactions(transaction_date);