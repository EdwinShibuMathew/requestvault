CREATE TABLE IF NOT EXISTS data_requests (
                                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                             customer_id TEXT NOT NULL,
                                             from_date DATE NOT NULL,
                                             to_date DATE NOT NULL,
                                             CONSTRAINT valid_date_range CHECK (to_date >= from_date),
    CONSTRAINT nonblank_customer_id CHECK (length(trim(customer_id)) > 0)
    );