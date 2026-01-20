CREATE TABLE if NOT EXISTS social_auths (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    provider VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (provider, provider_id)
)