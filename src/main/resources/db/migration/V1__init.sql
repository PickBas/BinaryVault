CREATE TABLE IF NOT EXISTS app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password TEXT NOT NULL,
    CONSTRAINT app_user_username_unique UNIQUE (username),
    CONSTRAINT appuser_email_unique UNIQUE (email)
);