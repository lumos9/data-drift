-- Drop table if it exists
DROP TABLE IF EXISTS user_data;

-- Create table for the given dataset
CREATE TABLE user_data (
    id BIGINT PRIMARY KEY NOT NULL,
    name VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    email VARCHAR(255),
    phone VARCHAR(20),
    city VARCHAR(100),
    state VARCHAR(50),
    country VARCHAR(50),
    occupation VARCHAR(100)
);

-- Optional: Truncate the table to remove all rows if needed
-- TRUNCATE TABLE user_data;

DROP TABLE IF EXISTS coastal_data;

CREATE TABLE coastal_data (
    id BIGSERIAL PRIMARY KEY,
    domain VARCHAR(50),
    station VARCHAR(50),
    name VARCHAR(100),
    sensorcode INT,
    method VARCHAR(20),
    aspect VARCHAR(20),
    start TIMESTAMP,
    timestamp TIMESTAMP NOT NULL,
    value DOUBLE PRECISION,
    error VARCHAR(255),
    qc_flags VARCHAR(255)
);

-- Optional: Truncate the table to remove all rows if needed
-- TRUNCATE TABLE coastal_data;
