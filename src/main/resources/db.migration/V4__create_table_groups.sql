CREATE TABLE IF NOT EXISTS groups
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT,
    sub_category_id BIGINT,
    capacity INT,
    leader_id BIGINT,
    is_online BOOLEAN,
    status VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories(id)
);