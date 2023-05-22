CREATE TABLE IF NOT EXISTS app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT app_user_username_unique UNIQUE (username),
    CONSTRAINT app_user_email_unique UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS file (
    id BIGSERIAL PRIMARY KEY,
    path TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    app_user_id BIGINT,
    CONSTRAINT fk_app_user_id
        FOREIGN KEY(app_user_id)
            REFERENCES app_user(id)
)