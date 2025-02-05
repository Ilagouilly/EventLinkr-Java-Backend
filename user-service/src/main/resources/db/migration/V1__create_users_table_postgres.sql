-- Create users table with specified fields
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,  -- No DEFAULT here, will be handled in INSERT statement
    username VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(255),
    headline VARCHAR(255),
    profile_link VARCHAR(255),
    headshot VARCHAR(255),
    status VARCHAR(20),
    provider VARCHAR(50),
    provider_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    guest_expiration TIMESTAMP,
    UNIQUE(provider, provider_id)
);

-- Indexes for quick lookups on email and username
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
