-- Account records. username is unique because registration rejects a taken name.
-- email is indexed for the forgot-username lookup. logincounter is stored but not enforced.
CREATE TABLE IF NOT EXISTS `user` (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    onlinestatus BOOLEAN NOT NULL DEFAULT FALSE,
    logincounter INT NOT NULL DEFAULT 0,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_username (username),
    KEY idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
