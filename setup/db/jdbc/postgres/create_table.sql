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