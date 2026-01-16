CREATE TABLE IF NOT EXISTS user_study_groups
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (group_id) REFERENCES study_groups(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (group_id, user_id)
);