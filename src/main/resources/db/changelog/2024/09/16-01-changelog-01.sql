CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS transactions_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE client
(
    client_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    middle_name VARCHAR(255)
);

CREATE TABLE account
(
    account_id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    account_type VARCHAR(30),
    balance DECIMAL(15,2) NOT NULL,
    status VARCHAR(30),
    frozen_amount DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

CREATE TABLE transactions
(
    transaction_id SERIAL PRIMARY KEY,
    account_id INT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL DEFAULT NOW(),
    status VARCHAR(30),
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
);

CREATE TABLE data_source_error_log
(
    id SERIAL PRIMARY KEY,
    stack_trace VARCHAR(10000),
    message VARCHAR(255),
    method_signature VARCHAR(255)
);

