-- Account records. username and email are unique because registration rejects a taken name or address.
-- logincounter is stored but not enforced.
-- password, verification_code, and reset_code store salted PBKDF2 hashes.
CREATE TABLE IF NOT EXISTS `user` (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    onlinestatus BOOLEAN NOT NULL DEFAULT FALSE,
    logincounter INT NOT NULL DEFAULT 0,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(255) NOT NULL,
    verification_code VARCHAR(255) NULL,
    reset_code VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_username (username),
    UNIQUE KEY uq_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
