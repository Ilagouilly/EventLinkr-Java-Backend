CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT RANDOM_UUID(),
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
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    guest_expiration TIMESTAMP WITH TIME ZONE,
    avatar_url VARCHAR(255),
    bio TEXT,
    email_verified BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP,
    login_attempts INTEGER DEFAULT 0,
    last_failed_login TIMESTAMP,
    UNIQUE(provider, provider_id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);