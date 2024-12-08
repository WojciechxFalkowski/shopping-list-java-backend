-- V1.0.6__create_receipt_and_receipt_item_tables.sql

-- Tworzenie tabeli receipt
CREATE TABLE receipt
(
    id                      UUID PRIMARY KEY,
    store_name              VARCHAR(100)                        NOT NULL,
    store_street            VARCHAR(100)                        NOT NULL,
    store_building_number   VARCHAR(20)                         NOT NULL,
    store_postal_code       VARCHAR(10)                         NOT NULL,
    store_city              VARCHAR(50)                         NOT NULL,
    store_country           VARCHAR(50)                         NOT NULL,
    nip                     VARCHAR(20)                         NOT NULL,
    merchant_company_reg_no VARCHAR(20),
    receipt_number          VARCHAR(50)                         NOT NULL UNIQUE,
    date                    DATE                                NOT NULL,
    time                    TIME                                NOT NULL,
    currency                VARCHAR(10)                         NOT NULL,
    total                   DOUBLE PRECISION                    NOT NULL,
    notes                   VARCHAR(255),
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Tworzenie tabeli receipt_item
CREATE TABLE receipt_item
(
    id         UUID PRIMARY KEY,
    receipt_id UUID                                NOT NULL REFERENCES receipt (id) ON DELETE CASCADE,
    name       VARCHAR(100)                        NOT NULL,
    quantity   DOUBLE PRECISION                    NOT NULL,
    unit_price DOUBLE PRECISION                    NOT NULL,
    category   VARCHAR(50)                         NOT NULL,
    remarks    VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
