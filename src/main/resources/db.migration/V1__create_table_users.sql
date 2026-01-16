CREATE TABLE IF NOT EXISTS users
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    major VARCHAR(255),
    student_number VARCHAR(255) UNIQUE,
    nickname VARCHAR(255) UNIQUE,
    is_verified BOOLEAN,
    profile_image_url VARCHAR(255),
    bio TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);