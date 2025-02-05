CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
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
    UNIQUE(provider, provider_id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);