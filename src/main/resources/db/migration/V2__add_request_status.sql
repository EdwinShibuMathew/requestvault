ALTER TABLE data_requests
ADD COLUMN status TEXT NOT NULL DEFAULT 'PENDING';

ALTER TABLE data_requests
ADD CONSTRAINT valid_request_status
CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));