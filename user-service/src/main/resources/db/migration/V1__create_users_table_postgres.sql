-- Create users table with specified fields
CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY, -- Auto-generate UUID
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
    avatar_url VARCHAR(255),
    bio TEXT,
    email_verified BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP,
    login_attempts INTEGER DEFAULT 0,
    last_failed_login TIMESTAMP,
    UNIQUE(provider, provider_id)
);

-- Indexes for quick lookups on email and username
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
